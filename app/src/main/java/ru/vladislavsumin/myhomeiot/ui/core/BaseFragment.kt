package ru.vladislavsumin.myhomeiot.ui.core

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.UiThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import moxy.MvpAppCompatFragment

abstract class BaseFragment : MvpAppCompatFragment(), BaseView {
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

    override fun showToast(text: String, duration: Int) {
        Toast.makeText(requireContext(), text, duration).show()
    }

    override fun startActivity(factory: (context: Context) -> Intent) {
        startActivity(factory(requireActivity()))
    }

    override fun setTitle(title: String) {
        requireActivity().title = title
    }
}