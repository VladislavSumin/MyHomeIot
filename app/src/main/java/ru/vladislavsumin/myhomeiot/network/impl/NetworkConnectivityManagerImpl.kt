package ru.vladislavsumin.myhomeiot.network.impl

import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import ru.vladislavsumin.myhomeiot.network.NetworkConnectivityManager
import ru.vladislavsumin.myhomeiot.utils.tag

class NetworkConnectivityManagerImpl(
    private val mConnectivityService: ConnectivityManager
) : NetworkConnectivityManager {

    companion object {
        private val TAG = tag<NetworkConnectivityManagerImpl>()
    }

    private val mNetworkConnectedObservable: Observable<Boolean> =
        createNetworkConnectedObservable()
            .distinctUntilChanged()
            .doOnSubscribe { Log.d(TAG, "start monitoring network") }
            .doOnNext { Log.d(TAG, "connection state: $it") }
            .doOnDispose { Log.d(TAG, "stop monitoring network") }
            .replay(1)
            .refCount()


    private fun createNetworkConnectedObservable(): Observable<Boolean> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            createNetworkConnectedObservableApi24()
        else
            createNetworkConnectedObservableApiBefore24()

    }

    private fun createNetworkConnectedObservableApiBefore24(): Observable<Boolean> {
        //TODO implement for api before 24
        //TODO разделить на две реализации!
        return Observable.just(true)
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun createNetworkConnectedObservableApi24(): Observable<Boolean> {
        return Observable.create<Boolean> {
            val callback = NetworkCallback(it)
            mConnectivityService.registerDefaultNetworkCallback(callback)
            it.setCancellable { mConnectivityService.unregisterNetworkCallback(callback) }
        }
    }

    override fun observeNetworkConnected(): Observable<Boolean> = mNetworkConnectedObservable

    private inner class NetworkCallback(private val emitter: ObservableEmitter<Boolean>) :
        ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            emitter.onNext(true)
        }

        override fun onUnavailable() {
            emitter.onNext(false)
        }

        override fun onLost(network: Network) {
            emitter.onNext(false)
        }
    }
}