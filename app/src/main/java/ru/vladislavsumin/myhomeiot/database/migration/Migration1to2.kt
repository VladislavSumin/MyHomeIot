package ru.vladislavsumin.myhomeiot.database.migration

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.vladislavsumin.myhomeiot.database.entity.GyverLampEntity
import ru.vladislavsumin.myhomeiot.utils.tag

class Migration1to2 : Migration(1, 2) {
    companion object {
        private val TAG = tag<Migration1to2>()
    }

    override fun migrate(database: SupportSQLiteDatabase) {
        Log.d(TAG, "start migration")
        val defValue = GyverLampEntity.DeviceType.GYVER_LAMP_ORIGIN.id
        database.execSQL("ALTER TABLE iot_gyver_lamps ADD COLUMN deviceType INTEGER DEFAULT $defValue NOT NULL")
        Log.d(TAG, "end migration")
    }
}