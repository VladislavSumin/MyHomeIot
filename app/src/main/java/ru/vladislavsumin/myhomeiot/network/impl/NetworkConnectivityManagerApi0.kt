package ru.vladislavsumin.myhomeiot.network.impl

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import io.reactivex.Observable
import io.reactivex.ObservableEmitter

@Suppress("DEPRECATION")
class NetworkConnectivityManagerApi0(
    private val mContext: Context,
    private val mConnectivityService: ConnectivityManager
) : NetworkConnectivityManagerAbstract() {

    override fun createNetworkConnectedObservable(): Observable<Boolean> {
        return Observable.create {
            val receiver = NetworkChangeReceiver(it)
            mContext.registerReceiver(
                receiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
            it.onNext(checkIsNetworkConnected())
            it.setCancellable { mContext.unregisterReceiver(receiver) }
        }
    }

    fun checkIsNetworkConnected(): Boolean {
        val activeNetwork = mConnectivityService.activeNetworkInfo
        return activeNetwork != null
    }

    private inner class NetworkChangeReceiver(private val mEmitter: ObservableEmitter<Boolean>) :
        BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            mEmitter.onNext(checkIsNetworkConnected())
        }
    }
}