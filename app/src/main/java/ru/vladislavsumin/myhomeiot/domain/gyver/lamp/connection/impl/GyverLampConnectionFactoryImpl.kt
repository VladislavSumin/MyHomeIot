package ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.impl

import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnection
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnectionFactory

class GyverLampConnectionFactoryImpl : GyverLampConnectionFactory {
    override fun createLoadingSate(): GyverLampConnection {
        return GyverLampConnectionStateILoadingImpl()
    }

    override fun createNetworkUnavailableSate(): GyverLampConnection {
        return GyverLampConnectionStateNetworkUnavailableImpl()
    }

    override fun create(): GyverLampConnection {
        return GyverLampConnectionStateImpl()
    }
}