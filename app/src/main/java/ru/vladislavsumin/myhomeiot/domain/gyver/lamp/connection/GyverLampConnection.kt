package ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection

import io.reactivex.Observable
import io.reactivex.Single
import java.lang.RuntimeException


interface GyverLampConnection {
    fun observeConnectionStatus(): Observable<GyverLampConnectionState>
    fun observeConnection(): Observable<Pair<GyverLampConnectionState, GyverLampState?>>

    @Throws(CannotConnectException::class)
    fun addRequest(request: String): Single<GyverLampState>

    class CannotConnectException() : Exception()
}