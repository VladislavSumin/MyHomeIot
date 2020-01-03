package ru.vladislavsumin.myhomeiot.ui.core

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.annotation.UiThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import moxy.MvpAppCompatActivity
import ru.vladislavsumin.myhomeiot.app.Injector
import ru.vladislavsumin.myhomeiot.domain.privacy.PrivacyPolicyInterractor
import ru.vladislavsumin.myhomeiot.ui.frw.FrwActivity
import javax.inject.Inject

abstract class BaseActivity : MvpAppCompatActivity(), BaseView {
    private val disposables = CompositeDisposable()

    @Inject
    lateinit var mPrivacyPolicyInterractor: PrivacyPolicyInterractor

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Injector.inject(this)

        if (!mPrivacyPolicyInterractor.isPrivacyPolicyAccepted() && this !is FrwActivity) {
            startActivity(FrwActivity.getLaunchIntent(this))
            finish()
        }

        setContentView(getLayoutResId())
    }

    @LayoutRes
    abstract fun getLayoutResId(): Int

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
        Toast.makeText(this, text, duration).show()
    }

    override fun startActivity(factory: (context: Context) -> Intent) {
        startActivity(factory(this))
    }

    override fun setTitle(title: String) {
        this.title = title
    }
}