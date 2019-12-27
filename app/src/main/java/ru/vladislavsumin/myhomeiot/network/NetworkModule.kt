package ru.vladislavsumin.myhomeiot.network

import android.net.ConnectivityManager
import dagger.Module
import dagger.Provides
import ru.vladislavsumin.myhomeiot.network.impl.NetworkAddressVerifierImpl
import ru.vladislavsumin.myhomeiot.network.impl.NetworkConnectivityManagerImpl
import ru.vladislavsumin.myhomeiot.network.impl.SocketProviderImpl
import javax.inject.Singleton

@Module
class NetworkModule {
    @Provides
    @Singleton
    fun provideSocketProvider(): SocketProvider {
        return SocketProviderImpl()
    }

    @Provides
    @Singleton
    fun provideNetworkConnectivityManager(connectivityManager: ConnectivityManager)
            : NetworkConnectivityManager {
        return NetworkConnectivityManagerImpl(connectivityManager)
    }

    @Provides
    @Singleton
    fun provideNetworkAddressVerifier(): NetworkAddressVerifier {
        return NetworkAddressVerifierImpl()
    }
}