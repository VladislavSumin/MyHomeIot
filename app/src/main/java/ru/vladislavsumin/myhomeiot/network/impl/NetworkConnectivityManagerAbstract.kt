package ru.vladislavsumin.myhomeiot.network.impl

import android.util.Log
import io.reactivex.Observable
import ru.vladislavsumin.myhomeiot.network.NetworkConnectivityManager
import ru.vladislavsumin.myhomeiot.utils.tag

abstract class NetworkConnectivityManagerAbstract : NetworkConnectivityManager {

    private val mNetworkConnectedObservable: Observable<Boolean> by lazy {
        createNetworkConnectedObservable()
            .distinctUntilChanged()
            .doOnSubscribe { Log.d(tag(), "start monitoring network") }
            .doOnNext { Log.d(tag(), "connection state: $it") }
            .doOnDispose { Log.d(tag(), "stop monitoring network") }
            .replay(1)
            .refCount()
    }

    final override fun observeNetworkConnected(): Observable<Boolean> {
        return mNetworkConnectedObservable
    }

    protected abstract fun createNetworkConnectedObservable(): Observable<Boolean>
}