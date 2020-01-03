package ru.vladislavsumin.myhomeiot.database

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.vladislavsumin.myhomeiot.database.entity.GyverLampEntity
import ru.vladislavsumin.myhomeiot.database.dao.GyverLampDao
import ru.vladislavsumin.myhomeiot.database.migration.Migration1to2
import ru.vladislavsumin.myhomeiot.utils.tag

@androidx.room.Database(
    entities = [
        GyverLampEntity::class
    ],
    exportSchema = true,
    version = 2
)
abstract class Database : RoomDatabase() {
    companion object {
        private const val DATABASE_FILE = "database"
        private val TAG = tag<Database>()

        fun createInstance(context: Context): Database {
            Log.i(TAG, "creating database instance")
            val database =
                Room.databaseBuilder<Database>(context, Database::class.java, DATABASE_FILE)
                    .addMigrations(Migration1to2())
                    .build()
            Log.i(TAG, "database created")
            return database
        }
    }

    abstract fun getGyverLampDao(): GyverLampDao
}