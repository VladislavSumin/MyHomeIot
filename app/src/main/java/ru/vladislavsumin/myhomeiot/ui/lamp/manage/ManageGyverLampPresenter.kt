package ru.vladislavsumin.myhomeiot.ui.lamp.manage

import androidx.annotation.UiThread
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import moxy.InjectViewState
import ru.vladislavsumin.myhomeiot.app.Injector
import ru.vladislavsumin.myhomeiot.database.entity.GyverLampEntity
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampManager
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampsInterractor
import ru.vladislavsumin.myhomeiot.ui.core.BasePresenter
import ru.vladislavsumin.myhomeiot.utils.observeOnMainThread
import java.net.InetAddress
import javax.inject.Inject

@InjectViewState
//TODO добавить проверку на дубликаты
class ManageGyverLampPresenter(private val id: Long) : BasePresenter<ManageGyverLampView>() {
    @Inject
    lateinit var mGyverLampsInterractor: GyverLampsInterractor
    @Inject
    lateinit var mGyverLampManager: GyverLampManager

    private var mCheckStateDisposable: Disposable? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Injector.inject(this)

        if (id == 0L) {
            viewState.showManageState(ManageGyverLampView.ManageState.ADD_NEW)
        } else {
            viewState.showManageState(ManageGyverLampView.ManageState.LOADING)
            mGyverLampManager.observeLamp(id)
                .observeOnMainThread()
                .subscribe(
                    {
                        viewState.showGyverLampEntity(it)
                        viewState.showManageState(ManageGyverLampView.ManageState.EDIT)
                    }, {
                        //TODO add error log
                    }
                )
                .autoDispose()
        }
    }

    @UiThread
    fun onClickCheckConnection(host: String, port: Int) {
        val inetAddress: InetAddress
        try {
            inetAddress = InetAddress.getByName(host)
        } catch (_: Throwable) {
            viewState.showCheckingState(ManageGyverLampView.CheckingState.INCORRECT_INPUT_DATA)
            return
        }

        if (!checkPort(port)) {
            viewState.showCheckingState(ManageGyverLampView.CheckingState.INCORRECT_INPUT_DATA)
            return
        }

        viewState.showCheckingState(ManageGyverLampView.CheckingState.CHECKING)
        mCheckStateDisposable?.dispose()
        mCheckStateDisposable = mGyverLampsInterractor.checkConnection(inetAddress, port)
            .observeOnMainThread()
            .subscribe(
                {
                    viewState.showCheckingState(ManageGyverLampView.CheckingState.CHECK_SUCCESS)
                },
                {
                    viewState.showCheckingState(ManageGyverLampView.CheckingState.CHECK_FAILED)
                }
            )
    }

    fun onClickSave(name: String, host: String, port: Int) {
        //TODO убрать эту копипасту! (см код выше), добавить проверку на пустой хост
        try {
            InetAddress.getByName(host)
        } catch (_: Throwable) {
            viewState.showCheckingState(ManageGyverLampView.CheckingState.INCORRECT_INPUT_DATA)
            return
        }

        if (!checkPort(port)) {
            viewState.showCheckingState(ManageGyverLampView.CheckingState.INCORRECT_INPUT_DATA)
            return
        }

        viewState.showCheckingState(ManageGyverLampView.CheckingState.SAVING)


        val gyverLampEntity = GyverLampEntity(
            id = id,
            name = if (name.isEmpty()) GyverLampEntity.DEFAULT_NAME else name,
            host = host,
            port = port
        )

        val completable: Completable = if (id == 0L) {
            viewState.showManageState(ManageGyverLampView.ManageState.SAVING_NEW)
            mGyverLampManager.addLamp(gyverLampEntity)
        } else {
            viewState.showManageState(ManageGyverLampView.ManageState.SAVING_EXIST)
            mGyverLampManager.updateLamp(gyverLampEntity)
        }

        completable
            .observeOnMainThread()
            .subscribe(
                {
                    viewState.finish()
                }, {
                    //TODO log error
                }
            )
            .autoDispose()
    }

    private fun checkPort(port: Int): Boolean {
        if (port < 0 || port > 65535) {
            viewState.showCheckingState(ManageGyverLampView.CheckingState.INCORRECT_INPUT_DATA)
            return false
        }
        return true
    }

    @UiThread
    fun onTextChanged() {
        viewState.showCheckingState(ManageGyverLampView.CheckingState.NOT_CHECKED)
        mCheckStateDisposable?.dispose()
    }

    override fun onDestroy() {
        mCheckStateDisposable?.dispose()
        super.onDestroy()
    }

    fun onClickSaveDelete() {
        viewState.showCheckingState(ManageGyverLampView.CheckingState.SAVING)
        viewState.showManageState(ManageGyverLampView.ManageState.SAVING_EXIST)

        mGyverLampManager.deleteLamp(id)
            .observeOnMainThread()
            .subscribe(
                {
                    viewState.finish()
                }, {
                    //TODO log error
                }
            )
            .autoDispose()
    }
}