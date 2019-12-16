package ru.vladislavsumin.myhomeiot.network.impl

import ru.vladislavsumin.myhomeiot.network.SocketProvider
import java.net.DatagramSocket

class SocketProviderImpl : SocketProvider {
    override fun createDatagramSocket(): DatagramSocket {
        return DatagramSocket()
    }
}