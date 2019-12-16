package ru.vladislavsumin.myhomeiot.network

import java.net.DatagramSocket

interface SocketProvider {
    fun getDatagramSocket():DatagramSocket
}