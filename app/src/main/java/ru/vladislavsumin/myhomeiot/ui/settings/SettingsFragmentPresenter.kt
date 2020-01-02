package ru.vladislavsumin.myhomeiot.ui.settings

import ru.vladislavsumin.myhomeiot.app.Injector
import ru.vladislavsumin.myhomeiot.domain.privacy.PrivacyPolicyInterractor
import ru.vladislavsumin.myhomeiot.ui.core.BasePresenter
import javax.inject.Inject

class SettingsFragmentPresenter : BasePresenter<SettingsFragmentView>() {

    @Inject
    lateinit var mProPolicyInterractor: PrivacyPolicyInterractor

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Injector.inject(this)
    }

    fun onClickReadPrivacyPolicy() {
        viewState.showPrivacyPolicyScreen(mProPolicyInterractor.getPrivacyPolicyUri())
    }
}