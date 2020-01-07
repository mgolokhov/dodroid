package doit.study.droid.app

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.facebook.stetho.Stetho
import doit.study.droid.BuildConfig
import doit.study.droid.di.AppComponent
import doit.study.droid.di.DaggerAppComponent
import doit.study.droid.utils.timber.CrashlyticsTree
import doit.study.droid.utils.timber.LogcatTree
import io.fabric.sdk.android.Fabric
import timber.log.Timber


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        dagger = DaggerAppComponent
                .factory()
                .create(this)
        setupCrashlytics()
        setupTimber()
        setupStetho()
    }

    private fun setupStetho() {
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(LogcatTree())
            Timber.d("Debug mode with logging on")
        } else {
            Timber.plant(CrashlyticsTree())
        }
    }

    private fun setupCrashlytics() {
        val core = CrashlyticsCore.Builder()
                .disabled(BuildConfig.DEBUG)
                .build()
        Fabric.with(this, Crashlytics.Builder().core(core).build(), Crashlytics())
    }

    companion object {
        lateinit var dagger: AppComponent
    }

}
