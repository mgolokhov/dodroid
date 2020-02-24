package doit.study.droid.data.local.preferences

import android.app.Application
import androidx.preference.PreferenceManager
import doit.study.droid.R
import javax.inject.Inject

class Sound @Inject constructor(private val appContext: Application) {
    private val sp = PreferenceManager.getDefaultSharedPreferences(appContext)

    fun isEnabled(): Boolean {
        return sp.getBoolean(
                appContext.resources.getString(R.string.pref_sound),
                false)
    }
}
