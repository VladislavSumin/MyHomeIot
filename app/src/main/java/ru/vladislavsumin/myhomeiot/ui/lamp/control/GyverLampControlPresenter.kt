package ru.vladislavsumin.myhomeiot.ui.lamp.control

import io.reactivex.disposables.Disposable
import moxy.InjectViewState
import ru.vladislavsumin.myhomeiot.app.Injector
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampInterractor
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampsInterractor
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnectionState
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampState
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
    private var mChangeBrightnessDisposable: Disposable? = null
    private var mChangeScaleDisposable: Disposable? = null
    private var mChangeSpeedDisposable: Disposable? = null

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
        //TODO add error handling!
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

    override fun onDestroy() {
        mChangeBrightnessDisposable?.dispose()
        mChangeScaleDisposable?.dispose()
        mChangeSpeedDisposable?.dispose()
        super.onDestroy()
    }

    fun onChangeBrightness(brightness: Int) {
        val lampState = mGyverLampState ?: return
        if (lampState.brightness == brightness) return

        mChangeBrightnessDisposable?.dispose()
        mChangeBrightnessDisposable = mGyverLampInterractor.observeChangeBrightness(brightness)
            .subscribe({}, {})
    }

    fun onChangeScale(scale: Int) {
        val lampState = mGyverLampState ?: return
        if (lampState.scale == scale) return

        mChangeScaleDisposable?.dispose()
        mChangeScaleDisposable = mGyverLampInterractor.observeChangeScale(scale)
            .subscribe({}, {})
    }

    fun onChangeSpeed(speed: Int) {
        val lampState = mGyverLampState ?: return
        if (lampState.speed == speed) return

        mChangeSpeedDisposable?.dispose()
        mChangeSpeedDisposable = mGyverLampInterractor.observeChangeSpeed(speed)
            .subscribe({}, {})
    }
}