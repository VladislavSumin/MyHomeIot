package ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection

data class GyverLampState(
    val mode: Int, //TODO replace with enum
    val brightness: Int,
    val speed: Int,
    val scale: Int,
    val isOn: Boolean
)