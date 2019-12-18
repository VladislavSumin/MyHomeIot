package ru.vladislavsumin.myhomeiot.ui.frw.privacy

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import kotlinx.android.synthetic.main.activity_privacy_policy.*
import ru.vladislavsumin.myhomeiot.R
import ru.vladislavsumin.myhomeiot.ui.core.ToolbarActivity

class PrivacyPolicyActivity : ToolbarActivity() {
    companion object {
        private const val LAYOUT = R.layout.activity_privacy_policy

        fun getLaunchIntent(context: Context): Intent {
            return Intent(context, PrivacyPolicyActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LAYOUT)

        val reader = resources.openRawResource(R.raw.privacy_policy).reader()
        val privacyPolicyText = reader.use {
            it.readText()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            activity_privacy_policy_text.text = Html.fromHtml(
                privacyPolicyText,
                Html.FROM_HTML_MODE_COMPACT
            )
        } else {
            activity_privacy_policy_text.text = Html.fromHtml(privacyPolicyText)
        }
        activity_privacy_policy_text.movementMethod = LinkMovementMethod.getInstance()
    }
}