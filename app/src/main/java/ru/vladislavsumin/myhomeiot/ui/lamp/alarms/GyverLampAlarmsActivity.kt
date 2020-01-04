package ru.vladislavsumin.myhomeiot.ui.lamp.alarms

import android.content.Context
import android.content.Intent
import ru.vladislavsumin.myhomeiot.R
import ru.vladislavsumin.myhomeiot.ui.core.ToolbarActivity

class GyverLampAlarmsActivity : ToolbarActivity() {
    companion object {
        private const val LAYOUT = R.layout.activity_gyver_lamp_alarms
        private const val GYVER_LAMP_ID = "gyver_lamp_id"

        fun getLaunchIntent(context: Context, id: Long): Intent {
            return Intent(context, GyverLampAlarmsActivity::class.java).apply {
                putExtra(GYVER_LAMP_ID, id)
            }
        }
    }

    override fun getLayoutResId(): Int {
        return LAYOUT
    }
}