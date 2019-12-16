package ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection

import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnection

interface GyverLampConnectionFactory {
    fun createLoadingSate(): GyverLampConnection
    fun createNetworkUnavailableSate(): GyverLampConnection
    fun create(): GyverLampConnection
}