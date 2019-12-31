package ru.vladislavsumin.myhomeiot.ui.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_content.*
import moxy.presenter.InjectPresenter
import ru.vladislavsumin.myhomeiot.ui.lamp.manage.ManageGyverLampActivity
import ru.vladislavsumin.myhomeiot.ui.lamp.control.GyverLampControlActivity
import ru.vladislavsumin.myhomeiot.R
import ru.vladislavsumin.myhomeiot.app.AppConfig
import ru.vladislavsumin.myhomeiot.database.entity.GyverLampEntity
import ru.vladislavsumin.myhomeiot.ui.core.ToolbarActivity
import ru.vladislavsumin.myhomeiot.ui.utils.setClickableLinkListener

class MainActivity : ToolbarActivity(), MainActivityView {
    companion object {
        private const val LAYOUT = R.layout.activity_main

        fun getLaunchIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    @InjectPresenter
    lateinit var mPresenter: MainActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LAYOUT)

        setupUi()
        setupUx()
        setFragment()
    }

    private fun setFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.activity_main_fragment_container, MainFragment.getInstance())
            .commit()
    }

    private fun setupUi() {
        val appVersion = getString(R.string.app_version).format(AppConfig.VERSION_NAME)
        activity_main_app_version_name.text = appVersion
    }

    private fun setupUx() {
        activity_main_privacy_policy.setClickableLinkListener {
            mPresenter.onClickReadPrivacyPolicy()
        }
    }

    override fun showPrivacyPolicyScreen(privacyPolicyUri: Uri) {
        val browserIntent = Intent(Intent.ACTION_VIEW, privacyPolicyUri)
        startActivity(browserIntent)
    }
}