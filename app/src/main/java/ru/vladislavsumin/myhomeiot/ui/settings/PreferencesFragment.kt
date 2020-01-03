package ru.vladislavsumin.myhomeiot.ui.settings

import android.os.Bundle
import android.widget.Toast
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import ru.vladislavsumin.myhomeiot.R
import ru.vladislavsumin.myhomeiot.app.AppConfig

class PreferencesFragment : PreferenceFragmentCompat() {

    private val listener = Preference.OnPreferenceChangeListener { preference, newValue ->
        showToast("AAAAAAAA", Toast.LENGTH_SHORT)
        if (preference is ListPreference) {
            showToast(newValue.toString(), Toast.LENGTH_SHORT)
            preferenceManager
                .findPreference<ListPreference>("pref_key_dark_mode")!!
                .summary = newValue.toString()
        }
        true
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)

        preferenceManager
            .findPreference<Preference>("pref_key_build_number")!!
            .summary = AppConfig.VERSION_NAME

        val selection = preferenceManager.sharedPreferences.getString("pref_key_dark_mode", "3")
        preferenceManager
            .findPreference<ListPreference>("pref_key_dark_mode")!!
            .summary = selection

        preferenceScreen.onPreferenceChangeListener = listener
        // TODO это не работает, я хз, че не так
    }

    fun showToast(text: String, duration: Int) {
        Toast.makeText(requireContext(), text, duration).show()
    }
}