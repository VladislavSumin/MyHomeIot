package ru.vladislavsumin.myhomeiot.app

import dagger.Component
import ru.vladislavsumin.myhomeiot.strorage.StorageModule
import ru.vladislavsumin.myhomeiot.ui.core.BaseActivity
import javax.inject.Singleton


@Component(
    modules = [
        AppModule::class,
        AndroidServiceModule::class,
        StorageModule::class
    ]
)
@Singleton
interface AppComponent {
    fun inject(activity: BaseActivity)
}