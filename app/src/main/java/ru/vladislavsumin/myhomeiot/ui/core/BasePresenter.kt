package ru.vladislavsumin.myhomeiot.ui.core

import androidx.annotation.UiThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import moxy.MvpPresenter

abstract class BasePresenter<T : BaseView> : MvpPresenter<T>() {
    private val disposables = CompositeDisposable()

    @UiThread
    protected fun addDisposable(disposable: Disposable) {
        disposables.add(disposable)
    }

    @UiThread
    protected fun Disposable.autoDispose() {
        disposables.add(this)
    }

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }
}