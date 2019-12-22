package ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection

import io.reactivex.Completable
import io.reactivex.Observable
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampState


interface GyverLampConnection {
    fun observeConnectionStatus(): Observable<GyverLampConnectionState>
    fun observeConnection(): Observable<Pair<GyverLampConnectionState, GyverLampState?>>

    @Throws(ConnectionException::class)
    fun addRequest(request: String): Completable

    class ConnectionException() : Exception()
}