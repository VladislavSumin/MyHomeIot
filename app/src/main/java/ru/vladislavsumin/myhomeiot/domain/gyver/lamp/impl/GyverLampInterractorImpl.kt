package ru.vladislavsumin.myhomeiot.domain.gyver.lamp.impl

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.Observables
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampInterractor
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampManager
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampProtocol
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnection
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnectionFactory
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnectionState
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampState
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

    override fun observeChangeEnabledState(setEnabled: Boolean): Single<GyverLampState> {
        return if (setEnabled) observeTurnOn() else observeTurnOff()
    }

    override fun observeTurnOff(): Single<GyverLampState> {
        return observeRequest(mGyverLampProtocol.getOffRequest())
    }

    override fun observeTurnOn(): Single<GyverLampState> {
        return observeRequest(mGyverLampProtocol.getOnRequest())
    }

    private fun observeRequest(request: String): Single<GyverLampState> {
        return mConnectionObservable
            .map { Pair(it, it.observeConnectionStatus()) }
            .flatMapSingle { pair ->
                pair.second
                    .filter { it != GyverLampConnectionState.LOADING }
                    .firstOrError()
                    .map { pair.first }
            }
            .firstOrError()
            .flatMap {
                it.addRequest(request)
            }
    }
}