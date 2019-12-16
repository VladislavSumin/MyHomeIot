package ru.vladislavsumin.myhomeiot.domain.gyver.lamp

import java.net.DatagramPacket

interface GyverLampProtocol {
    fun getRequest(): String

    fun stringToDatagramPacket(data: String, datagramPacket: DatagramPacket? = null): DatagramPacket
}