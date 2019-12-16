package ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.impl

import android.util.Log
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.SingleEmitter
import io.reactivex.schedulers.Schedulers
import ru.vladislavsumin.myhomeiot.database.entity.GyverLampEntity
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampProtocol
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnection
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnectionState
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampState
import ru.vladislavsumin.myhomeiot.network.SocketProvider
import ru.vladislavsumin.myhomeiot.utils.tag
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class GyverLampConnectionStateImpl(
    private val mSocketProvider: SocketProvider,
    private val mGyverLampProtocol: GyverLampProtocol,
    private val mGyverLampEntity: GyverLampEntity
) : GyverLampConnection {

    companion object {
        private val TAG = tag<GyverLampConnectionStateImpl>()

        private const val DEFAULT_RECEIVE_PACKAGE_SIZE = 1000
        private const val DEFAULT_TIMEOUT = 4000
        private const val DEFAULT_RECONNECT_TIMEOUT = 4000L
        private const val PING_INTERVAL = 1500L
    }

    private var mLastPingCheck = 0L
    private val mMessageQueue: Queue<Pair<SingleEmitter<GyverLampState>, DatagramPacket>> =
        ArrayDeque()
    @Volatile
    private var mIsRunning = false
    private val mLock = ReentrantLock()
    private val mQueueNotEmpty = mLock.newCondition()

    private lateinit var mSocket: DatagramSocket
    private val mPacket = DatagramPacket(
        ByteArray(DEFAULT_RECEIVE_PACKAGE_SIZE),
        DEFAULT_RECEIVE_PACKAGE_SIZE
    )

    private val mConnectionObservable: Observable<Pair<GyverLampConnectionState, GyverLampState?>> =
        Observable.create<Pair<GyverLampConnectionState, GyverLampState?>> { emitter ->
            mLock.withLock {
                if (mIsRunning) throw AlreadyRunningException()
                mIsRunning = true
                mLastPingCheck = 0
            }
            Log.d(TAG, "onSubscribe()")

            emitter.setCancellable(this::onDispose)

            connectionLoop(emitter)
        }
            .subscribeOn(Schedulers.newThread())
            .startWith(Pair(GyverLampConnectionState.DISCONNECTED, null))
            .replay(1)
            .refCount()

    private fun onDispose() {
        Log.d(TAG, "onDispose()")
        mLock.withLock {
            mIsRunning = false
            mSocket.close()
            Log.d(TAG, "onDispose() close port")
            mQueueNotEmpty.signal()
        }
    }

    private fun connectionLoop(emitter: ObservableEmitter<Pair<GyverLampConnectionState, GyverLampState?>>) {
        setupSocket(emitter)
        while (mIsRunning && !emitter.isDisposed) {
            val request = getRequest(emitter) ?: continue

            if (request.first == null || !request.first!!.isDisposed) {
                try {
                    val datagramPacket = request.second
                    datagramPacket.address = InetAddress.getByName(mGyverLampEntity.host)
                    datagramPacket.port = mGyverLampEntity.port
                    Log.d(TAG, "socket work")
                    mSocket.send(datagramPacket)

                    mSocket.receive(mPacket)
                    //TODO check received ip

                    mLock.withLock {
                        if (mIsRunning && !emitter.isDisposed)
                            mLastPingCheck = System.currentTimeMillis()
                    }

                    Log.d(TAG, "socket end work")
                    emitter.onNext(
                        Pair(
                            GyverLampConnectionState.CONNECTED,
                            null //TODO add response parse
                        )
                    )
                } catch (e: Exception) {
                    Log.d(TAG, "socket error")
                    mSocket.close()
                    if (!emitter.isDisposed) {
                        emitter.onNext(Pair(GyverLampConnectionState.DISCONNECTED, null))
                    }
                    setupSocket(emitter)
                }
            }
        }
        Log.d(TAG, "Connection closed")
    }

    private fun getRequest(emitter: ObservableEmitter<*>):
            Pair<SingleEmitter<GyverLampState>?, DatagramPacket>? {
        mLock.withLock {
            return if (!mIsRunning || emitter.isDisposed) {
                null
            } else if (mMessageQueue.isNotEmpty()) {
                mMessageQueue.poll()!!
            } else if (System.currentTimeMillis() - mLastPingCheck > PING_INTERVAL) {
                getPingRequest()
            } else {
                while (mIsRunning && !emitter.isDisposed && mMessageQueue.isEmpty() &&
                    mLastPingCheck + PING_INTERVAL >= System.currentTimeMillis()
                ) {
                    mQueueNotEmpty.await(
                        PING_INTERVAL - (System.currentTimeMillis() - mLastPingCheck),
                        TimeUnit.MILLISECONDS
                    )
                    Log.d(TAG, "QueryLoop()")
                }

                if (!mIsRunning || emitter.isDisposed)
                    null
                else if (mMessageQueue.isNotEmpty()) {
                    mMessageQueue.poll()!!
                } else {
                    getPingRequest()
                }
            }
        }
    }

    private fun getPingRequest(): Pair<SingleEmitter<GyverLampState>?, DatagramPacket> {
        return Pair(null, mGyverLampProtocol.getRequest().toDatagramPacket())
    }

    private fun setupSocket(emitter: ObservableEmitter<*>) {
        mLock.withLock {
            if (mIsRunning && !emitter.isDisposed) {
                mSocket = mSocketProvider.createDatagramSocket()
                mSocket.soTimeout = DEFAULT_TIMEOUT
            }
        }
    }

    //
//    private val mConnectionObservable: Observable<Pair<GyverLampConnectionState, Unit?>> =
//        createConnectionObservable()
//            .subscribeOnIo()
//            .startWith(Pair(GyverLampConnectionState.DISCONNECTED, null))
//            .replay(1)
//            .refCount()
//
//    private fun createConnectionObservable(): Observable<Pair<GyverLampConnectionState, Unit?>> {
//        return Observable.create { emitter ->
//            val latch = CountDownLatch(1)
//            emitter.setCancellable { latch.countDown() }
//
//            while (!emitter.isDisposed) {
//                try {
//                    mSocket = mSocketProvider.createDatagramSocket()
//                    mSocket.soTimeout = DEFAULT_TIMEOUT
//                    connectionLoop(emitter)
//                } catch (e: SocketTimeoutException) {
//                    emitter.onNext(Pair(GyverLampConnectionState.DISCONNECTED, null))
//                } finally {
//                    mSocket.close()
//                }
//                sleep(System.currentTimeMillis() + DEFAULT_RECONNECT_TIMEOUT, emitter)
//            }
//        }
//    }
//
//    private fun connectionLoop(emitter: ObservableEmitter<Pair<GyverLampConnectionState, Unit?>>) {
//        while (!emitter.isDisposed) {
//            val millis = System.currentTimeMillis()
//            if (millis - mLastPingCheck > PING_INTERVAL) {
//                val packet = mGyverLampProtocol.getRequest().toDatagramPacket()
//                packet.port = mGyverLampEntity.port
//                packet.address = InetAddress.getByName(mGyverLampEntity.host)
//                mSocket.send(packet)
//            } else {
//                sleep(PING_INTERVAL + mLastPingCheck, emitter)
//                continue
//            }
//            mSocket.receive(mPacket)
//            mLastPingCheck = System.currentTimeMillis()
//            emitter.onNext(Pair(GyverLampConnectionState.CONNECTED, null))
//        }
//    }
//
    override fun observeConnectionStatus(): Observable<GyverLampConnectionState> {
//        return TODO()
        return mConnectionObservable.map { it.first }
    }

    private fun String.toDatagramPacket(): DatagramPacket {
        return mGyverLampProtocol.stringToDatagramPacket(this)
    }
//
//    private fun sleep(until: Long, emitter: ObservableEmitter<*>) {
//        while (System.currentTimeMillis() < until)
//            try {
//                Thread.sleep(until - System.currentTimeMillis())
//            } catch (e: InterruptedException) {
//                if (emitter.isDisposed) return
//            }
//    }

    private class AlreadyRunningException : RuntimeException()
}