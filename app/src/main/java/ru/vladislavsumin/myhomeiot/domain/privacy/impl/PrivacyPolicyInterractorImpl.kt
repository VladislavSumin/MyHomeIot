package ru.vladislavsumin.myhomeiot.domain.privacy.impl

import ru.vladislavsumin.myhomeiot.domain.privacy.PrivacyPolicyInterractor
import ru.vladislavsumin.myhomeiot.strorage.PrivacyPolicyStorage

class PrivacyPolicyInterractorImpl(
    private val privacyPolicyStorage: PrivacyPolicyStorage
) : PrivacyPolicyInterractor {

    override fun isPrivacyPolicyAccepted(): Boolean {
        return privacyPolicyStorage.isAccepted()
    }

    override fun acceptPrivacyPolicy() {
        privacyPolicyStorage.setIsAccepted(true)
    }
}