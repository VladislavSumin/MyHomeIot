package ru.vladislavsumin.myhomeiot.domain.gyver.lamp.impl

import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampProtocol
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampState
import java.lang.IllegalArgumentException
import java.net.DatagramPacket
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

class GyverLampProtocolImpl : GyverLampProtocol {
    companion object {
        private val CURRENT_STATE_REGEXP =
            Pattern.compile(
//                "^CURR (?<mode>[0-2]?[0-9]{1,2}) (?<brightness>[0-2]?[0-9]{1,2}) (?<speed>[0-2]?[0-9]{1,2}) (?<scale>[0-2]?[0-9]{1,2}) (?<onFlag>[0-1]*)\$"
                "^CURR ([0-2]?[0-9]{1,2}) ([0-2]?[0-9]{1,2}) ([0-2]?[0-9]{1,2}) ([0-2]?[0-9]{1,2}) ([0-1]*)\$"
            )
        private val BRIGHTNESS_STATE_REGEXP =
            Pattern.compile(
                "^BRI([0-2]?[0-9]{1,2})\$"
            )
    }

    override fun getCurrentStateRequest(): String = "GET"
    override fun getOnRequest(): String = "P_ON"
    override fun getOffRequest(): String = "P_OFF"
    override fun getBrightnessRequest(brightness: Int): String = "BRI%03d".format(brightness)

    override fun parseCurrentStateResponse(
        response: String,
        previousState: GyverLampState?
    ): GyverLampState? {
        //TODO это должно зависить от запроса
        if (response.startsWith("CURR")) return parseCurrent(response)
        if (response.startsWith("BRI")) return parseBrightness(response, previousState)
        throw GyverLampProtocol.BadResponseException()
    }

    private fun parseBrightness(response: String, previousState: GyverLampState?): GyverLampState? {
        previousState ?: return null
        val matcher = BRIGHTNESS_STATE_REGEXP.matcher(response)
        if (!matcher.matches()) throw GyverLampProtocol.BadResponseException()
        return previousState.copy(brightness = matcher.group(1)!!.toInt())
    }

    private fun parseCurrent(response: String): GyverLampState {
        val matcher = CURRENT_STATE_REGEXP.matcher(response)
        if (!matcher.matches()) throw GyverLampProtocol.BadResponseException()
        return GyverLampState(
            mode = matcher.group(1)!!.toInt(),
            brightness = matcher.group(2)!!.toInt(),
            speed = matcher.group(3)!!.toInt(),
            scale = matcher.group(4)!!.toInt(),
            isOn = matcher.group(5)!!.toInt() != 0
        )
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

    override fun datagramPacketToString(datagramPacket: DatagramPacket): String {
        val data = datagramPacket.data
        val indexOf = data.indexOf(0) //find end of string
        if (indexOf == -1) throw GyverLampProtocol.BadPacketException()
        return String(data, 0, indexOf, StandardCharsets.US_ASCII)
    }
}