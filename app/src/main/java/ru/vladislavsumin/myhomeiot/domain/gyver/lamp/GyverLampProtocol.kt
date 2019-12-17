package ru.vladislavsumin.myhomeiot.domain.gyver.lamp

import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampState
import java.lang.Exception
import java.net.DatagramPacket

/**
 *
 *
 * Current state package structure
 * Uses space as delimiter
 *
 * CURR currentMode brightness speed scale onFlag(as number)
 *
 *
 */
interface GyverLampProtocol {
    fun getCurrentStateRequest(): String

    @Throws(BadResponseException::class)
    fun parseCurrentStateResponse(response: String): GyverLampState


    fun stringToDatagramPacket(data: String, datagramPacket: DatagramPacket? = null): DatagramPacket

    @Throws(BadPacketException::class)
    fun datagramPacketToString(datagramPacket: DatagramPacket): String

    class BadPacketException : Exception()
    class BadResponseException : Exception()
}