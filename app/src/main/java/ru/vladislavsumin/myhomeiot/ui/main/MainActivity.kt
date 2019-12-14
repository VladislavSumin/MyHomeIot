package ru.vladislavsumin.myhomeiot.ui.main

import android.content.Context
import android.content.Intent
import ru.vladislavsumin.myhomeiot.ui.core.BaseActivity

class MainActivity : BaseActivity() {
    companion object {
        fun getLaunchIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}