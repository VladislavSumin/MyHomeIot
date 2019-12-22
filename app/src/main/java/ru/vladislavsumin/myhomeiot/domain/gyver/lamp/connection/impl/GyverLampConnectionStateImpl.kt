package ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.impl

import android.util.Log
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import ru.vladislavsumin.myhomeiot.database.entity.GyverLampEntity
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampProtocol
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnection
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnectionState
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampState
import ru.vladislavsumin.myhomeiot.network.SocketProvider
import ru.vladislavsumin.myhomeiot.utils.subscribeOnIo
import ru.vladislavsumin.myhomeiot.utils.tag
import java.io.IOException
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
        private const val DEFAULT_RECONNECT_TIMEOUT = 2500L
        private const val PING_INTERVAL = 1500L
    }

    private var mLastPingCheck = 0L
    private val mMessageQueue: Queue<Pair<CompletableEmitter, String>> =
        ArrayDeque()
    @Volatile
    private var mIsRunning = false
    private val mLock = ReentrantLock()
    private val mQueueNotEmpty = mLock.newCondition()
    private var mLastResponse: GyverLampState? = null

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
//            mQueueNotEmpty.signal()
            clearQuery()
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
                    val requestPacket = request.second.toDatagramPacket()
                    requestPacket.address = InetAddress.getByName(mGyverLampEntity.host)
                    requestPacket.port = mGyverLampEntity.port
                    Log.d(TAG, "socket work")
                    mSocket.send(requestPacket)

                    mPacket.data.clear()
                    mSocket.receive(mPacket)
                    //TODO check received ip

                    val response = parseResponse(mPacket)

                    mLock.withLock {
                        if (mIsRunning && !emitter.isDisposed) {
                            mLastResponse = response
                            mLastPingCheck = if (response != null) {
                                System.currentTimeMillis()
                            } else {
                                0
                            }
                        }
                    }

                    Log.d(TAG, "socket end work")


                    request.first?.apply {
                        if (!isDisposed) onComplete()
                    }

                    emitter.onNext(
                        Pair(
                            GyverLampConnectionState.CONNECTED,
                            response
                        )
                    )
                } catch (e: Exception) {
                    request.first?.apply {
                        if (!isDisposed) onError(GyverLampConnection.ConnectionException())
                    }

                    clearQuery()

                    when (e) {
                        is IOException,
                        is SocketException,
                        is SocketTimeoutException -> {
                            Log.d(TAG, "socket error")
                            mSocket.close()
                            if (!emitter.isDisposed) {
                                emitter.onNext(Pair(GyverLampConnectionState.DISCONNECTED, null))
                            }

                            reconnectTimeout(emitter)
                            setupSocket(emitter)
                        }
                        else -> throw e
                    }
                }
            }
        }
        Log.d(TAG, "Connection closed")
    }

    private fun reconnectTimeout(emitter: ObservableEmitter<*>) {
        val startTime = System.currentTimeMillis()
        mLock.withLock {
            while (mIsRunning && !emitter.isDisposed && mMessageQueue.isEmpty() &&
                (System.currentTimeMillis() - startTime < DEFAULT_RECONNECT_TIMEOUT)
            ) {
                mQueueNotEmpty.await(
                    startTime + PING_INTERVAL - System.currentTimeMillis(),
                    TimeUnit.MILLISECONDS
                )

                if (mMessageQueue.isNotEmpty()) {
                    val request = mMessageQueue.poll()!!.first
                    if (!request.isDisposed) request.onError(GyverLampConnection.ConnectionException())
                }
            }
        }
    }

    private fun clearQuery() {
        mLock.withLock {
            mMessageQueue.forEach {
                if (!it.first.isDisposed)
                    it.first.onError(GyverLampConnection.ConnectionException())
            }
            mMessageQueue.clear()
            mQueueNotEmpty.signal()
        }
    }

    private fun getRequest(emitter: ObservableEmitter<*>):
            Pair<CompletableEmitter?, String>? {
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
        return mGyverLampProtocol.parseCurrentStateResponse(data, mLastResponse)
    }

    private fun getPingRequest(): Pair<CompletableEmitter?, String> {
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

    override fun addRequest(request: String): Completable {
        return Completable.create { emitter ->
            mLock.withLock {
                mMessageQueue.offer(Pair(emitter, request))
                mQueueNotEmpty.signal()
            }
            emitter.setDisposable(mConnectionObservable.subscribe())
        }.subscribeOnIo()
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

    private fun ByteArray.clear() {
        for (i in 0 until size) {
            this[i] = 0
        }
    }

    private class AlreadyRunningException : RuntimeException()
}