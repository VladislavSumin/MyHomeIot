package ru.vladislavsumin.myhomeiot.app

import android.app.Application
import android.util.Log
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        setupRxJava()
        initDagger()
    }

    private fun setupRxJava() {
        RxJavaPlugins.setErrorHandler { throwable ->
            when (throwable) {
                is UndeliverableException -> Log.d(
                    RX_JAVA_ERROR_HANDLER_TAG,
                    "RxJavaUndeliverable",
                    throwable
                )
                else -> {
                    Log.e(RX_JAVA_ERROR_HANDLER_TAG, "Unknown rxJava exception", throwable)
                    throw throwable
                }
            }
        }
    }

    private fun initDagger() {
        Log.i(TAG, "Initializing Dagger")
        Injector = DaggerAppComponent.builder()
            .appModule(AppModule(applicationContext))
            .build()
        Log.i(TAG, "Dagger initialized")
    }

    companion object {
        private val TAG = App::class.java.simpleName
        private const val RX_JAVA_ERROR_HANDLER_TAG = "RxJavaErrorHandler"
    }
}