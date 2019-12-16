package ru.vladislavsumin.myhomeiot.ui.lamp.manage

import androidx.annotation.UiThread
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
//TODO добавить обработку id / переиминовать активити в ManageGyverLampActivity
//TODO добавить проверку на дубликаты
class AddGyverLampPresenter(private val id: Long?) : BasePresenter<AddGyverLampView>() {
    @Inject
    lateinit var mGyverLampsInterractor: GyverLampsInterractor
    @Inject
    lateinit var mGyverLampManager: GyverLampManager

    private var mCheckStateDisposable: Disposable? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Injector.inject(this)
    }

    @UiThread
    fun onClickCheckConnection(host: String, port: Int) {
        val inetAddress: InetAddress
        try {
            inetAddress = InetAddress.getByName(host)
        } catch (_: Throwable) {
            viewState.showCheckingState(AddGyverLampView.CheckingState.INCORRECT_INPUT_DATA)
            return
        }

        if (!checkPort(port)) {
            viewState.showCheckingState(AddGyverLampView.CheckingState.INCORRECT_INPUT_DATA)
            return
        }

        viewState.showCheckingState(AddGyverLampView.CheckingState.CHECKING)
        mCheckStateDisposable?.dispose()
        mCheckStateDisposable = mGyverLampsInterractor.checkConnection(inetAddress, port)
            .observeOnMainThread()
            .subscribe(
                {
                    viewState.showCheckingState(AddGyverLampView.CheckingState.CHECK_SUCCESS)
                },
                {
                    viewState.showCheckingState(AddGyverLampView.CheckingState.CHECK_FAILED)
                }
            )
    }

    fun onClickSave(name: String, host: String, port: Int) {
        //TODO убрать эту копипасту! (см код выше), добавить проверку на пустой хост
        try {
            InetAddress.getByName(host)
        } catch (_: Throwable) {
            viewState.showCheckingState(AddGyverLampView.CheckingState.INCORRECT_INPUT_DATA)
            return
        }

        if (!checkPort(port)) {
            viewState.showCheckingState(AddGyverLampView.CheckingState.INCORRECT_INPUT_DATA)
            return
        }

        viewState.showCheckingState(AddGyverLampView.CheckingState.SAVING)

        val gyverLampEntity = GyverLampEntity(
            name = if (name.isEmpty()) GyverLampEntity.DEFAULT_NAME else name,
            host = host,
            port = port
        )

        mGyverLampManager.addLamp(gyverLampEntity)
            .observeOnMainThread()
            .subscribe { viewState.finish() }
            .autoDispose()
    }

    private fun checkPort(port: Int): Boolean {
        if (port < 0 || port > 65535) {
            viewState.showCheckingState(AddGyverLampView.CheckingState.INCORRECT_INPUT_DATA)
            return false
        }
        return true
    }

    @UiThread
    fun onTextChanged() {
        viewState.showCheckingState(AddGyverLampView.CheckingState.NOT_CHECKED)
        mCheckStateDisposable?.dispose()
    }

    override fun onDestroy() {
        mCheckStateDisposable?.dispose()
        super.onDestroy()
    }
}