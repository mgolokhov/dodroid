package doit.study.droid.debug

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.jakewharton.processphoenix.ProcessPhoenix
import doit.study.droid.R

class DebugFragment() : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.debug_fragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupMisc()
        setupNetwork()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.title = getString(R.string.debug_title_debug_menu)
    }

    private fun setupNetwork() {
        findPreference<Preference>(getString(R.string.debug_pref_ssl_pinning))?.let {
            it.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                ProcessPhoenix.triggerRebirth(context)
                true
            }
        }
    }

    private fun setupMisc() {
        findPreference<Preference>(getString(R.string.debug_force_crash))?.let {
            it.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                throw RuntimeException("forced crash")
            }
        }
    }
}
