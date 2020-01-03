package ru.vladislavsumin.myhomeiot.ui.main

import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.vladislavsumin.myhomeiot.database.entity.GyverLampEntity
import ru.vladislavsumin.myhomeiot.ui.core.BaseFragmentView
import ru.vladislavsumin.myhomeiot.ui.core.BaseView

interface MainFragmentView : BaseFragmentView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setGyverLampList(list: List<GyverLampEntity>)
}