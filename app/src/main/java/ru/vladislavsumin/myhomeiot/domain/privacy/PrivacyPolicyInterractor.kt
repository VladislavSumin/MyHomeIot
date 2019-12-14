package ru.vladislavsumin.myhomeiot.domain.privacy

import io.reactivex.Observable


interface PrivacyPolicyInterractor {
    fun observerPrivacyPolicyAccepted(): Observable<Boolean>
    fun isPrivacyPolicyAccepted(): Boolean
    fun acceptPrivacyPolicy()
}