package doit.study.droid.app

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import doit.study.droid.BuildConfig
import doit.study.droid.di.AppComponent
import doit.study.droid.di.AppModule
import doit.study.droid.di.DaggerAppComponent
import doit.study.droid.di.NetworkModule
import io.fabric.sdk.android.Fabric


abstract class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()
        dagger = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
        setupCrashlytics()
    }

    private fun setupCrashlytics() {
        val core = CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()
        Fabric.with(this, Crashlytics.Builder().core(core).build(), Crashlytics())
    }

    companion object {
        lateinit var dagger: AppComponent
    }

}
