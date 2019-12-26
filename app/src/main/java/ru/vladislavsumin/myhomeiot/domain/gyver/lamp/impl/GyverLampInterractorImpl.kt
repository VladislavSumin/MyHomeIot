package ru.vladislavsumin.myhomeiot.domain.gyver.lamp.impl

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.*
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnection
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnectionFactory
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnectionState
import ru.vladislavsumin.myhomeiot.network.NetworkConnectivityManager
import ru.vladislavsumin.myhomeiot.utils.subscribeOnIo
import ru.vladislavsumin.myhomeiot.utils.tag
import java.util.concurrent.TimeUnit

class GyverLampInterractorImpl(
    gyverLampId: Long,
    mGyverLampManager: GyverLampManager,
    mNetworkConnectivityManager: NetworkConnectivityManager,
    mGyverLampConnectionFactory: GyverLampConnectionFactory,
    private val mGyverLampProtocol: GyverLampProtocol
) : GyverLampInterractor {
    companion object {
        private val TAG = tag<GyverLampInterractor>()
        private const val CONNECTION_CLOSE_TIMEOUT = 10L //seconds

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
}