package ru.vladislavsumin.myhomeiot.ui.settings

import android.net.Uri
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.vladislavsumin.myhomeiot.ui.core.BaseFragmentView

interface SettingsFragmentView : BaseFragmentView {
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showPrivacyPolicyScreen(privacyPolicyUri: Uri)
}