package ru.vladislavsumin.myhomeiot.ui.frw

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_frw.*
import moxy.presenter.InjectPresenter
import ru.vladislavsumin.myhomeiot.R
import ru.vladislavsumin.myhomeiot.ui.core.BaseActivity
import ru.vladislavsumin.myhomeiot.ui.main.MainActivity
import ru.vladislavsumin.myhomeiot.ui.utils.setClickableLinkListener


class FrwActivity : BaseActivity(), FrwView {
    companion object {
        private const val LAYOUT = R.layout.activity_frw

        fun getLaunchIntent(context: Context): Intent {
            return Intent(context, FrwActivity::class.java)
        }
    }

    @InjectPresenter
    lateinit var mPresenter: FrwPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LAYOUT)
        activity_frw_privacy_policy_checkbox.setClickableLinkListener {
            mPresenter.onClickReadPrivacyPolicy()
        }

        activity_frw_privacy_policy_checkbox.setOnCheckedChangeListener { _, isAccepted ->
            mPresenter.onClickPrivacyPolicyCheckbox(isAccepted)
        }
        activity_frw_next_btn.setOnClickListener { mPresenter.onClickNextButton() }

    }

    override fun goToMainScreen() {
        startActivity(MainActivity.getLaunchIntent(this))
        finish()
    }

    override fun setNextButtonEnabled(isEnabled: Boolean) {
        activity_frw_next_btn.isEnabled = isEnabled
    }

    override fun openPrivacyPolicyScreen(uri: Uri) {
        val browserIntent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(browserIntent)
    }
}