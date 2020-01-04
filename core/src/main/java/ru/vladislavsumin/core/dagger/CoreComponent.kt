package ru.vladislavsumin.core.dagger

import android.content.Context
import dagger.Component

@Component(modules = [AppModule::class])
interface CoreComponent {
    fun getContext(): Context
}