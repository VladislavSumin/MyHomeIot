package ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection

import ru.vladislavsumin.myhomeiot.database.entity.GyverLampEntity

interface GyverLampConnectionFactory {
    fun createLoadingSate(): GyverLampConnection
    fun createNetworkUnavailableSate(): GyverLampConnection
    fun create(gyverLampEntity: GyverLampEntity): GyverLampConnection
}