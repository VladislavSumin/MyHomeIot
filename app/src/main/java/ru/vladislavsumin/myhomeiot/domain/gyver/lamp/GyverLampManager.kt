package ru.vladislavsumin.myhomeiot.domain.gyver.lamp

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import ru.vladislavsumin.myhomeiot.database.entity.GyverLampEntity
import java.lang.RuntimeException

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

    /**
     * Async. Return data on internal thread
     */
    fun observeLamps(): Flowable<List<GyverLampEntity>>

    /**
     * Async. Return data on internal thread
     */
    fun observeLamp(id: Long): Flowable<GyverLampEntity>

    /**
     * Async. Return data on internal thread
     */
    fun getLamp(id: Long): Single<GyverLampEntity>

    class LampNotFoundException : RuntimeException()
}