package ru.vladislavsumin.myhomeiot.domain.gyver.lamp.impl

import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampInterractor
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampManager
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnection
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnectionFactory
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnectionState
import ru.vladislavsumin.myhomeiot.network.NetworkConnectivityManager
import ru.vladislavsumin.myhomeiot.utils.subscribeOnIo

class GyverLampInterractorImpl(
    gyverLampId: Long,
    mGyverLampManager: GyverLampManager,
    mNetworkConnectivityManager: NetworkConnectivityManager,
    mGyverLampConnectionFactory: GyverLampConnectionFactory
) : GyverLampInterractor {

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
                //TODO
                if (connectivityStatus) {
                    Observable.just(mGyverLampConnectionFactory.create())
                } else {
                    Observable.just(mGyverLampConnectionFactory.createNetworkUnavailableSate())
                }
            }
            .subscribeOnIo()
            .replay(1)
            .refCount()

    override fun observeConnectionState(): Observable<GyverLampConnectionState> {
        return mConnectionObservable
            .switchMap { it.observeConnectionStatus() }
    }
}