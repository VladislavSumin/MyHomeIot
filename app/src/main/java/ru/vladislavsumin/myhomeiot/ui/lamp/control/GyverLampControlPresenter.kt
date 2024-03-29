package ru.vladislavsumin.myhomeiot.ui.lamp.control

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import moxy.InjectViewState
import ru.vladislavsumin.myhomeiot.app.Injector
import ru.vladislavsumin.myhomeiot.database.entity.GyverLampEntity
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.*
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnectionState
import ru.vladislavsumin.myhomeiot.ui.core.BasePresenter
import ru.vladislavsumin.myhomeiot.utils.observeOnMainThread
import javax.inject.Inject

@InjectViewState
class GyverLampControlPresenter(private val mGyverLampId: Long) :
    BasePresenter<GyverLampControlView>() {

    //TODO сделать один ViewState!

    @Inject
    lateinit var mGyverLampsInterractor: GyverLampsInterractor

    @Inject
    lateinit var mGyverLampManager: GyverLampManager

    private lateinit var mGyverLampInterractor: GyverLampInterractor

    private lateinit var mStateObservable: Observable<Pair<GyverLampConnectionState, GyverLampState?>>
    private var mGyverLampEntity: GyverLampEntity? = null

    private var mGyverLampState: GyverLampState? = null
    private var mChangeBrightnessDisposable: Disposable? = null
    private var mChangeScaleDisposable: Disposable? = null
    private var mChangeSpeedDisposable: Disposable? = null
    private var mChangeModeDisposable: Disposable? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Injector.inject(this)
        mGyverLampInterractor = mGyverLampsInterractor.getGyverLampInterractor(mGyverLampId)

        mStateObservable = mGyverLampInterractor
            .observeConnectionState()
            .observeOnMainThread()
            .doOnError(this::onError)
            .doOnNext { mGyverLampState = it.second }

        mGyverLampManager.observeLamp(mGyverLampId)
            .observeOnMainThread()
            .subscribe(
                {
                    mGyverLampEntity = it
                    viewState.setTitle(it.name)
                }, this::onError
            )
            .autoDispose()
    }

    fun observeState() = mStateObservable

    private fun onError(t: Throwable) {
        when (t) {
            is GyverLampManager.LampNotFoundException -> viewState.finish()
            else -> throw t
        }
    }

    fun onClickOnOffButton() {
        val lampState = mGyverLampState
        if (lampState != null) {
            if (lampState.isOn) {
                mGyverLampInterractor.observeTurnOff()
                    .subscribe({}, {})
                    .autoDispose()
            } else {
                mGyverLampInterractor.observeTurnOn()
                    .subscribe({}, {})
                    .autoDispose()
            }
        }
    }

    override fun onDestroy() {
        mChangeBrightnessDisposable?.dispose()
        mChangeScaleDisposable?.dispose()
        mChangeSpeedDisposable?.dispose()
        mChangeModeDisposable?.dispose()
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

    fun onChangeMode(mode: GyverLampMode) {
        val lampState = mGyverLampState ?: return
        if (lampState.mode == mode) return

        mChangeModeDisposable?.dispose()
        mChangeSpeedDisposable = mGyverLampInterractor.observeChangeMode(mode)
            .subscribe({}, {})
    }

    fun onClickSettingsButton() {
        val gyverLampEntity = mGyverLampEntity ?: return
        viewState.showSettingsScreen(gyverLampEntity.id)
    }
}