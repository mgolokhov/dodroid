package doit.study.droid.app

import android.util.Log

import com.crashlytics.android.Crashlytics

import io.fabric.sdk.android.Fabric
import timber.log.Timber

class App : BaseApp() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(CrashReportingTree())
        Fabric.with(this, Crashlytics())
    }

    private class CrashReportingTree : Timber.Tree() {

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
}
