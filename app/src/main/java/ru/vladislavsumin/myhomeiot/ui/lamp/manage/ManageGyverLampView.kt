package ru.vladislavsumin.myhomeiot.ui.lamp.manage

import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.vladislavsumin.myhomeiot.ui.core.BaseView

interface ManageGyverLampView : BaseView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showCheckingState(state: CheckingState)

    enum class CheckingState {
        NOT_CHECKED,
        CHECKING,
        CHECK_FAILED,
        CHECK_SUCCESS,
        INCORRECT_INPUT_DATA,
        SAVING
    }
}