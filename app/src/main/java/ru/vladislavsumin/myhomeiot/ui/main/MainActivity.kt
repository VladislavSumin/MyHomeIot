package ru.vladislavsumin.myhomeiot.ui.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import moxy.presenter.InjectPresenter
import ru.vladislavsumin.myhomeiot.ui.lamp.manage.ManageGyverLampActivity
import ru.vladislavsumin.myhomeiot.ui.lamp.control.GyverLampControlActivity
import ru.vladislavsumin.myhomeiot.R
import ru.vladislavsumin.myhomeiot.app.AppConfig
import ru.vladislavsumin.myhomeiot.database.entity.GyverLampEntity
import ru.vladislavsumin.myhomeiot.ui.core.ToolbarActivity
import ru.vladislavsumin.myhomeiot.ui.utils.setClickableLinkListener

class MainActivity : ToolbarActivity(), MainActivityView {
    companion object {
        private const val LAYOUT = R.layout.activity_main

        fun getLaunchIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    @InjectPresenter
    lateinit var mPresenter: MainActivityPresenter

    private lateinit var mAdapter: Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LAYOUT)

        setupUi()
        setupUx()
    }

    private fun setupUi() {
        val appVersion = getString(R.string.app_version).format(AppConfig.VERSION_NAME)
        activity_main_app_version_name.text = appVersion
        mAdapter = Adapter()
        activity_main_lamps_list.adapter = mAdapter
        val layoutManager = LinearLayoutManager(this)
        activity_main_lamps_list.layoutManager = layoutManager
        activity_main_lamps_list.addItemDecoration(
            DividerItemDecoration(
                this,
                layoutManager.orientation
            )
        )
    }

    private fun setupUx() {
        activity_main_privacy_policy.setClickableLinkListener {
            mPresenter.onClickReadPrivacyPolicy()
        }
        activity_main_fab.setOnClickListener {
            startActivity(ManageGyverLampActivity.getLaunchIntent(this))
        }
    }

    override fun setGyverLamsList(list: List<GyverLampEntity>) {
        mAdapter.mLamps = list
    }

    override fun showPrivacyPolicyScreen(privacyPolicyUri: Uri) {
        val browserIntent = Intent(Intent.ACTION_VIEW, privacyPolicyUri)
        startActivity(browserIntent)
    }

    private class Adapter : RecyclerView.Adapter<GyverLampViewHolder>() {
        var mLamps: List<GyverLampEntity> = emptyList()
            @UiThread
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GyverLampViewHolder {
            return GyverLampViewHolder.getInstance(parent)
        }

        override fun getItemCount(): Int = mLamps.size

        override fun onBindViewHolder(holder: GyverLampViewHolder, position: Int) {
            holder.bind(mLamps[position])
        }
    }

    private class GyverLampViewHolder private constructor(view: View) :
        RecyclerView.ViewHolder(view) {
        companion object {

            private const val LAYOUT = R.layout.list_gyver_lamp_element

            fun getInstance(parent: ViewGroup): GyverLampViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(LAYOUT, parent, false)
                return GyverLampViewHolder(view)
            }
        }

        private val name: TextView = view.findViewById(R.id.list_gyver_lamp_element_name)
        private lateinit var mItem: GyverLampEntity

        init {
            view.setOnClickListener {
                //TODO тут по хорошему нужно через презентер дергать а не на прямую, надо поправить
                it.context.startActivity(
                    GyverLampControlActivity.getLaunchIntent(
                        it.context,
                        mItem.id
                    )
                )
            }
        }

        fun bind(item: GyverLampEntity) {
            mItem = item
            name.text = item.name
        }
    }
}
