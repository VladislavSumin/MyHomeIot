package ru.vladislavsumin.myhomeiot.domain.gyver.lamp.impl

import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampMode
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampProtocol
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampState
import java.lang.IllegalArgumentException
import java.net.DatagramPacket
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

//TODO переписать эту херню целиком
class GyverLampProtocolImpl : GyverLampProtocol {
    companion object {
        private val CURRENT_STATE_REGEXP =
            Pattern.compile(
//                "^CURR (?<mode>[0-2]?[0-9]{1,2}) (?<brightness>[0-2]?[0-9]{1,2}) (?<speed>[0-2]?[0-9]{1,2}) (?<scale>[0-2]?[0-9]{1,2}) (?<onFlag>[0-1]*)\$"
                "^CURR ([0-2]?[0-9]{1,2}) ([0-2]?[0-9]{1,2}) ([0-2]?[0-9]{1,2}) ([0-2]?[0-9]{1,2}) ([0-1]*).*\$"
            )
        private val BRIGHTNESS_STATE_REGEXP =
            Pattern.compile(
                "^BRI([0-2]?[0-9]{1,2})\$"
            )

        private val SCALE_STATE_REGEXP =
            Pattern.compile(
                "^SCA([0-2]?[0-9]{1,2})\$"
            )

        private val SPEED_STATE_REGEXP =
            Pattern.compile(
                "^SPD([0-2]?[0-9]{1,2})\$"
            )
    }

    override fun getCurrentStateRequest(): String = "GET"
    override fun getOnRequest(): String = "P_ON"
    override fun getOffRequest(): String = "P_OFF"
    override fun getBrightnessRequest(brightness: Int): String = "BRI%03d".format(brightness)
    override fun getScaleRequest(scale: Int): String = "SCA%03d".format(scale)
    override fun getSpeedRequest(speed: Int): String = "SPD%03d".format(speed)
    override fun getModeRequest(mode: GyverLampMode): String = "EFF%03d".format(mode.id)

    override fun parseCurrentStateResponse(
        response: String,
        previousState: GyverLampState?
    ): GyverLampState? {
        //TODO это должно зависеть от запроса
        if (response.startsWith("CURR")) return parseCurrent(response)
        if (response.startsWith("BRI")) return parseBrightness(response, previousState)
        if (response.startsWith("SCA")) return parseScale(response, previousState)
        if (response.startsWith("SPD")) return parseSpeed(response, previousState)
        throw GyverLampProtocol.BadResponseException()
    }

    private fun parseBrightness(response: String, previousState: GyverLampState?): GyverLampState? {
        previousState ?: return null
        val matcher = BRIGHTNESS_STATE_REGEXP.matcher(response)
        if (!matcher.matches()) throw GyverLampProtocol.BadResponseException()
        return previousState.copy(brightness = matcher.group(1)!!.toInt())
    }

    private fun parseScale(response: String, previousState: GyverLampState?): GyverLampState? {
        previousState ?: return null
        val matcher = SCALE_STATE_REGEXP.matcher(response)
        if (!matcher.matches()) throw GyverLampProtocol.BadResponseException()
        return previousState.copy(scale = matcher.group(1)!!.toInt())
    }

    private fun parseSpeed(response: String, previousState: GyverLampState?): GyverLampState? {
        previousState ?: return null
        val matcher = SPEED_STATE_REGEXP.matcher(response)
        if (!matcher.matches()) throw GyverLampProtocol.BadResponseException()
        return previousState.copy(speed = matcher.group(1)!!.toInt())
    }

    //TODO добавить проверки на ошибки
    private fun parseCurrent(response: String): GyverLampState {
        val matcher = CURRENT_STATE_REGEXP.matcher(response)
        if (!matcher.matches()) throw GyverLampProtocol.BadResponseException()

        val modeInt = matcher.group(1)!!.toInt()
        val mode = GyverLampMode.values().find { it.id == modeInt }
            ?: GyverLampMode.UNKNOWN

        return GyverLampState(
            mode = mode,
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