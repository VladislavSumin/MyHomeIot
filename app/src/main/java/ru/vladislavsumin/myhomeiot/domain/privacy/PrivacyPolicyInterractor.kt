package ru.vladislavsumin.myhomeiot.domain.privacy

interface PrivacyPolicyInterractor {
    fun isPrivacyPolicyAccepted(): Boolean
    fun acceptPrivacyPolicy()
}