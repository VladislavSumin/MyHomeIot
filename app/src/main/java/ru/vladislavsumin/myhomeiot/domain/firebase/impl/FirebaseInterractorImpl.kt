package ru.vladislavsumin.myhomeiot.domain.firebase.impl

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import ru.vladislavsumin.myhomeiot.domain.firebase.FirebaseInterractor
import ru.vladislavsumin.myhomeiot.domain.privacy.PrivacyPolicyInterractor
import ru.vladislavsumin.myhomeiot.utils.observeOnIo
import ru.vladislavsumin.myhomeiot.utils.tag

class FirebaseInterractorImpl(
    private val mContext: Context,
    private val mPrivacyPolicyInterractor: PrivacyPolicyInterractor
) : FirebaseInterractor {
    companion object {
        private val TAG = tag<FirebaseInterractorImpl>()
    }

    @SuppressLint("CheckResult")
    override fun start() {
        mPrivacyPolicyInterractor.observerPrivacyPolicyAccepted()
            .filter { it }
            .firstOrError()
            .ignoreElement()
            .observeOnIo()
            .subscribe(this::init)
    }


    private fun init() {
        Log.i(TAG, "Initializing FirebaseApp")
        val initializeApp = FirebaseApp.initializeApp(mContext)
        if (initializeApp != null) {
            Log.i(TAG, "FirebaseApp initialization successful")
        } else {
            Log.e(TAG, "FirebaseApp initialization unsuccessful")
        }
    }
}