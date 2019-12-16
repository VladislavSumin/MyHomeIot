package ru.vladislavsumin.myhomeiot.ui.lamp.control

import moxy.InjectViewState
import ru.vladislavsumin.myhomeiot.app.Injector
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampInterractor
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampsInterractor
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnectionState
import ru.vladislavsumin.myhomeiot.ui.core.BasePresenter
import ru.vladislavsumin.myhomeiot.utils.observeOnMainThread
import javax.inject.Inject

@InjectViewState
class GyverLampControlPresenter(private val mGyverLampId: Long) :
    BasePresenter<GyverLampControlView>() {

    @Inject
    lateinit var mGyverLampsInterractor: GyverLampsInterractor

    lateinit var mGyverLampInterractor: GyverLampInterractor

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

    private fun onGyverLampStateChange(connectionState: GyverLampConnectionState) {
        viewState.showGyverLampState(connectionState)
    }
}