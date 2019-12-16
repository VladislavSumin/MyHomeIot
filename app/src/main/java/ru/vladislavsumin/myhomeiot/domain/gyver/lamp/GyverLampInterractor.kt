package ru.vladislavsumin.myhomeiot.domain.gyver.lamp

import io.reactivex.Observable
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnectionState

interface GyverLampInterractor {
    fun observeConnectionState(): Observable<GyverLampConnectionState>
}