package ru.vladislavsumin.myhomeiot.ui.lamp.manage

import androidx.annotation.UiThread
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import moxy.InjectViewState
import ru.vladislavsumin.myhomeiot.app.Injector
import ru.vladislavsumin.myhomeiot.database.entity.GyverLampEntity
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampManager
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampsInterractor
import ru.vladislavsumin.myhomeiot.network.NetworkAddressVerifier
import ru.vladislavsumin.myhomeiot.ui.core.BasePresenter
import ru.vladislavsumin.myhomeiot.utils.observeOnMainThread
import javax.inject.Inject

@InjectViewState
//TODO добавить проверку на дубликаты
class ManageGyverLampPresenter(private val id: Long) : BasePresenter<ManageGyverLampView>() {
    @Inject
    lateinit var mGyverLampsInterractor: GyverLampsInterractor
    @Inject
    lateinit var mGyverLampManager: GyverLampManager
    @Inject
    lateinit var mNetworkAddressVerifier: NetworkAddressVerifier

    private val mViewState: BehaviorSubject<ManageGyverLampViewState> = BehaviorSubject.create()

    private var mCheckStateDisposable: Disposable? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Injector.inject(this)

        if (id == 0L) {
            showAddNewState()
        } else {
            showLoadingState()
            mGyverLampManager.getLamp(id)
                .observeOnMainThread()
                .subscribe(this::showLoadedState, this::onError)
                .autoDispose()
        }
    }

    fun observeViewState(): Observable<ManageGyverLampViewState> = mViewState

    @UiThread
    fun onClickCheckConnection() {
        if (!validate()) {
            showCheckingState(ManageGyverLampViewState.CheckingState.INCORRECT_INPUT_DATA)
        }

        val state = mViewState.value!!

        showCheckingState(ManageGyverLampViewState.CheckingState.CHECKING)
        mCheckStateDisposable?.dispose()
        mCheckStateDisposable = mGyverLampsInterractor.checkConnection(state.host, state.port)
            .observeOnMainThread()
            .subscribe(
                { showCheckingState(ManageGyverLampViewState.CheckingState.CHECK_SUCCESS) },
                { showCheckingState(ManageGyverLampViewState.CheckingState.CHECK_FAILED) }
            )
    }

    fun onClickSave() {
        if (!validate()) {
            showCheckingState(ManageGyverLampViewState.CheckingState.INCORRECT_INPUT_DATA)
        }

        val state = mViewState.value!!

        val gyverLampEntity = GyverLampEntity(
            id = id,
            name = if (state.name.isEmpty()) GyverLampEntity.DEFAULT_NAME else state.name,
            host = state.host,
            port = state.port,
            deviceType = GyverLampEntity.DeviceType.GYVER_LAMP_ORIGIN
        )

        val completable: Completable = if (id == 0L) {
            mGyverLampManager.addLamp(gyverLampEntity)
        } else {
            mGyverLampManager.updateLamp(gyverLampEntity)
        }

        showSavingState()

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


    @UiThread
    private fun validate(): Boolean {
        val state = mViewState.value!!

        // validate ip
        if (!mNetworkAddressVerifier.verifyIsHostOrIp(state.host)) return false

        // validate port
        if (state.port < 0 || state.port > 65535) return false

        return true
    }

    @UiThread
    fun onTextChanged(name: String, host: String, port: Int) {
        val state = mViewState.value!!
        val checkedDataChanged = host != state.host || port != state.port

        mViewState.onNext(
            state.copy(
                checkingState = if (checkedDataChanged)
                    ManageGyverLampViewState.CheckingState.NOT_CHECKED
                else state.checkingState,

                name = name,
                host = host,
                port = port,
                forceUpdate = false
            )
        )

        if (checkedDataChanged) mCheckStateDisposable?.dispose()
    }

    override fun onDestroy() {
        mCheckStateDisposable?.dispose()
        super.onDestroy()
    }

    fun onClickSaveDelete() {
        showSavingState()//TODO add delete state
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

    private fun onError(t: Throwable) {
//TODO
    }

    @UiThread
    private fun showAddNewState() {
        mViewState.onNext(
            ManageGyverLampViewState(
                ManageGyverLampViewState.ManageState.ADD_NEW,
                ManageGyverLampViewState.CheckingState.NOT_CHECKED,
                "",
                "",
                8888,
                true
            )
        )
    }

    @UiThread
    private fun showLoadingState() {
        mViewState.onNext(
            ManageGyverLampViewState(
                ManageGyverLampViewState.ManageState.LOADING,
                ManageGyverLampViewState.CheckingState.NOT_CHECKED,
                "",
                "",
                0,
                true
            )
        )
    }

    @UiThread
    private fun showLoadedState(gyverLampEntity: GyverLampEntity) {
        mViewState.onNext(
            ManageGyverLampViewState(
                ManageGyverLampViewState.ManageState.EDIT,
                ManageGyverLampViewState.CheckingState.NOT_CHECKED,
                gyverLampEntity.name,
                gyverLampEntity.host,
                gyverLampEntity.port,
                true
            )
        )
    }

    @UiThread
    private fun showSavingState() {
        mViewState.onNext(
            mViewState.value!!.copy(
                manageState = if (id == 0L) ManageGyverLampViewState.ManageState.SAVING_NEW
                else ManageGyverLampViewState.ManageState.SAVING_EXIST,
                checkingState = ManageGyverLampViewState.CheckingState.SAVING,
                forceUpdate = false
            )
        )
    }

    @UiThread
    private fun showCheckingState(checkingState: ManageGyverLampViewState.CheckingState) {
        mViewState.onNext(
            mViewState.value!!.copy(checkingState = checkingState, forceUpdate = false)
        )
    }
}