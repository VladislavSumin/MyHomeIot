package ru.vladislavsumin.myhomeiot.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.vladislavsumin.myhomeiot.R

class SettingsFragment : Fragment() {
    companion object {
        private const val LAYOUT = R.layout.fragment_settings

        fun getInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }
}