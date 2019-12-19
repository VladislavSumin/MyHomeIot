package ru.vladislavsumin.myhomeiot.ui.frw

import android.net.Uri
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.vladislavsumin.myhomeiot.domain.privacy.PrivacyPolicyInterractor
import ru.vladislavsumin.myhomeiot.ui.core.BaseView

interface FrwView : BaseView {
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun goToMainScreen()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setNextButtonEnabled(isEnabled: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun openPrivacyPolicyScreen(uri: Uri)
}