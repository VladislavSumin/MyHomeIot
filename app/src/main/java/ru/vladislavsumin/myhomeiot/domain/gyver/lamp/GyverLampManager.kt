package ru.vladislavsumin.myhomeiot.domain.gyver.lamp

import io.reactivex.Completable
import io.reactivex.Flowable
import ru.vladislavsumin.myhomeiot.database.entity.GyverLampEntity

interface GyverLampManager {
    /**
     * Async. Return data on internal thread
     */
    fun addLamp(gyverLampEntity: GyverLampEntity): Completable

    /**
     * Async. Return data on internal thread
     */
    fun updateLamp(gyverLampEntity: GyverLampEntity): Completable

    /**
     * Async. Return data on internal thread
     */
    fun deleteLamp(gyverLampEntity: GyverLampEntity): Completable

    /**
     * Async. Return data on internal thread
     */
    fun deleteLamp(id: Long): Completable

    fun observeLamps(): Flowable<List<GyverLampEntity>>

    fun observeLamp(id: Long): Flowable<GyverLampEntity>
}