package ru.vladislavsumin.myhomeiot.network

import io.reactivex.Observable

interface NetworkConnectivityManager {
    fun observeNetworkConnected(): Observable<Boolean>
}