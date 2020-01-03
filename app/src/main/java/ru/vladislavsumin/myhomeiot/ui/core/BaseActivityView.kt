package ru.vladislavsumin.myhomeiot.ui.core

import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

interface BaseActivityView : BaseView {
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun finish()
}