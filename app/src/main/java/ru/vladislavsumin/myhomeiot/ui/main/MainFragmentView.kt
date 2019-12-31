package ru.vladislavsumin.myhomeiot.ui.main

import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.vladislavsumin.myhomeiot.database.entity.GyverLampEntity
import ru.vladislavsumin.myhomeiot.ui.core.BaseView

interface MainFragmentView : BaseView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setGyverLamsList(list: List<GyverLampEntity>)
}