package ru.vladislavsumin.myhomeiot.domain.gyver.lamp

import java.lang.Exception
import java.net.DatagramPacket

interface GyverLampProtocol {
    fun getRequest(): String

    fun stringToDatagramPacket(data: String, datagramPacket: DatagramPacket? = null): DatagramPacket


    @Throws(BadPacketException::class)
    fun datagramPacketToString(datagramPacket: DatagramPacket): String

    class BadPacketException : Exception()
}