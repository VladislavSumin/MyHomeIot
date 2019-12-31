package ru.vladislavsumin.myhomeiot.network.impl

import android.net.ConnectivityManager
import android.net.Network
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.Mockito.`when` as when_
import org.mockito.junit.MockitoJUnitRunner
import ru.vladislavsumin.myhomeiot.network.NetworkConnectivityManager

@RunWith(MockitoJUnitRunner::class)
class NetworkConnectivityManagerApi24Test {

    @Mock
    private lateinit var mConnectivityManager: ConnectivityManager
    var mCallback: ConnectivityManager.NetworkCallback? = null

    lateinit var mNetworkConnectivityManager: NetworkConnectivityManager

    @Before
    fun before() {
        when_(mConnectivityManager.registerDefaultNetworkCallback(any()))
            .then {
                if (mCallback != null) fail("try to add second listener")
                mCallback = it.arguments[0] as ConnectivityManager.NetworkCallback
                it
            }

        when_(
            mConnectivityManager.unregisterNetworkCallback(
                any(ConnectivityManager.NetworkCallback::class.java)
            )
        )
            .then {
                if (mCallback == null) fail("try to remove second listener")
                assertSame(mCallback, it.arguments[0])
                mCallback = null
                it
            }

        mNetworkConnectivityManager = NetworkConnectivityManagerApi24(mConnectivityManager)
    }

    @Test
    fun `single connect`() {
        val testObserver = mNetworkConnectivityManager.observeNetworkConnected()
            .test()

        assertNotNull(mCallback)
        mCallback!!.onAvailable(mock(Network::class.java))

        testObserver.assertValue(true)
        testObserver.dispose()

        assertNull(mCallback)
    }

    @Test
    fun `single connect listen multiple value`() {
        val testObserver = mNetworkConnectivityManager.observeNetworkConnected()
            .test()

        mCallback!!.onUnavailable()
        mCallback!!.onAvailable(mock(Network::class.java))
        mCallback!!.onAvailable(mock(Network::class.java))
        mCallback!!.onAvailable(mock(Network::class.java))
        mCallback!!.onLost(mock(Network::class.java))

        testObserver.assertValueSequence(listOf(false, true, false))
        testObserver.dispose()
    }

    @Test
    fun `multiple connect`() {
        val testObserver1 = mNetworkConnectivityManager.observeNetworkConnected()
            .test()

        mCallback!!.onUnavailable()

        val testObserver2 = mNetworkConnectivityManager.observeNetworkConnected()
            .test()

        testObserver1.assertValue(false)
        testObserver2.assertValue(false)

        testObserver1.dispose()
        testObserver2.dispose()
    }
}