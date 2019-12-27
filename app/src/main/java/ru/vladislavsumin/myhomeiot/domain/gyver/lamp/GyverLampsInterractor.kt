package ru.vladislavsumin.myhomeiot.domain.gyver.lamp

import androidx.annotation.AnyThread
import io.reactivex.Completable

interface GyverLampsInterractor {
    /**
     * Work async, return result in internal thread
     */
    fun checkConnection(host: String, port: Int, timeout: Int = 1000): Completable

    @AnyThread
    fun getGyverLampInterractor(gyverLampId: Long): GyverLampInterractor
}