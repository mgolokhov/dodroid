package doit.study.droid.data.local.preferences

import android.app.Application
import androidx.preference.PreferenceManager
import doit.study.droid.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SslPinning @Inject constructor(val appContext: Application) {
    private val sp = PreferenceManager.getDefaultSharedPreferences(appContext)

    fun isEnabled(): Boolean {
        return sp.getBoolean(
                appContext.resources.getString(R.string.debug_pref_ssl_pinning),
                false)
    }
}
