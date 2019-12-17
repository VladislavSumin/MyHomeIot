package ru.vladislavsumin.myhomeiot.domain.gyver.lamp

import io.reactivex.Observable
import io.reactivex.Single
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnectionState
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampState

interface GyverLampInterractor {
    fun observeConnectionState(): Observable<Pair<GyverLampConnectionState, GyverLampState?>>

    fun observeChangeEnabledState(setEnabled: Boolean): Single<GyverLampState>
    fun observeTurnOff(): Single<GyverLampState>
    fun observeTurnOn(): Single<GyverLampState>
}