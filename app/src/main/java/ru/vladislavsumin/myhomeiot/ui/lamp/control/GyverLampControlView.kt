package ru.vladislavsumin.myhomeiot.ui.lamp.control

import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnectionState
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampState
import ru.vladislavsumin.myhomeiot.ui.core.BaseView

interface GyverLampControlView : BaseView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showGyverLampConnectionState(connectionState: GyverLampConnectionState)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showGyverLampState(state: GyverLampState?)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showSettingsScreen(id: Long)
}