package ru.vladislavsumin.myhomeiot.ui.frw

import android.content.Context
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_frw.*
import moxy.presenter.InjectPresenter
import ru.vladislavsumin.myhomeiot.R
import ru.vladislavsumin.myhomeiot.ui.core.BaseActivity
import ru.vladislavsumin.myhomeiot.ui.frw.privacy.PrivacyPolicyActivity
import ru.vladislavsumin.myhomeiot.ui.main.MainActivity

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
        activity_frw_privacy_policy_checkbox.setOnCheckedChangeListener { _, isAccepted ->
            mPresenter.onClickPrivacyPolicyCheckbox(isAccepted)
        }
        activity_frw_next_btn.setOnClickListener { mPresenter.onClickNextButton() }
        activity_frw_tmp_btn.setOnClickListener { mPresenter.onClickReadPrivacyPolicy() }
    }

    override fun goToMainScreen() {
        startActivity(MainActivity.getLaunchIntent(this))
        finish()
    }

    override fun setNextButtonEnabled(isEnabled: Boolean) {
        activity_frw_next_btn.isEnabled = isEnabled
    }

    override fun openPrivacyPolicyScreen() {
        startActivity(PrivacyPolicyActivity.getLaunchIntent(this))
    }
}