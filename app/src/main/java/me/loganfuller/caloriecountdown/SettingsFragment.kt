package me.loganfuller.caloriecountdown

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        val pref = findPreference(getString(R.string.key_setup_completed))
        pref.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
                    sharedPrefs.edit().clear().apply()
                    val i = Intent(activity?.baseContext, InitActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(i)
                    true
                }
    }
}