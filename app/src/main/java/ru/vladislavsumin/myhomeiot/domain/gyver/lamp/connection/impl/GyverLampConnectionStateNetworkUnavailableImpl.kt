package ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.impl

import io.reactivex.Completable
import io.reactivex.Observable
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnection
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnectionState
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampState

class GyverLampConnectionStateNetworkUnavailableImpl : GyverLampConnectionAbstract() {
    override fun observeConnection(): Observable<Pair<GyverLampConnectionState, GyverLampState?>> {
        return Observable.just(Pair(GyverLampConnectionState.NETWORK_UNAVAILABLE, null))
    }

    override fun addRequest(request: String): Completable {
        return Completable.error(GyverLampConnection.CannotConnectException())
    }
}