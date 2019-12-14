package ru.vladislavsumin.myhomeiot.strorage

import androidx.annotation.AnyThread

interface PrivacyPolicyStorage {
    @AnyThread
    fun isAccepted(): Boolean

    @AnyThread
    fun setIsAccepted(isAccepted: Boolean)
}