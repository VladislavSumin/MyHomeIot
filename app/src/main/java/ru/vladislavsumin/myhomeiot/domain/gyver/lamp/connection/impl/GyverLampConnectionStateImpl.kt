package ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.impl

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import ru.vladislavsumin.myhomeiot.database.entity.GyverLampEntity
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampProtocol
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnection
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnectionState
import ru.vladislavsumin.myhomeiot.network.SocketProvider
import ru.vladislavsumin.myhomeiot.utils.subscribeOnIo
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class GyverLampConnectionStateImpl(
    private val mSocketProvider: SocketProvider,
    private val mGyverLampProtocol: GyverLampProtocol,
    private val mGyverLampEntity: GyverLampEntity
) : GyverLampConnection {
    companion object {
        private const val DEFAULT_RECEIVE_PACKAGE_SIZE = 1000
        private const val DEFAULT_TIMEOUT = 4000
        private const val DEFAULT_RECONNECT_TIMEOUT = 4000L
        private const val PING_INTERVAL = 1500L
    }

    private var mLastPingCheck = 0L
    private lateinit var mSocket: DatagramSocket
    private val mPacket = DatagramPacket(
        ByteArray(DEFAULT_RECEIVE_PACKAGE_SIZE),
        DEFAULT_RECEIVE_PACKAGE_SIZE
    )

    private val mConnectionObservable: Observable<Pair<GyverLampConnectionState, Unit?>> =
        createConnectionObservable()
            .subscribeOnIo()
            .startWith(Pair(GyverLampConnectionState.DISCONNECTED, null))
            .replay(1)
            .refCount()

    private fun createConnectionObservable(): Observable<Pair<GyverLampConnectionState, Unit?>> {
        return Observable.create { emitter ->
            while (!emitter.isDisposed) {
                try {
                    mSocket = mSocketProvider.createDatagramSocket()
                    mSocket.soTimeout = DEFAULT_TIMEOUT
                    connectionLoop(emitter)
                } catch (e: SocketTimeoutException) {
                    emitter.onNext(Pair(GyverLampConnectionState.DISCONNECTED, null))
                } finally {
                    mSocket.close()
                }
                sleep(System.currentTimeMillis() + DEFAULT_RECONNECT_TIMEOUT, emitter)
            }
        }
    }

    private fun connectionLoop(emitter: ObservableEmitter<Pair<GyverLampConnectionState, Unit?>>) {
        while (!emitter.isDisposed) {
            val millis = System.currentTimeMillis()
            if (millis - mLastPingCheck > PING_INTERVAL) {
                val packet = mGyverLampProtocol.getRequest().toDatagramPacket()
                packet.port = mGyverLampEntity.port
                packet.address = InetAddress.getByName(mGyverLampEntity.host)
                mSocket.send(packet)
            } else {
                sleep(PING_INTERVAL + mLastPingCheck, emitter)
                continue
            }
            mSocket.receive(mPacket)
            mLastPingCheck = System.currentTimeMillis()
            emitter.onNext(Pair(GyverLampConnectionState.CONNECTED, null))
        }
    }

    override fun observeConnectionStatus(): Observable<GyverLampConnectionState> {
        return mConnectionObservable.map { it.first }
    }

    private fun String.toDatagramPacket(): DatagramPacket {
        return mGyverLampProtocol.stringToDatagramPacket(this)
    }

    private fun sleep(until: Long, emitter: ObservableEmitter<*>) {
        while (System.currentTimeMillis() < until)
            try {
                Thread.sleep(until - System.currentTimeMillis())
            } catch (e: InterruptedException) {
                if (emitter.isDisposed) return
            }
    }
}