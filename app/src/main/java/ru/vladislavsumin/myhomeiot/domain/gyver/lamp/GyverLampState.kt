package ru.vladislavsumin.myhomeiot.domain.gyver.lamp

data class GyverLampState(
    val mode: GyverLampMode,
    val brightness: Int,
    val speed: Int,
    val scale: Int,
    val isOn: Boolean
)