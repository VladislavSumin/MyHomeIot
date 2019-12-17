package ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.impl

import io.reactivex.Observable
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnection
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnectionState

abstract class GyverLampConnectionAbstract : GyverLampConnection {
    final override fun observeConnectionStatus(): Observable<GyverLampConnectionState> {
        return observeConnection().map { it.first }
    }
}