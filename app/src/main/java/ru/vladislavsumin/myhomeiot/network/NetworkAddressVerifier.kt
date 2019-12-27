package ru.vladislavsumin.myhomeiot.network

interface NetworkAddressVerifier {
    fun verifyIsHostOrIp(host: String): Boolean
}