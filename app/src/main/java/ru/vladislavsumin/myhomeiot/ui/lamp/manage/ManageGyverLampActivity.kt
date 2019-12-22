package ru.vladislavsumin.myhomeiot.ui.lamp.manage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_manage_gyver_lamp.*
import kotlinx.android.synthetic.main.loading.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.vladislavsumin.myhomeiot.R
import ru.vladislavsumin.myhomeiot.database.entity.GyverLampEntity
import ru.vladislavsumin.myhomeiot.ui.core.ToolbarActivity

//TODO поправить логику отображения, при добавлении проверки на дубликаты
class ManageGyverLampActivity : ToolbarActivity(),
    ManageGyverLampView {
    companion object {
        private const val LAYOUT = R.layout.activity_manage_gyver_lamp

        private const val GYVER_LAMP_ID = "gyver_lamp_id"

        fun getLaunchIntent(context: Context): Intent {
            return getLaunchIntent(context, 0)
        }

        fun getLaunchIntent(context: Context, id: Long): Intent {
            return Intent(context, ManageGyverLampActivity::class.java).apply {
                putExtra(GYVER_LAMP_ID, id)
            }
        }
    }

    @ProvidePresenter
    fun providePresenter(): ManageGyverLampPresenter {
        return ManageGyverLampPresenter(
            intent.getLongExtra(GYVER_LAMP_ID, 0)
        )
    }

    @InjectPresenter
    lateinit var mPresenter: ManageGyverLampPresenter

    private var mMenuItemSave: MenuItem? = null
    private var mMenuItemDelete: MenuItem? = null
    private var mManageState: ManageGyverLampView.ManageState =
        ManageGyverLampView.ManageState.LOADING


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LAYOUT)

        activity_manage_gyver_lamp_check_connection.setOnClickListener { onClickCheckConnection() }
        activity_manage_gyver_lamp_save.setOnClickListener { onClickSave() }
        activity_manage_gyver_lamp_ip.editText!!.addTextChangedListener(
            ChangeTextWatcher(
                mPresenter::onTextChanged
            )
        )
        activity_manage_gyver_lamp_port.editText!!.addTextChangedListener(
            ChangeTextWatcher(
                mPresenter::onTextChanged
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.gyver_lamp_manage, menu)
        mMenuItemDelete = menu.findItem(R.id.gyver_lamp_manage_delete)
        mMenuItemSave = menu.findItem(R.id.gyver_lamp_manage_save)
        setMenuItemVisibility(mManageState)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.gyver_lamp_manage_save -> {
                onClickSave()
                true
            }
            R.id.gyver_lamp_manage_delete -> {
                mPresenter.onClickSaveDelete()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * This need because onCreateOptionsMenu() call after mPresenter.onFirstViewAttach()
     */
    private fun setMenuItemVisibility(state: ManageGyverLampView.ManageState) {
        when (state) {
            ManageGyverLampView.ManageState.LOADING,
            ManageGyverLampView.ManageState.ADD_NEW,
            ManageGyverLampView.ManageState.SAVING_NEW -> {
                mMenuItemDelete?.isVisible = false
                mMenuItemSave?.isVisible = false
            }
            ManageGyverLampView.ManageState.EDIT -> {
                mMenuItemDelete?.isVisible = true
                mMenuItemSave?.isVisible = true

                mMenuItemDelete?.isEnabled = true
                mMenuItemSave?.isEnabled = true
            }
            ManageGyverLampView.ManageState.SAVING_EXIST -> {
                mMenuItemDelete?.isVisible = true
                mMenuItemSave?.isVisible = true

                //TODO добавить tint для выключенных кнопок
                mMenuItemDelete?.isEnabled = false
                mMenuItemSave?.isEnabled = false
            }
        }!!
    }

    private fun onClickSave() {

        val name = activity_manage_gyver_lamp_name.editText!!.text.toString()
        val host = activity_manage_gyver_lamp_ip.editText!!.text.toString()
        val port = activity_manage_gyver_lamp_port.editText!!.text.toString().toInt()

        mPresenter.onClickSave(name, host, port)
    }

    private fun onClickCheckConnection() {
        val host = activity_manage_gyver_lamp_ip.editText!!.text.toString()
        val port = activity_manage_gyver_lamp_port.editText!!.text.toString().toInt()

        mPresenter.onClickCheckConnection(host, port)
    }

    @Suppress("DEPRECATION")
    override fun showCheckingState(state: ManageGyverLampView.CheckingState) {
        //TODO добавить цветовою индикацию с цветами темы!
        when (state) {
            ManageGyverLampView.CheckingState.NOT_CHECKED -> {
                activity_manage_gyver_lamp_check_connection.isEnabled = true
                activity_manage_gyver_lamp_status_text.visibility = View.GONE
            }
            ManageGyverLampView.CheckingState.CHECKING -> {
                activity_manage_gyver_lamp_check_connection.isEnabled = false
                activity_manage_gyver_lamp_status_text.apply {
                    visibility = View.VISIBLE
                    text = getText(R.string.check_connection_status_checking)
                    setTextColor(resources.getColor(android.R.color.black))
                }
            }
            ManageGyverLampView.CheckingState.CHECK_FAILED -> {
                activity_manage_gyver_lamp_check_connection.isEnabled = true
                activity_manage_gyver_lamp_status_text.apply {
                    visibility = View.VISIBLE
                    text = getText(R.string.check_connection_status_failed)
                    setTextColor(resources.getColor(android.R.color.holo_red_dark))
                }
            }
            ManageGyverLampView.CheckingState.CHECK_SUCCESS -> {
                activity_manage_gyver_lamp_check_connection.isEnabled = true
                activity_manage_gyver_lamp_status_text.apply {
                    visibility = View.VISIBLE
                    text = getText(R.string.check_connection_status_success)
                    setTextColor(resources.getColor(android.R.color.holo_green_dark))
                }
            }
            ManageGyverLampView.CheckingState.INCORRECT_INPUT_DATA -> {
                activity_manage_gyver_lamp_check_connection.isEnabled = true
                activity_manage_gyver_lamp_status_text.apply {
                    visibility = View.VISIBLE
                    text = getText(R.string.check_connection_status_incorrect_data)
                    setTextColor(resources.getColor(android.R.color.holo_red_dark))
                }
            }
            ManageGyverLampView.CheckingState.SAVING -> {
                //now this las state, we don`t need enable save button after that
                activity_manage_gyver_lamp_status_text.visibility = View.GONE
                activity_manage_gyver_lamp_check_connection.isEnabled = false
                activity_manage_gyver_lamp_save.isEnabled = false
            }
        }!!
    }

    override fun showManageState(state: ManageGyverLampView.ManageState) {
        mManageState = state
        when (state) {
            ManageGyverLampView.ManageState.LOADING -> {
                loading_layout.visibility = View.VISIBLE
                activity_manage_gyver_lamp_layout.visibility = View.GONE
            }
            ManageGyverLampView.ManageState.ADD_NEW -> {
                loading_layout.visibility = View.GONE
                activity_manage_gyver_lamp_layout.visibility = View.VISIBLE
                activity_manage_gyver_lamp_save.visibility = View.VISIBLE
            }
            ManageGyverLampView.ManageState.EDIT -> {
                loading_layout.visibility = View.GONE
                activity_manage_gyver_lamp_layout.visibility = View.VISIBLE
                activity_manage_gyver_lamp_save.visibility = View.GONE
            }
            ManageGyverLampView.ManageState.SAVING_NEW,
            ManageGyverLampView.ManageState.SAVING_EXIST -> {
                // no actions
            }
        }!!
        setMenuItemVisibility(state)
    }

    override fun showGyverLampEntity(gyverLampEntity: GyverLampEntity) {
        activity_manage_gyver_lamp_name.editText!!.setText(gyverLampEntity.name)
        activity_manage_gyver_lamp_ip.editText!!.setText(gyverLampEntity.host)
        activity_manage_gyver_lamp_port.editText!!.setText(gyverLampEntity.port.toString())
    }

    private class ChangeTextWatcher(private val callback: () -> Unit) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            callback()
        }
    }
}
