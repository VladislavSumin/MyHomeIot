package ru.vladislavsumin.myhomeiot.ui.main

import android.net.Uri
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.vladislavsumin.myhomeiot.database.entity.GyverLampEntity
import ru.vladislavsumin.myhomeiot.ui.core.BaseView

interface MainActivityView : BaseView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setGyverLamsList(list: List<GyverLampEntity>)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showPrivacyPolicyScreen(privacyPolicyUri: Uri)
}