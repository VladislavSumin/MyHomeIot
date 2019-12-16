package ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.impl

import android.util.Log
import ru.vladislavsumin.myhomeiot.database.entity.GyverLampEntity
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampProtocol
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnection
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnectionFactory
import ru.vladislavsumin.myhomeiot.network.SocketProvider
import ru.vladislavsumin.myhomeiot.utils.tag

class GyverLampConnectionFactoryImpl(
    private val mSocketProvider: SocketProvider,
    private val mGyverLampProtocol: GyverLampProtocol
) : GyverLampConnectionFactory {
    companion object {
        private val TAG = tag<GyverLampConnectionFactoryImpl>()
    }

    override fun createLoadingSate(): GyverLampConnection {
        Log.d(TAG, "Creating loading state")
        return GyverLampConnectionStateILoadingImpl()
    }

    override fun createNetworkUnavailableSate(): GyverLampConnection {
        Log.d(TAG, "Creating network unavailable state")

        return GyverLampConnectionStateNetworkUnavailableImpl()
    }

    override fun create(gyverLampEntity: GyverLampEntity): GyverLampConnection {
        Log.d(TAG, "Creating normal state")

        return GyverLampConnectionStateImpl(mSocketProvider, mGyverLampProtocol, gyverLampEntity)
    }
}