package ru.vladislavsumin.myhomeiot.domain

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.vladislavsumin.myhomeiot.database.dao.GyverLampDao
import ru.vladislavsumin.myhomeiot.domain.firebase.FirebaseInterractor
import ru.vladislavsumin.myhomeiot.domain.firebase.impl.FirebaseInterractorImpl
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampInterractorFactory
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampManager
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampProtocol
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampsInterractor
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnectionFactory
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.impl.GyverLampConnectionFactoryImpl
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.impl.GyverLampInterractorFactoryImpl
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.impl.GyverLampManagerImpl
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.impl.GyverLampProtocolImpl
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.impl.GyverLampsInterractorImpl
import ru.vladislavsumin.myhomeiot.domain.privacy.PrivacyPolicyInterractor
import ru.vladislavsumin.myhomeiot.domain.privacy.impl.PrivacyPolicyInterractorImpl
import ru.vladislavsumin.myhomeiot.network.NetworkConnectivityManager
import ru.vladislavsumin.myhomeiot.network.SocketProvider
import ru.vladislavsumin.myhomeiot.strorage.PrivacyPolicyStorage
import javax.inject.Singleton

@Module
class DomainModule {
    @Provides
    @Singleton
    fun providePrivacyPolicyInterractor(
        privacyPolicyStorage: PrivacyPolicyStorage
    ): PrivacyPolicyInterractor {
        return PrivacyPolicyInterractorImpl(privacyPolicyStorage)
    }

    @Provides
    @Singleton
    fun provideFirebaseInterractor(
        context: Context,
        privacyPolicyInterractor: PrivacyPolicyInterractor
    ): FirebaseInterractor {
        return FirebaseInterractorImpl(context, privacyPolicyInterractor)
    }


    @Provides
    @Singleton
    fun provideGyverLampManager(gyverLampDao: GyverLampDao): GyverLampManager {
        return GyverLampManagerImpl(gyverLampDao)
    }

    @Provides
    @Singleton
    fun provideGyverLampProtocol(): GyverLampProtocol {
        return GyverLampProtocolImpl()
    }

    @Provides
    @Singleton
    fun provideGyverLampsInterractor(
        socketProvider: SocketProvider,
        gyverLampProtocol: GyverLampProtocol,
        gyverLampInterractorFactory: GyverLampInterractorFactory
    ): GyverLampsInterractor {
        return GyverLampsInterractorImpl(
            socketProvider, gyverLampProtocol, gyverLampInterractorFactory
        )
    }

    @Provides
    @Singleton
    fun provideGyverLampInterractorFactory(
        gyverLampManager: GyverLampManager,
        connectivityManager: NetworkConnectivityManager,
        gyverLampConnectionFactory: GyverLampConnectionFactory
    ): GyverLampInterractorFactory {
        return GyverLampInterractorFactoryImpl(
            gyverLampManager,
            connectivityManager,
            gyverLampConnectionFactory
        )
    }

    @Provides
    @Singleton
    fun provideGyverLampConnectionFactory(): GyverLampConnectionFactory {
        return GyverLampConnectionFactoryImpl()
    }
}