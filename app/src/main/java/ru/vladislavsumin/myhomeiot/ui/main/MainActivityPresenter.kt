package ru.vladislavsumin.myhomeiot.ui.main

import moxy.InjectViewState
import ru.vladislavsumin.myhomeiot.app.Injector
import ru.vladislavsumin.myhomeiot.domain.privacy.PrivacyPolicyInterractor
import ru.vladislavsumin.myhomeiot.ui.core.BasePresenter
import javax.inject.Inject

@InjectViewState
class MainActivityPresenter : BasePresenter<MainActivityView>() {
    @Inject
    lateinit var mProPolicyInterractor: PrivacyPolicyInterractor

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Injector.inject(this)
    }
}