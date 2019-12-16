package ru.vladislavsumin.myhomeiot.app

import dagger.Component
import ru.vladislavsumin.myhomeiot.database.DatabaseModule
import ru.vladislavsumin.myhomeiot.domain.DomainModule
import ru.vladislavsumin.myhomeiot.network.NetworkModule
import ru.vladislavsumin.myhomeiot.strorage.StorageModule
import ru.vladislavsumin.myhomeiot.ui.core.BaseActivity
import ru.vladislavsumin.myhomeiot.ui.frw.FrwPresenter
import javax.inject.Singleton


@Component(
    modules = [
        AppModule::class,
        AndroidServiceModule::class,
        DatabaseModule::class,
        DomainModule::class,
        NetworkModule::class,
        StorageModule::class
    ]
)
@Singleton
interface AppComponent {
    fun inject(app: App)

    fun inject(activity: BaseActivity)

    fun inject(presenter: FrwPresenter)
}