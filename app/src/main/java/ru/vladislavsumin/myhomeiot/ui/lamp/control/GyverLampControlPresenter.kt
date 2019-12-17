package ru.vladislavsumin.myhomeiot.ui.lamp.control

import moxy.InjectViewState
import ru.vladislavsumin.myhomeiot.app.Injector
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampInterractor
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampsInterractor
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnectionState
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampState
import ru.vladislavsumin.myhomeiot.ui.core.BasePresenter
import ru.vladislavsumin.myhomeiot.utils.observeOnMainThread
import javax.inject.Inject

@InjectViewState
class GyverLampControlPresenter(private val mGyverLampId: Long) :
    BasePresenter<GyverLampControlView>() {

    //TODO сделать один ViewState!

    @Inject
    lateinit var mGyverLampsInterractor: GyverLampsInterractor

    private lateinit var mGyverLampInterractor: GyverLampInterractor

    private var mGyverLampState: GyverLampState? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Injector.inject(this)
        mGyverLampInterractor = mGyverLampsInterractor.getGyverLampInterractor(mGyverLampId)

        mGyverLampInterractor
            .observeConnectionState()
            .observeOnMainThread()
            .subscribe(this::onGyverLampStateChange)
            .autoDispose()
    }

    private fun onGyverLampStateChange(state: Pair<GyverLampConnectionState, GyverLampState?>) {
        mGyverLampState = state.second
        viewState.showGyverLampConnectionState(state.first)
        viewState.showGyverLampState(state.second)
    }

    fun onClickOnOffButton() {
        val lampState = mGyverLampState
        if (lampState != null) {
            if (lampState.isOn) {
                mGyverLampInterractor.observeTurnOff()
                    .subscribe()
                    .autoDispose()
            } else {
                mGyverLampInterractor.observeTurnOn()
                    .subscribe()
                    .autoDispose()
            }
        }
    }
}