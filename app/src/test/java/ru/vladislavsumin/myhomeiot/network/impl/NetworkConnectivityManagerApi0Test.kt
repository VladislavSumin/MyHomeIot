package ru.vladislavsumin.myhomeiot.network.impl

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.Mockito.`when` as when_
import org.mockito.junit.MockitoJUnitRunner
import ru.vladislavsumin.myhomeiot.network.NetworkConnectivityManager

@RunWith(MockitoJUnitRunner::class)
class NetworkConnectivityManagerApi0Test {
    @Mock
    private lateinit var mContext: Context

    @Mock
    private lateinit var mConnectivityManager: ConnectivityManager

    lateinit var mNetworkConnectivityManager: NetworkConnectivityManager


    private var mBroadcastReceiver: BroadcastReceiver? = null
    private var isNetworkConnected = false


    @Before
    fun before() {
        isNetworkConnected = false

        when_(mConnectivityManager.activeNetworkInfo).thenAnswer {
            return@thenAnswer if (isNetworkConnected) mock(NetworkInfo::class.java)
            else null
        }

        when_(mContext.registerReceiver(any(), any())).then {
            if (mBroadcastReceiver != null) fail("already subscribed")
            mBroadcastReceiver = it.arguments[0] as BroadcastReceiver
            null
        }

        when_(mContext.unregisterReceiver(any())).then {
            if (mBroadcastReceiver == null) fail("not subscribed")

            val receiver = it.arguments[0] as BroadcastReceiver
            if (!(receiver === mBroadcastReceiver)) fail("not equals")
            mBroadcastReceiver = null
            null
        }


        mNetworkConnectivityManager =
            NetworkConnectivityManagerApi0(mContext, mConnectivityManager)
    }

    @Test
    fun `single connect`() {
        val testObserver = mNetworkConnectivityManager.observeNetworkConnected()
            .test()

        assertNotNull(mBroadcastReceiver)

        testObserver.assertValue(false)
        testObserver.dispose()

        assertNull(mBroadcastReceiver)
    }

    @Test
    fun `single connect listen multiple value`() {
        val testObserver = mNetworkConnectivityManager.observeNetworkConnected()
            .test()

        isNetworkConnected = true
        mBroadcastReceiver!!.onReceive(null, mock(Intent::class.java))
        mBroadcastReceiver!!.onReceive(null, mock(Intent::class.java))
        mBroadcastReceiver!!.onReceive(null, mock(Intent::class.java))
        isNetworkConnected = false
        mBroadcastReceiver!!.onReceive(null, mock(Intent::class.java))

        testObserver.assertValueSequence(listOf(false, true, false))
        testObserver.dispose()
    }

    @Test
    fun `multiple connect`() {
        val testObserver1 = mNetworkConnectivityManager.observeNetworkConnected()
            .test()
        val testObserver2 = mNetworkConnectivityManager.observeNetworkConnected()
            .test()

        testObserver1.assertValue(false)
        testObserver2.assertValue(false)

        testObserver1.dispose()
        testObserver2.dispose()

        assertNull(mBroadcastReceiver)
    }
}