package ru.vladislavsumin.myhomeiot.ui.lamp.control

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_gyver_lamp_control.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.vladislavsumin.myhomeiot.R
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnectionState
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampState
import ru.vladislavsumin.myhomeiot.ui.core.ToolbarActivity

class GyverLampControlActivity : ToolbarActivity(), GyverLampControlView {
    companion object {
        private const val LAYOUT = R.layout.activity_gyver_lamp_control
        private const val GYVER_LAMP_ID = "gyver_lamp_id"

        fun getLaunchIntent(context: Context, id: Long): Intent {
            return Intent(context, GyverLampControlActivity::class.java).apply {
                putExtra(GYVER_LAMP_ID, id)
            }
        }
    }

    @InjectPresenter
    lateinit var mPresenter: GyverLampControlPresenter

    @ProvidePresenter
    fun providePresenter(): GyverLampControlPresenter {
        return GyverLampControlPresenter(
            this.intent.getLongExtra(
                GYVER_LAMP_ID,
                0
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LAYOUT)
        setupUi()
        setupUx()
    }

    private fun setupUi() {
        activity_gyver_lamp_control_scale.max = 100
        activity_gyver_lamp_control_speed.max = 100
        activity_gyver_lamp_control_brightness.max = 255
    }

    private fun setupUx() {
        activity_gyver_lamp_control_on_off.setOnClickListener { mPresenter.onClickOnOffButton() }
    }

    override fun showGyverLampConnectionState(connectionState: GyverLampConnectionState) {
        activity_gyver_lamp_control_status.text = connectionState.toString()

        //TODO rewrite
        activity_gyver_lamp_control_brightness.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mPresenter.onChangeBrightness(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        activity_gyver_lamp_control_scale.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mPresenter.onChangeScale(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        activity_gyver_lamp_control_speed.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mPresenter.onChangeSpeed(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    override fun showGyverLampState(state: GyverLampState?) {
        activity_gyver_lamp_control_state.text = state?.toString()

        if (state != null) {
            activity_gyver_lamp_control_on_off.isEnabled = true
            activity_gyver_lamp_control_scale.isEnabled = true
            activity_gyver_lamp_control_speed.isEnabled = true
            activity_gyver_lamp_control_brightness.isEnabled = true

            activity_gyver_lamp_control_scale.progress = state.scale
            activity_gyver_lamp_control_speed.progress = state.speed
            activity_gyver_lamp_control_brightness.progress = state.brightness

        } else {
            activity_gyver_lamp_control_on_off.isEnabled = false
            activity_gyver_lamp_control_scale.isEnabled = false
            activity_gyver_lamp_control_speed.isEnabled = false
            activity_gyver_lamp_control_brightness.isEnabled = false
        }
    }
}