package ru.vladislavsumin.myhomeiot.domain.gyver.lamp.impl

import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampInterractor
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampInterractorFactory
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampManager
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnectionFactory
import ru.vladislavsumin.myhomeiot.network.NetworkConnectivityManager

class GyverLampInterractorFactoryImpl(
    private val mGyverLampManager: GyverLampManager,
    private val mNetworkConnectivityManager: NetworkConnectivityManager,
    private val mGyverLampConnectionFactory: GyverLampConnectionFactory
) : GyverLampInterractorFactory {
    override fun createGyverLampInterractor(gyverLampId: Long): GyverLampInterractor {
        return GyverLampInterractorImpl(
            gyverLampId,
            mGyverLampManager,
            mNetworkConnectivityManager,
            mGyverLampConnectionFactory
        )
    }
}