package ru.vladislavsumin.myhomeiot.network

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Exception
import java.net.*
import kotlin.concurrent.thread

@RunWith(AndroidJUnit4::class)
class DatagramSocketTest {

    @Test
    fun testSocketCloseWhileReceive() {
        val datagramSocket = DatagramSocket()
        var exception: Exception? = null

        thread {
            try {
                datagramSocket.receive(getDatagramPacket())
            } catch (e: Exception) {
                exception = e
            }
        }

        Thread.sleep(100)
        datagramSocket.close()
        waitCondition { exception != null }
        assertTrue(exception is SocketException)
    }

//    @Test
//    fun testSocketCloseWhileReceiveWithTimeout() {
//        val datagramSocket = DatagramSocket()
//        datagramSocket.soTimeout = 5000
//        var exception: Exception? = null
//
//        thread {
//            try {
//                datagramSocket.receive(getDatagramPacket())
//            } catch (e: Exception) {
//                exception = e
//            }
//        }
//
//        Thread.sleep(100)
//        datagramSocket.soTimeout = 0
//        datagramSocket.close()
//        waitCondition { exception != null }
//        assertTrue(exception is SocketException)
//    }

    @Test(expected = SocketException::class)
    fun testCallReceiveOnClosedSocket() {
        val datagramSocket = DatagramSocket()
        datagramSocket.close()
        datagramSocket.receive(getDatagramPacket())
    }

    private fun getDatagramPacket(): DatagramPacket {
        return DatagramPacket(ByteArray(1000), 1000).apply {
            address = InetAddress.getByName("10.0.0.1")
            port = 7000
        }
    }

    private fun waitCondition(timeout: Long = 5000, condition: () -> Boolean) {
        val time = System.currentTimeMillis()
        while (System.currentTimeMillis() - time < timeout && !condition()) Thread.sleep(1)
        assertTrue(condition())
    }
}