package ru.vladislavsumin.myhomeiot.domain.privacy

import android.net.Uri
import io.reactivex.Observable


interface PrivacyPolicyInterractor {
    fun observerPrivacyPolicyAccepted(): Observable<Boolean>
    fun isPrivacyPolicyAccepted(): Boolean
    fun acceptPrivacyPolicy()
    fun getPrivacyPolicyUri(): Uri
}