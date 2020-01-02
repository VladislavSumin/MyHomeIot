package ru.vladislavsumin.myhomeiot.ui.main

import moxy.InjectViewState
import ru.vladislavsumin.myhomeiot.app.Injector
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampManager
import ru.vladislavsumin.myhomeiot.ui.core.BasePresenter
import ru.vladislavsumin.myhomeiot.utils.observeOnMainThread
import javax.inject.Inject

@InjectViewState
class MainFragmentPresenter : BasePresenter<MainFragmentView>() {
    @Inject
    lateinit var mGyverLampManager: GyverLampManager

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Injector.inject(this)

        mGyverLampManager
            .observeLamps()
            .observeOnMainThread()
            .subscribe {
                viewState.setGyverLampList(it)
            }
            .autoDispose()
    }
}