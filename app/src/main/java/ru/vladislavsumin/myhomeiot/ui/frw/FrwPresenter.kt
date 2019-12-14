package ru.vladislavsumin.myhomeiot.ui.frw

import moxy.InjectViewState
import ru.vladislavsumin.myhomeiot.app.Injector
import ru.vladislavsumin.myhomeiot.domain.privacy.PrivacyPolicyInterractor
import ru.vladislavsumin.myhomeiot.ui.core.BasePresenter
import javax.inject.Inject

@InjectViewState
class FrwPresenter : BasePresenter<FrwView>() {
    @Inject
    lateinit var mPrivacyPolicyInterractor: PrivacyPolicyInterractor

    private var mIsPrivacyPolicyAccepted = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Injector.inject(this)
    }

    fun onClickNextButton() {
        if (mIsPrivacyPolicyAccepted) {
            mPrivacyPolicyInterractor.acceptPrivacyPolicy()
            viewState.goToMainScreen()
        } else {
            //This must be unreachable code
            viewState.setNextButtonEnabled(false)
        }
    }

    fun onClickPrivacyPolicyCheckbox(accepted: Boolean) {
        mIsPrivacyPolicyAccepted = accepted
        viewState.setNextButtonEnabled(accepted)
    }
}