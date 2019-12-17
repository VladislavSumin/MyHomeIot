package ru.vladislavsumin.myhomeiot.domain.gyver.lamp

import io.reactivex.Observable
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnectionState
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampState

interface GyverLampInterractor {
    fun observeConnectionState(): Observable<Pair<GyverLampConnectionState, GyverLampState?>>
}