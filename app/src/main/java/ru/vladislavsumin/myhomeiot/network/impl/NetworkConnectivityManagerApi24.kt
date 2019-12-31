package ru.vladislavsumin.myhomeiot.network.impl

import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import androidx.annotation.RequiresApi
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import ru.vladislavsumin.myhomeiot.utils.tag

@RequiresApi(Build.VERSION_CODES.N)
class NetworkConnectivityManagerApi24(
    private val mConnectivityService: ConnectivityManager
) : NetworkConnectivityManagerAbstract() {

    override fun createNetworkConnectedObservable(): Observable<Boolean> {
        return Observable.create<Boolean> {
            val callback = NetworkCallback(it)
            mConnectivityService.registerDefaultNetworkCallback(callback)
            it.setCancellable { mConnectivityService.unregisterNetworkCallback(callback) }
        }
    }

    private class NetworkCallback(private val emitter: ObservableEmitter<Boolean>) :
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