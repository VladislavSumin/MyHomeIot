package ru.vladislavsumin.myhomeiot.network

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import dagger.Module
import dagger.Provides
import ru.vladislavsumin.myhomeiot.network.impl.NetworkAddressVerifierImpl
import ru.vladislavsumin.myhomeiot.network.impl.NetworkConnectivityManagerApi0
import ru.vladislavsumin.myhomeiot.network.impl.NetworkConnectivityManagerApi24
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
    fun provideNetworkConnectivityManager(
        context: Context,
        connectivityManager: ConnectivityManager
    ): NetworkConnectivityManager {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            NetworkConnectivityManagerApi24(connectivityManager)
        else
            NetworkConnectivityManagerApi0(context, connectivityManager)
    }

    @Provides
    @Singleton
    fun provideNetworkAddressVerifier(): NetworkAddressVerifier {
        return NetworkAddressVerifierImpl()
    }
}