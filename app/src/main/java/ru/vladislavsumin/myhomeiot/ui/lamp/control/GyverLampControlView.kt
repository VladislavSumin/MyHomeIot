package ru.vladislavsumin.myhomeiot.ui.lamp.control

import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.vladislavsumin.myhomeiot.ui.core.BaseView

interface GyverLampControlView : BaseView {
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showSettingsScreen(id: Long)
}