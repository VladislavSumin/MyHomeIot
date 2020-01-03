package ru.vladislavsumin.myhomeiot.ui.lamp.manage

data class ManageGyverLampViewState(
    val manageState: ManageState,
    val checkingState: CheckingState,

    val name: String,
    val host: String,
    val port: String,
    val forceUpdate: Boolean
) {
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