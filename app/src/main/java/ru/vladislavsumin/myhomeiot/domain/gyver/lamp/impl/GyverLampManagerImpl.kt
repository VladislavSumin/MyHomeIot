package ru.vladislavsumin.myhomeiot.domain.gyver.lamp.impl

import io.reactivex.Completable
import io.reactivex.Flowable
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampManager
import ru.vladislavsumin.myhomeiot.database.dao.GyverLampDao
import ru.vladislavsumin.myhomeiot.database.entity.GyverLampEntity
import ru.vladislavsumin.myhomeiot.utils.subscribeOnIo

class GyverLampManagerImpl(
    private val mGyverLampDao: GyverLampDao
) : GyverLampManager {
    override fun addLamp(gyverLampEntity: GyverLampEntity): Completable {
        return mGyverLampDao
            .observeInsert(gyverLampEntity)
            .subscribeOnIo()
    }

    override fun observeLamps(): Flowable<List<GyverLampEntity>> {
        return mGyverLampDao
            .observeAll()
            .subscribeOnIo()
    }

    override fun observeLamp(id: Long): Flowable<GyverLampEntity> {
        return mGyverLampDao
            .observerById(id)
            .doOnNext{
                println()
            }
            .subscribeOnIo()
    }
}