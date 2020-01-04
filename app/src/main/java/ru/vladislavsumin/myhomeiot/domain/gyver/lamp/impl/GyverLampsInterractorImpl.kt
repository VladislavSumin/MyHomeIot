package ru.vladislavsumin.myhomeiot.domain.gyver.lamp.impl

import android.util.Log
import io.reactivex.Completable
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampInterractor
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampInterractorFactory
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampProtocol
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampsInterractor
import ru.vladislavsumin.myhomeiot.network.SocketProvider
import ru.vladislavsumin.myhomeiot.utils.subscribeOnIo
import ru.vladislavsumin.myhomeiot.utils.subscribeOnNewThread
import ru.vladislavsumin.myhomeiot.utils.tag
import java.lang.ref.WeakReference
import java.net.DatagramPacket
import java.net.InetAddress
import java.util.concurrent.ConcurrentHashMap

class GyverLampsInterractorImpl(
    private val mSocketProvider: SocketProvider,
    private val mGyverLampProtocol: GyverLampProtocol,
    private val mGyverLampInterractorFactory: GyverLampInterractorFactory
) : GyverLampsInterractor {

    companion object {
        private val TAG = tag<GyverLampManagerImpl>()
        private const val DEFAULT_RECEIVE_PACKAGE_SIZE = 1000
    }

    private val mGyverLampInterractors: MutableMap<Long, WeakReference<GyverLampInterractor>> =
        ConcurrentHashMap()

    override fun getGyverLampInterractor(gyverLampId: Long): GyverLampInterractor {
        synchronized(mGyverLampInterractors) {
            // Search on weak reference storage
            var gyverLampInterractor = mGyverLampInterractors[gyverLampId]?.get()
            if (gyverLampInterractor != null) {
                Log.d(
                    TAG,
                    "Find ${GyverLampInterractor::class.java.simpleName} " +
                            "with id $gyverLampId in weak reference cache"
                )
                return gyverLampInterractor
            } else {
                // Create instance if does not find
                Log.d(
                    TAG,
                    "Creating new instance of ${GyverLampInterractor::class.java.simpleName} " +
                            "with id $gyverLampId"
                )
                gyverLampInterractor = createGyverLampInterractor(gyverLampId)
                mGyverLampInterractors[gyverLampId] = WeakReference(gyverLampInterractor)
                return gyverLampInterractor
            }
        }
    }

    private fun createGyverLampInterractor(gyverLampId: Long): GyverLampInterractor {
        return mGyverLampInterractorFactory.createGyverLampInterractor(gyverLampId)
    }

    //***********************************************************************//
    //                          check connection                             //
    //***********************************************************************//

    override fun checkConnection(host: String, port: Int, timeout: Int): Completable {
        return Completable.create { emitter ->
            mSocketProvider.createDatagramSocket().use { socket ->
                // Send hello package
                val helloPacket = mGyverLampProtocol.getCurrentStateRequest().toDatagramPacket()
                helloPacket.address = InetAddress.getByName(host)
                helloPacket.port = port
                socket.send(helloPacket)


                // Wait response
                val byteArray = ByteArray(DEFAULT_RECEIVE_PACKAGE_SIZE)
                val datagramPacket = DatagramPacket(byteArray, byteArray.size)
                socket.soTimeout = timeout
                socket.receive(datagramPacket)

                // Emit ok if receive any response
                emitter.onComplete()
            }
        }
            .subscribeOnNewThread()
    }

    private fun String.toDatagramPacket(): DatagramPacket {
        return mGyverLampProtocol.stringToDatagramPacket(this)
    }
}