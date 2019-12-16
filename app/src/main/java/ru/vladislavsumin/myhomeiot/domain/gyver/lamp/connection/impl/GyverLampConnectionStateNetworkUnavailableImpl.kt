package ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.impl

import io.reactivex.Observable
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnection
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnectionState

class GyverLampConnectionStateNetworkUnavailableImpl : GyverLampConnection {
    override fun observeConnectionStatus(): Observable<GyverLampConnectionState> {
        return Observable.just(GyverLampConnectionState.NETWORK_UNAVAILABLE)
    }
}