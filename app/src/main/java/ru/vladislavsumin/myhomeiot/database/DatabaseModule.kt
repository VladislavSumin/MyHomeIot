package ru.vladislavsumin.myhomeiot.database

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.vladislavsumin.myhomeiot.database.Database
import ru.vladislavsumin.myhomeiot.database.dao.GyverLampDao
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(context: Context): Database {
        return Database.createInstance(context)
    }

    @Provides
    @Singleton
    fun provideGyverLampDao(database: Database): GyverLampDao {
        return database.getGyverLampDao()
    }
}