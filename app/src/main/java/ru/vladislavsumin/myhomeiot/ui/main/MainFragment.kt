package ru.vladislavsumin.myhomeiot.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_main.*
import moxy.presenter.InjectPresenter
import ru.vladislavsumin.myhomeiot.R
import ru.vladislavsumin.myhomeiot.database.entity.GyverLampEntity
import ru.vladislavsumin.myhomeiot.ui.core.BaseFragment
import ru.vladislavsumin.myhomeiot.ui.lamp.control.GyverLampControlActivity
import ru.vladislavsumin.myhomeiot.ui.lamp.manage.ManageGyverLampActivity

class MainFragment : BaseFragment(), MainFragmentView {
    companion object {
        private const val LAYOUT = R.layout.fragment_main
    }

    @InjectPresenter
    lateinit var mPresenter: MainFragmentPresenter

    private lateinit var mAdapter: Adapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(LAYOUT, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        setupUx()
    }

    private fun setupUi() {
        mAdapter = Adapter()
        fragment_main_lamps_list.adapter = mAdapter
        val layoutManager = LinearLayoutManager(requireContext())
        fragment_main_lamps_list.layoutManager = layoutManager
        fragment_main_lamps_list.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                layoutManager.orientation
            )
        )
    }

    private fun setupUx() {
        fragment_main_fab.setOnClickListener {
            startActivity(ManageGyverLampActivity.getLaunchIntent(requireContext()))
        }
    }

    override fun setGyverLampList(list: List<GyverLampEntity>) {
        mAdapter.mLamps = list
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
