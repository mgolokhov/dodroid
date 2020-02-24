package doit.study.droid.utils.timber

import android.util.Log
import com.crashlytics.android.Crashlytics
import timber.log.Timber

class CrashlyticsTree : Timber.Tree() {

    override fun isLoggable(priority: Int): Boolean {
        return !(priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO)
        // warn, err, wtf
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (isLoggable(priority)) {

            Crashlytics.log(priority, tag, message)

            if (t != null) {
                Crashlytics.logException(t)
            }
        }
    }
}
