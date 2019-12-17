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
import java.lang.Exception
import java.net.*
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class GyverLampConnectionStateImpl(
    private val mSocketProvider: SocketProvider,
    private val mGyverLampProtocol: GyverLampProtocol,
    private val mGyverLampEntity: GyverLampEntity
) : GyverLampConnectionAbstract() {

    companion object {
        private val TAG = tag<GyverLampConnectionStateImpl>()

        private const val DEFAULT_RECEIVE_PACKAGE_SIZE = 1000
        private const val DEFAULT_TIMEOUT = 4000
        //TODO не плохо бы это реализовать, а то если нет сети будет активное ожидание
        private const val DEFAULT_RECONNECT_TIMEOUT = 4000L
        private const val PING_INTERVAL = 1500L
    }

    private var mLastPingCheck = 0L
    private val mMessageQueue: Queue<Pair<SingleEmitter<GyverLampState>, String>> =
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
                    //TODO надо добавить повторную отправку UDP как никак
                    //TODO Еще бы адаптивную задержку сделать
                    request.second.toDatagramPacket(mPacket)
                    mPacket.address = InetAddress.getByName(mGyverLampEntity.host)
                    mPacket.port = mGyverLampEntity.port
                    Log.d(TAG, "socket work")
                    mSocket.send(mPacket)

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
                            parseResponse(mPacket)
                        )
                    )
                } catch (e: Exception) {
                    when (e) {
                        is SocketException,
                        is SocketTimeoutException -> {
                            Log.d(TAG, "socket error")
                            mSocket.close()
                            if (!emitter.isDisposed) {
                                emitter.onNext(Pair(GyverLampConnectionState.DISCONNECTED, null))
                            }
                            setupSocket(emitter)
                        }
                        else -> throw e
                    }
                }
            }
        }
        Log.d(TAG, "Connection closed")
    }

    private fun getRequest(emitter: ObservableEmitter<*>):
            Pair<SingleEmitter<GyverLampState>?, String>? {
        mLock.withLock {
            //Wait loop
            while (mIsRunning && !emitter.isDisposed && mMessageQueue.isEmpty() &&
                (System.currentTimeMillis() - mLastPingCheck < PING_INTERVAL)
            ) {
                Log.d(TAG, "QueryLoop()")

                mQueueNotEmpty.await(
                    mLastPingCheck + PING_INTERVAL - System.currentTimeMillis(),
                    TimeUnit.MILLISECONDS
                )
            }

            if (!mIsRunning || emitter.isDisposed) return null
            if (mMessageQueue.isNotEmpty()) return mMessageQueue.poll()

            return getPingRequest()
        }
    }

    private fun parseResponse(packet: DatagramPacket): GyverLampState? {
        val data = packet.getStringData()
        return mGyverLampProtocol.parseCurrentStateResponse(data)
    }

    private fun getPingRequest(): Pair<SingleEmitter<GyverLampState>?, String> {
        return Pair(null, mGyverLampProtocol.getCurrentStateRequest())
    }

    private fun setupSocket(emitter: ObservableEmitter<*>) {
        mLock.withLock {
            if (mIsRunning && !emitter.isDisposed) {
                mSocket = mSocketProvider.createDatagramSocket()
                mSocket.soTimeout = DEFAULT_TIMEOUT
            }
        }
    }

    override fun observeConnection(): Observable<Pair<GyverLampConnectionState, GyverLampState?>> {
        return mConnectionObservable
    }

    private fun DatagramPacket.getStringData(): String {
        return mGyverLampProtocol.datagramPacketToString(this)
    }

    private fun String.toDatagramPacket(packet: DatagramPacket? = null): DatagramPacket {
        return mGyverLampProtocol.stringToDatagramPacket(this, packet)
    }

    private class AlreadyRunningException : RuntimeException()
}