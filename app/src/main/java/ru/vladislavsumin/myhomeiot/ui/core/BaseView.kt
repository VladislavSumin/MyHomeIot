package ru.vladislavsumin.myhomeiot.ui.core

import android.content.Context
import android.content.Intent
import android.widget.Toast
import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

interface BaseView : MvpView {
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showToast(text: String, duration: Int = Toast.LENGTH_SHORT)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun startActivity(factory: (context: Context) -> Intent)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun finish()
}