package ru.vladislavsumin.myhomeiot.domain.gyver.lamp.impl

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
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

    override fun updateLamp(gyverLampEntity: GyverLampEntity): Completable {
        return mGyverLampDao
            .observeUpdate(gyverLampEntity)
            .subscribeOnIo()
    }

    override fun deleteLamp(gyverLampEntity: GyverLampEntity): Completable {
        return deleteLamp(gyverLampEntity.id)
    }

    override fun deleteLamp(id: Long): Completable {
        return mGyverLampDao
            .observeDeleteById(id)
            .subscribeOnIo()
    }

    override fun observeLamps(): Flowable<List<GyverLampEntity>> {
        return mGyverLampDao
            .observeAll()
            .subscribeOnIo()
    }

    override fun observeLamp(id: Long): Flowable<GyverLampEntity> {
        return mGyverLampDao
            .observeById(id)
            .map {
                if (it.isEmpty()) throw GyverLampManager.LampNotFoundException()
                it[0]
            }
            .subscribeOnIo()
    }

    override fun getLamp(id: Long): Single<GyverLampEntity> {
        return observeLamp(id).firstOrError()
    }
}