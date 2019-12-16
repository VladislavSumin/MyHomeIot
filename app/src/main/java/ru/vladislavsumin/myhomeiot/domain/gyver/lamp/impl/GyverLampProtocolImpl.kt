package ru.vladislavsumin.myhomeiot.domain.gyver.lamp.impl

import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampProtocol
import java.lang.IllegalArgumentException
import java.net.DatagramPacket
import java.nio.charset.StandardCharsets

class GyverLampProtocolImpl : GyverLampProtocol {
    override fun getRequest(): String {
        return "GET"
    }

    override fun stringToDatagramPacket(
        data: String,
        datagramPacket: DatagramPacket?
    ): DatagramPacket {
        var dataBytes = data.toByteArray(StandardCharsets.US_ASCII)

        if (datagramPacket != null) {
            val destination = datagramPacket.data
            if (destination.size - 1 < dataBytes.size) throw IllegalArgumentException()
            dataBytes.copyInto(destination)
            destination[destination.size - 1] = 0
            return datagramPacket
        }

        dataBytes = dataBytes.copyOf(dataBytes.size + 1)
        dataBytes[dataBytes.size - 1] = 0
        return DatagramPacket(dataBytes, dataBytes.size)
    }
}