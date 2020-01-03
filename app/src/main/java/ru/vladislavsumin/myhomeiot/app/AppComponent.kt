package ru.vladislavsumin.myhomeiot.app

import dagger.Component
import ru.vladislavsumin.myhomeiot.database.DatabaseModule
import ru.vladislavsumin.myhomeiot.domain.DomainModule
import ru.vladislavsumin.myhomeiot.network.NetworkModule
import ru.vladislavsumin.myhomeiot.strorage.StorageModule
import ru.vladislavsumin.myhomeiot.ui.core.BaseActivity
import ru.vladislavsumin.myhomeiot.ui.frw.FrwPresenter
import ru.vladislavsumin.myhomeiot.ui.lamp.control.GyverLampControlPresenter
import ru.vladislavsumin.myhomeiot.ui.lamp.manage.ManageGyverLampPresenter
import ru.vladislavsumin.myhomeiot.ui.main.MainActivityPresenter
import ru.vladislavsumin.myhomeiot.ui.main.MainFragmentPresenter
import ru.vladislavsumin.myhomeiot.ui.settings.SettingsFragmentPresenter
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
    fun inject(presenter: MainActivityPresenter)
    fun inject(presenter: ManageGyverLampPresenter)
    fun inject(presenter: GyverLampControlPresenter)
    fun inject(presenter: MainFragmentPresenter)
    fun inject(presenter: SettingsFragmentPresenter)
}