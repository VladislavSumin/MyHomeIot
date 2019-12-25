package ru.vladislavsumin.myhomeiot.domain.firebase.impl

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.google.firebase.FirebaseApp
import io.fabric.sdk.android.Fabric
import ru.vladislavsumin.myhomeiot.app.AppConfig
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

    @Suppress("ConstantConditionIf")
    @SuppressLint("CheckResult")
    override fun start() {
        if (!AppConfig.ENABLE_FIREBASE) {
            Log.i(TAG, "Firebase is disabled by build config, skipping initialization")
            return
        }

        mPrivacyPolicyInterractor.observerPrivacyPolicyAccepted()
            .filter { it }
            .firstOrError()
            .ignoreElement()
            .observeOnIo()
            .subscribe {
                initFirebaseApp()
                initCrashlytics()
            }
    }

    private fun initFirebaseApp() {
        Log.i(TAG, "Initializing FirebaseApp")
        val initializeApp = FirebaseApp.initializeApp(mContext)
        if (initializeApp != null) {
            Log.i(TAG, "FirebaseApp initialization successful")
        } else {
            Log.e(TAG, "FirebaseApp initialization unsuccessful")
        }
    }

    private fun initCrashlytics() {
        Log.i(TAG, "Initializing Crashlytics")
        Fabric.with(mContext, Crashlytics())
        Log.i(TAG, "Crashlytics initialization end")
    }

}