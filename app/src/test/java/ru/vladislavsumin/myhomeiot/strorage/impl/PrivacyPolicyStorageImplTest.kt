package ru.vladislavsumin.myhomeiot.strorage.impl

import android.content.Context
import android.content.SharedPreferences
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.Mockito.anyString
import org.mockito.Mockito.`when` as when_
import org.mockito.junit.MockitoJUnitRunner
import ru.vladislavsumin.myhomeiot.SharedPreferencesMock
import ru.vladislavsumin.myhomeiot.strorage.PrivacyPolicyStorage

@RunWith(MockitoJUnitRunner::class)
class PrivacyPolicyStorageImplTest {

    @Mock
    private lateinit var mContext: Context

    private lateinit var mSharedPreferences: SharedPreferences

    private lateinit var mStorage: PrivacyPolicyStorage

    @Before
    fun before() {
        mSharedPreferences = SharedPreferencesMock()
        when_(mContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mSharedPreferences)
        mStorage = PrivacyPolicyStorageImpl(mContext)
    }

    @Test
    fun `test check default return value`() {
        assertFalse(mStorage.isAccepted())
    }

    @Test
    fun `test check setter`() {
        mStorage.setIsAccepted(true)
        assertTrue(mStorage.isAccepted())

        mStorage.setIsAccepted(false)
        assertFalse(mStorage.isAccepted())
    }
}