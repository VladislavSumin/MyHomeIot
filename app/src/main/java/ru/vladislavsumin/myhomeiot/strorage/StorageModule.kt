package ru.vladislavsumin.myhomeiot.strorage

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.vladislavsumin.myhomeiot.strorage.impl.PrivacyPolicyStorageImpl
import javax.inject.Singleton

@Module
class StorageModule {
    @Provides
    @Singleton
    fun providePrivacyPolicyStorage(context: Context): PrivacyPolicyStorage {
        return PrivacyPolicyStorageImpl(context)
    }
}