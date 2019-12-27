package ru.vladislavsumin.myhomeiot.network.impl

import ru.vladislavsumin.myhomeiot.network.NetworkAddressVerifier
import java.util.regex.Pattern

class NetworkAddressVerifierImpl : NetworkAddressVerifier {
    companion object {
        private val HOST_OR_IP_REGEX = Pattern.compile(
            "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])\$"
        )
    }

    override fun verifyIsHostOrIp(host: String): Boolean {
        return HOST_OR_IP_REGEX.matcher(host).matches()
    }
}