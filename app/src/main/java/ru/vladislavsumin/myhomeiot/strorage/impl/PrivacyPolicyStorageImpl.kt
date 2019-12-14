package ru.vladislavsumin.myhomeiot.strorage.impl

import android.content.Context
import ru.vladislavsumin.myhomeiot.strorage.PrivacyPolicyStorage
import ru.vladislavsumin.myhomeiot.strorage.StorageConstants

class PrivacyPolicyStorageImpl(context: Context) : PrivacyPolicyStorage {
    companion object {
        private const val PREFERENCE_FILE = StorageConstants.PRIVACY_POLICY_SHARED_PREFERENCES
        private const val IS_PRIVACY_POLICY_ACCEPTED = "is_privacy_policy_accepted"
    }

    private val mPreference = context.getSharedPreferences(
        PREFERENCE_FILE, Context.MODE_PRIVATE
    )

    override fun isAccepted(): Boolean {
        return mPreference.getBoolean(IS_PRIVACY_POLICY_ACCEPTED, false)
    }

    override fun setIsAccepted(isAccepted: Boolean) {
        mPreference
            .edit()
            .putBoolean(IS_PRIVACY_POLICY_ACCEPTED, isAccepted)
            .apply()
    }
}