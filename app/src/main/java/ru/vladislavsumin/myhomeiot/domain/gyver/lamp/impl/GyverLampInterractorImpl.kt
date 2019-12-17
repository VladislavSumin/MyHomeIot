package ru.vladislavsumin.myhomeiot.domain.gyver.lamp.impl

import android.util.Log
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampInterractor
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampManager
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
    mGyverLampConnectionFactory: GyverLampConnectionFactory
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
}