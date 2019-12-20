package ru.vladislavsumin.myhomeiot.domain.gyver.lamp.impl

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampInterractor
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampManager
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampProtocol
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnection
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnectionFactory
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnectionState
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampState
import ru.vladislavsumin.myhomeiot.network.NetworkConnectivityManager
import ru.vladislavsumin.myhomeiot.utils.subscribeOnIo
import ru.vladislavsumin.myhomeiot.utils.tag

class GyverLampInterractorImpl(
    gyverLampId: Long,
    mGyverLampManager: GyverLampManager,
    mNetworkConnectivityManager: NetworkConnectivityManager,
    mGyverLampConnectionFactory: GyverLampConnectionFactory,
    private val mGyverLampProtocol: GyverLampProtocol
) : GyverLampInterractor {
    companion object {
        private val TAG = tag<GyverLampInterractor>()
    }

    private val mConnectionObservable: Observable<GyverLampConnection> =
        Observables
            .combineLatest(
                //TODO тут может быть ошибка если в бд нет такого id
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

    override fun observeConnectionState(): Observable<Pair<GyverLampConnectionState, GyverLampState?>> {
        return mConnectionObservable
            .switchMap { it.observeConnection() }
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