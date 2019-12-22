package ru.vladislavsumin.myhomeiot.ui.lamp.manage

import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.vladislavsumin.myhomeiot.database.entity.GyverLampEntity
import ru.vladislavsumin.myhomeiot.ui.core.BaseView

interface ManageGyverLampView : BaseView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showCheckingState(state: CheckingState)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showManageState(state: ManageState)

    /**
     * One execution when not override user changes after change screen orientation
     */
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showGyverLampEntity(gyverLampEntity: GyverLampEntity)

    // Default state NOT_CHECKED
    enum class CheckingState {
        NOT_CHECKED,
        CHECKING,
        CHECK_FAILED,
        CHECK_SUCCESS,
        INCORRECT_INPUT_DATA,
        SAVING // == DELETING
    }

    // Default UNDEFINED
    enum class ManageState {
        LOADING,
        ADD_NEW,
        EDIT,

        SAVING_NEW,
        SAVING_EXIST // == DELETING_EXIST
    }
}