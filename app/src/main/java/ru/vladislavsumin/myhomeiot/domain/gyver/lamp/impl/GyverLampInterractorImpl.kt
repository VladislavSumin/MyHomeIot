package ru.vladislavsumin.myhomeiot.domain.gyver.lamp.impl

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.Observables
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.*
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnection
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnectionFactory
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnectionState
import ru.vladislavsumin.myhomeiot.network.NetworkConnectivityManager
import ru.vladislavsumin.myhomeiot.network.SocketProvider
import ru.vladislavsumin.myhomeiot.utils.subscribeOnIo
import ru.vladislavsumin.myhomeiot.utils.subscribeOnNewThread
import ru.vladislavsumin.myhomeiot.utils.tag
import java.net.DatagramPacket
import java.net.InetAddress
import java.util.concurrent.TimeUnit

class GyverLampInterractorImpl(
    private val gyverLampId: Long,
    private val mGyverLampManager: GyverLampManager,
    mNetworkConnectivityManager: NetworkConnectivityManager,
    mGyverLampConnectionFactory: GyverLampConnectionFactory,
    private val mGyverLampProtocol: GyverLampProtocol,
    private val mSocketProvider: SocketProvider
) : GyverLampInterractor {
    companion object {
        private val TAG = tag<GyverLampInterractor>()
        private const val CONNECTION_CLOSE_TIMEOUT = 10L //seconds
        private const val DEFAULT_RECEIVE_PACKAGE_SIZE = 1000
    }

    private val mConnectionObservable: Observable<GyverLampConnection> =
        Observables
            .combineLatest(
                mGyverLampManager.observeLamp(gyverLampId).toObservable(),
                mNetworkConnectivityManager.observeNetworkConnected()
            ) { entity, connectivityStatus ->
                Pair(entity, connectivityStatus)
            }
            .switchMap { (gyverLampEntity, connectivityStatus) ->

                if (connectivityStatus) {
                    Observable.just(mGyverLampConnectionFactory.create(gyverLampEntity))
                } else {
                    Observable.just(mGyverLampConnectionFactory.createNetworkUnavailableSate())
                }
            }
            .subscribeOnIo()
            .startWith(mGyverLampConnectionFactory.createLoadingSate())
            .replay(1)
            .refCount()

    private val mConnectionStateObservable: Observable<Pair<GyverLampConnectionState, GyverLampState?>> =
        mConnectionObservable
            .switchMap { it.observeConnection() }
            .replay(1)
            .refCount(CONNECTION_CLOSE_TIMEOUT, TimeUnit.SECONDS)


    override fun observeConnectionState(): Observable<Pair<GyverLampConnectionState, GyverLampState?>> {
        return mConnectionStateObservable
    }

    override fun observeChangeEnabledState(setEnabled: Boolean): Completable {
        return if (setEnabled) observeTurnOn() else observeTurnOff()
    }

    override fun observeTurnOff(): Completable {
        return observeRequest(mGyverLampProtocol.getOffRequest())
    }

    override fun observeTurnOn(): Completable {
        return observeRequest(mGyverLampProtocol.getOnRequest())
    }

    override fun observeChangeBrightness(brightness: Int): Completable {
        return observeRequest(mGyverLampProtocol.getBrightnessRequest(brightness))
    }

    override fun observeChangeScale(scale: Int): Completable {
        return observeRequest(mGyverLampProtocol.getScaleRequest(scale))
    }

    override fun observeChangeSpeed(speed: Int): Completable {
        return observeRequest(mGyverLampProtocol.getSpeedRequest(speed))
    }

    override fun observeChangeMode(mode: GyverLampMode): Completable {
        return observeRequest(mGyverLampProtocol.getModeRequest(mode))
    }

    override fun getAlarms(): Single<Unit> {
        //TODO move retry && timeout куда нибудь
        return observeSingleEvent(mGyverLampProtocol.getAlarmRequest(), 3, 3000).map { Unit }
    }

    private fun observeSingleEvent(request: String, retry: Long, timeout: Int): Single<String> {
        return mGyverLampManager.observeLamp(gyverLampId).firstOrError()
            .flatMap { gyverLamp ->
                Single.create<String> { emitter ->
                    mSocketProvider.createDatagramSocket().use { socket ->
                        // Send hello package
                        val helloPacket = request.toDatagramPacket()
                        helloPacket.address = InetAddress.getByName(gyverLamp.host)
                        helloPacket.port = gyverLamp.port
                        socket.send(helloPacket)

                        // Wait response
                        val byteArray = ByteArray(DEFAULT_RECEIVE_PACKAGE_SIZE)
                        val datagramPacket = DatagramPacket(byteArray, byteArray.size)
                        socket.soTimeout = timeout
                        socket.receive(datagramPacket)

                        emitter.onSuccess(datagramPacket.getDataString())
                    }
                }
            }
            .subscribeOnNewThread()
            .retry(retry)
    }

    private fun observeRequest(request: String): Completable {
        return mConnectionObservable
            .map { Pair(it, it.observeConnectionStatus()) }
            .flatMapSingle { pair ->
                pair.second
                    .filter { it != GyverLampConnectionState.LOADING }
                    .firstOrError()
                    .map { pair.first }
            }
            .firstOrError()
            .flatMapCompletable {
                it.addRequest(request)
            }
    }

    private fun String.toDatagramPacket(): DatagramPacket {
        return mGyverLampProtocol.stringToDatagramPacket(this)
    }

    private fun DatagramPacket.getDataString(): String {
        return mGyverLampProtocol.datagramPacketToString(this)
    }
}