package ru.vladislavsumin.myhomeiot.ui.core

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.annotation.UiThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import moxy.MvpAppCompatActivity

abstract class BaseActivity : MvpAppCompatActivity(), BaseView {
    private val disposables = CompositeDisposable()

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @UiThread
    protected fun addDisposable(disposable: Disposable) {
        disposables.add(disposable)
    }

    @UiThread
    protected fun Disposable.autoDispose() {
        disposables.add(this)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        setupUi()
        setupUx()
    }

    protected open fun setupUi() {}
    protected open fun setupUx() {}

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }

    override fun showToast(text: String, duration: Int) {
        Toast.makeText(this, text, duration).show()
    }

    override fun startActivity(factory: (context: Context) -> Intent) {
        startActivity(factory(this))
    }
}