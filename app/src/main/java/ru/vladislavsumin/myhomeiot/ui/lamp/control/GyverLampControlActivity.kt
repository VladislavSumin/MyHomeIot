package ru.vladislavsumin.myhomeiot.ui.lamp.control

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_gyver_lamp_control.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.vladislavsumin.myhomeiot.R
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampMode
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.GyverLampState
import ru.vladislavsumin.myhomeiot.domain.gyver.lamp.connection.GyverLampConnectionState
import ru.vladislavsumin.myhomeiot.ui.core.ToolbarActivity
import ru.vladislavsumin.myhomeiot.ui.lamp.manage.ManageGyverLampActivity


class GyverLampControlActivity : ToolbarActivity(), GyverLampControlView {
    companion object {
        private const val LAYOUT = R.layout.activity_gyver_lamp_control
        private const val LAYOUT_LIST_ELEMENT = R.layout.list_mode_element

        private const val GYVER_LAMP_ID = "gyver_lamp_id"

        fun getLaunchIntent(context: Context, id: Long): Intent {
            return Intent(context, GyverLampControlActivity::class.java).apply {
                putExtra(GYVER_LAMP_ID, id)
            }
        }
    }

    @InjectPresenter
    lateinit var mPresenter: GyverLampControlPresenter

    private lateinit var mAdapter: Adapter
    private lateinit var mModeNames: Array<String>

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
        mModeNames = resources.getStringArray(R.array.gyver_lamp_modes)

        activity_gyver_lamp_control_scale.max = 100
        activity_gyver_lamp_control_speed.max = 100
        activity_gyver_lamp_control_brightness.max = 255

        mAdapter = Adapter()
        activity_gyver_lamp_control_modes.adapter = mAdapter
        val layoutManager = GridLayoutManager(this, 2)
        activity_gyver_lamp_control_modes.layoutManager = layoutManager
        activity_gyver_lamp_control_modes.addItemDecoration(
            DividerItemDecoration(this, RecyclerView.HORIZONTAL)
        )
        activity_gyver_lamp_control_modes.addItemDecoration(
            DividerItemDecoration(this, RecyclerView.VERTICAL)
        )
    }

    private fun setupUx() {
        activity_gyver_lamp_control_on_off.setOnClickListener { mPresenter.onClickOnOffButton() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.gyver_lamp_control, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.gyver_lamp_control_settings -> {
                mPresenter.onClickSettingsButton()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun showSettingsScreen(id: Long) {
        startActivity(ManageGyverLampActivity.getLaunchIntent(this, id))
    }

    override fun showGyverLampConnectionState(connectionState: GyverLampConnectionState) {
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
        if (state != null) {
            activity_gyver_lamp_control_on_off.isEnabled = true
            activity_gyver_lamp_control_scale.isEnabled = true
            activity_gyver_lamp_control_speed.isEnabled = true
            activity_gyver_lamp_control_brightness.isEnabled = true
            activity_gyver_lamp_control_modes.isEnabled = true

            activity_gyver_lamp_control_scale.progress = state.scale
            activity_gyver_lamp_control_speed.progress = state.speed
            activity_gyver_lamp_control_brightness.progress = state.brightness

            activity_gyver_lamp_control_on_off.isOn = state.isOn
            activity_gyver_lamp_control_mode.text = getString(R.string.current_mode)
                .format(getModeString(state.mode))
        } else {
            activity_gyver_lamp_control_on_off.isEnabled = false
            activity_gyver_lamp_control_scale.isEnabled = false
            activity_gyver_lamp_control_speed.isEnabled = false
            activity_gyver_lamp_control_brightness.isEnabled = false
            activity_gyver_lamp_control_modes.isEnabled = false
        }
    }

    private fun getModeString(gyverLampMode: GyverLampMode): String {
        return mModeNames[gyverLampMode.id]
    }

    private fun getViewHolderInstance(parent: ViewGroup): ModsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(LAYOUT_LIST_ELEMENT, parent, false)
        return ModsViewHolder(view)
    }


    private inner class Adapter : RecyclerView.Adapter<ModsViewHolder>() {
        var mMods: List<GyverLampMode> = GyverLampMode.values().toList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModsViewHolder {
            return getViewHolderInstance(parent)
        }

        override fun getItemCount(): Int = mMods.size

        override fun onBindViewHolder(holder: ModsViewHolder, position: Int) {
            holder.bind(mMods[position])
        }
    }

    private inner class ModsViewHolder constructor(view: View) :
        RecyclerView.ViewHolder(view) {

        private val name: TextView = view.findViewById(R.id.list_mode_element_name)
        private lateinit var mItem: GyverLampMode

        init {
            view.setOnClickListener {
                mPresenter.onChangeMode(mItem)
            }
        }

        fun bind(item: GyverLampMode) {
            mItem = item
            name.text = mModeNames[item.id]
        }
    }
}