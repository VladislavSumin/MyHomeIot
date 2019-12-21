package ru.vladislavsumin.myhomeiot.domain.gyver.lamp

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
    fun getOnRequest(): String
    fun getOffRequest(): String
    fun getBrightnessRequest(brightness: Int): String
    fun getScaleRequest(scale: Int): String
    fun getSpeedRequest(speed: Int): String
    fun getModeRequest(mode: GyverLampMode): String

    @Throws(BadResponseException::class)
    fun parseCurrentStateResponse(response: String, previousState: GyverLampState?): GyverLampState?


    fun stringToDatagramPacket(data: String, datagramPacket: DatagramPacket? = null): DatagramPacket

    @Throws(BadPacketException::class)
    fun datagramPacketToString(datagramPacket: DatagramPacket): String


    class BadPacketException : Exception()
    class BadResponseException : Exception()
}