package doit.study.droid.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import doit.study.droid.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_general)
    }

}