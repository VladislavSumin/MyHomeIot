package ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.impl

import io.reactivex.Completable
import io.reactivex.Observable
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnection
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnectionState
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampState

class GyverLampConnectionStateILoadingImpl : GyverLampConnectionAbstract() {
    override fun observeConnection(): Observable<Pair<GyverLampConnectionState, GyverLampState?>> {
        return Observable.just(Pair(GyverLampConnectionState.LOADING, null))
    }

    override fun addRequest(request: String): Completable {
        return Completable.error(GyverLampConnection.ConnectionException())
    }
}