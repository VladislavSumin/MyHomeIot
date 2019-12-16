package ru.vladislavsumin.myhomeiot.domain.gyver.lamp

import androidx.annotation.AnyThread
import io.reactivex.Completable
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampInterractor
import java.net.InetAddress

interface GyverLampsInterractor {
    /**
     * Work async, return result in internal thread
     */
    fun checkConnection(host: InetAddress, port: Int, timeout: Int = 1000): Completable

    @AnyThread
    fun getGyverLampInterractor(gyverLampId: Long): GyverLampInterractor
}