package ru.vladislavsumin.myhomeiot.ui.lamp.alarms

import moxy.InjectViewState
import ru.vladislavsumin.myhomeiot.ui.core.BasePresenter

@InjectViewState
class GyverLampAlarmsPresenter(private val mGyverLampId: Long) :
    BasePresenter<GyverLampAlarmsView>()