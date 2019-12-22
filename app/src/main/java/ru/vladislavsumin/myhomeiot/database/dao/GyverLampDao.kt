package ru.vladislavsumin.myhomeiot.database.dao

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Flowable
import ru.vladislavsumin.myhomeiot.database.entity.GyverLampEntity

@Dao
interface GyverLampDao : BaseDao<GyverLampEntity> {
    @Query("SELECT * FROM iot_gyver_lamps")
    fun observeAll(): Flowable<List<GyverLampEntity>>

    @Query("SELECT * FROM iot_gyver_lamps WHERE id = :id")
    fun observeById(id: Long): Flowable<GyverLampEntity>

    @Query("DELETE FROM iot_gyver_lamps WHERE id = :id")
    fun observeDeleteById(id: Long): Completable
}