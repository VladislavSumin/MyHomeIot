package ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.impl

import io.reactivex.Observable
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnection
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnectionState

class GyverLampConnectionStateImpl : GyverLampConnection {
    override fun observeConnectionStatus(): Observable<GyverLampConnectionState> {
        //TODO realize it
        return Observable.just(GyverLampConnectionState.DISCONNECTED)
    }
}