package ru.vladislavsumin.myhomeiot.ui.main

import android.content.Context
import android.content.Intent
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
import ru.vladislavsumin.myhomeiot.ui.lamp.manage.AddGyverLampActivity
import ru.vladislavsumin.myhomeiot.ui.lamp.control.GyverLampControlActivity
import ru.vladislavsumin.myhomeiot.R
import ru.vladislavsumin.myhomeiot.app.AppConfig
import ru.vladislavsumin.myhomeiot.database.entity.GyverLampEntity
import ru.vladislavsumin.myhomeiot.ui.core.ToolbarActivity

class MainActivity : ToolbarActivity(), MainActivityView {
    companion object {
        private const val LAYOUT = R.layout.activity_main

        fun getLaunchIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    @InjectPresenter
    lateinit var mPresenter: MainActivityPresenter

    lateinit var mAdapter: Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LAYOUT)
    }

    override fun setupUi() {
        super.setupUi()
        val app_version = getString(R.string.app_version).format(AppConfig.VERSION_NAME)
        activity_main_app_version_name.text = app_version
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

    override fun setupUx() {
        super.setupUx()
        activity_main_fab.setOnClickListener {
            startActivity(AddGyverLampActivity.getLaunchIntent(this))
        }
    }

    override fun setGyverLamsList(list: List<GyverLampEntity>) {
        mAdapter.mLamps = list
    }

    class Adapter : RecyclerView.Adapter<GyverLampViewHolder>() {
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

    class GyverLampViewHolder private constructor(view: View) :
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
