package ru.vladislavsumin.myhomeiot.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_settings.*
import moxy.presenter.InjectPresenter
import ru.vladislavsumin.myhomeiot.R
import ru.vladislavsumin.myhomeiot.app.AppConfig
import ru.vladislavsumin.myhomeiot.ui.core.BaseFragment
import ru.vladislavsumin.myhomeiot.ui.utils.setClickableLinkListener

class SettingsFragment : BaseFragment(), SettingsFragmentView{
    companion object {
        private const val LAYOUT = R.layout.fragment_settings
    }

    @InjectPresenter
    lateinit var mPresenter: SettingsFragmentPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUi()
        setupUx()

        super.onViewCreated(view, savedInstanceState)
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