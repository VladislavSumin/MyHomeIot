package ru.vladislavsumin.myhomeiot.strorage.impl

import android.content.Context
import android.content.SharedPreferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.vladislavsumin.myhomeiot.strorage.PrivacyPolicyStorage
import ru.vladislavsumin.myhomeiot.strorage.StorageConstants


@RunWith(AndroidJUnit4::class)
class PrivacyPolicyStorageImplAndroidTest {
    private lateinit var mStorage: PrivacyPolicyStorage
    private lateinit var mContext: Context

    @Before
    fun before() {
        mContext = InstrumentationRegistry.getInstrumentation().targetContext

        clearSharedPrefs()
        mStorage = PrivacyPolicyStorageImpl(mContext)
    }

    @Test
    fun testCheckDefaultPrivacyPoliceStatus() {
        assertFalse(mStorage.isAccepted())
    }

    @Test
    fun checkAcceptPrivacyPolicy() {
        assertFalse(mStorage.isAccepted())
        mStorage.setIsAccepted(true)
        assertTrue(mStorage.isAccepted())
    }


    /**
     * Clears everything in the SharedPreferences
     */
    private fun clearSharedPrefs() {
        val prefs: SharedPreferences =
            mContext.getSharedPreferences(
                StorageConstants.PRIVACY_POLICY_SHARED_PREFERENCES,
                Context.MODE_PRIVATE
            )
        val editor = prefs.edit()
        editor.clear()
        editor.commit()
    }
}