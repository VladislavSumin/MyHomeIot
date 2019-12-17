package ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection

import io.reactivex.Observable


interface GyverLampConnection {
    fun observeConnectionStatus(): Observable<GyverLampConnectionState>
    fun observeConnection(): Observable<Pair<GyverLampConnectionState, GyverLampState?>>
}