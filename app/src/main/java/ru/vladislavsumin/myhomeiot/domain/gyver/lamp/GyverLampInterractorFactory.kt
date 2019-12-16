package ru.vladislavsumin.myhomeiot.domain.gyver.lamp

import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampInterractor

interface GyverLampInterractorFactory {
    fun createGyverLampInterractor(gyverLampId: Long): GyverLampInterractor
}