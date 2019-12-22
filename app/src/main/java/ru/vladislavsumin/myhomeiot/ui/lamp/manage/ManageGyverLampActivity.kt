package ru.vladislavsumin.myhomeiot.ui.lamp.manage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import kotlinx.android.synthetic.main.activity_manage_gyver_lamp.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.vladislavsumin.myhomeiot.R
import ru.vladislavsumin.myhomeiot.ui.core.ToolbarActivity

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
            getLaunchIntent(
                this
            ).getLongExtra(
                GYVER_LAMP_ID,
                0
            )
        )
    }

    @InjectPresenter
    lateinit var mPresenter: ManageGyverLampPresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LAYOUT)

        activity_add_gyver_lamp_check_connection.setOnClickListener { onClickCheckConnection() }
        activity_add_gyver_lamp_save.setOnClickListener { onClickSave() }
        activity_add_gyver_lamp_ip.editText!!.addTextChangedListener(
            ChangeTextWatcher(
                mPresenter::onTextChanged
            )
        )
        activity_add_gyver_lamp_port.editText!!.addTextChangedListener(
            ChangeTextWatcher(
                mPresenter::onTextChanged
            )
        )
    }

    private fun onClickSave() {

        val name = activity_add_gyver_lamp_name.editText!!.text.toString()
        val host = activity_add_gyver_lamp_ip.editText!!.text.toString()
        val port = activity_add_gyver_lamp_port.editText!!.text.toString().toInt()

        mPresenter.onClickSave(name, host, port)
    }

    private fun onClickCheckConnection() {
        val host = activity_add_gyver_lamp_ip.editText!!.text.toString()
        val port = activity_add_gyver_lamp_port.editText!!.text.toString().toInt()

        mPresenter.onClickCheckConnection(host, port)
    }

    @Suppress("DEPRECATION")
    override fun showCheckingState(state: ManageGyverLampView.CheckingState) {
        //TODO добавить цветовою индикацию с цветами темы!
        when (state) {
            ManageGyverLampView.CheckingState.NOT_CHECKED -> {
                activity_add_gyver_lamp_check_connection.isEnabled = true
                activity_add_gyver_lamp_status_text.visibility = View.GONE
            }
            ManageGyverLampView.CheckingState.CHECKING -> {
                activity_add_gyver_lamp_check_connection.isEnabled = false
                activity_add_gyver_lamp_status_text.apply {
                    visibility = View.VISIBLE
                    text = getText(R.string.check_connection_status_checking)
                    setTextColor(resources.getColor(android.R.color.black))
                }
            }
            ManageGyverLampView.CheckingState.CHECK_FAILED -> {
                activity_add_gyver_lamp_check_connection.isEnabled = true
                activity_add_gyver_lamp_status_text.apply {
                    visibility = View.VISIBLE
                    text = getText(R.string.check_connection_status_failed)
                    setTextColor(resources.getColor(android.R.color.holo_red_dark))
                }
            }
            ManageGyverLampView.CheckingState.CHECK_SUCCESS -> {
                activity_add_gyver_lamp_check_connection.isEnabled = true
                activity_add_gyver_lamp_status_text.apply {
                    visibility = View.VISIBLE
                    text = getText(R.string.check_connection_status_success)
                    setTextColor(resources.getColor(android.R.color.holo_green_dark))
                }
            }
            ManageGyverLampView.CheckingState.INCORRECT_INPUT_DATA -> {
                activity_add_gyver_lamp_check_connection.isEnabled = true
                activity_add_gyver_lamp_status_text.apply {
                    visibility = View.VISIBLE
                    text = getText(R.string.check_connection_status_incorrect_data)
                    setTextColor(resources.getColor(android.R.color.holo_red_dark))
                }
            }
            ManageGyverLampView.CheckingState.SAVING -> {
                //now this las state, we don`t need enable save button after that
                activity_add_gyver_lamp_status_text.visibility = View.GONE
                activity_add_gyver_lamp_check_connection.isEnabled = false
                activity_add_gyver_lamp_save.isEnabled = false
            }
        }!!
    }

    override fun showManageState(state: ManageGyverLampView.ManageState) {
        TODO("not implemented")
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
