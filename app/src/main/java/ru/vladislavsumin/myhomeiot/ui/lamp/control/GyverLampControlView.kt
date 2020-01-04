package ru.vladislavsumin.myhomeiot.ui.lamp.control

import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.vladislavsumin.myhomeiot.ui.core.BaseActivity
import ru.vladislavsumin.myhomeiot.ui.core.BaseActivityView
import ru.vladislavsumin.myhomeiot.ui.core.BaseView

interface GyverLampControlView : BaseActivityView {
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showSettingsScreen(id: Long)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showAlarmsScreen(id: Long)
}