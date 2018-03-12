package doit.study.droid.app;

import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import doit.study.droid.BuildConfig;
import doit.study.droid.R;
import doit.study.droid.di.AppComponent;
import doit.study.droid.di.AppModule;
import doit.study.droid.di.DaggerAppComponent;
import io.fabric.sdk.android.Fabric;


public abstract class BaseApp extends MultiDexApplication {
    private Tracker mTracker;

    private static AppComponent sAppComponent;

    public static AppComponent getAppComponent(){
        return sAppComponent;
    }

    @Override
    public void onCreate() {
        sAppComponent  = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        super.onCreate();
        CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build(), new Crashlytics());
    }

    // Get the tracker associated with this app
    public void startTracking() {

        // Initialize an Analytics tracker using a Google Analytics property ID.

        // Does the Tracker already exist?
        // If not, create it
        if (mTracker == null) {
            GoogleAnalytics ga = GoogleAnalytics.getInstance(this);

            // Get the config data for the tracker
            mTracker = ga.newTracker(R.xml.track_app);

            // Enable tracking of activities
            ga.enableAutoActivityReports(this);
        }
    }

    public Tracker getTracker() {
        // Make sure the tracker exists
        startTracking();

        // Then return the tracker
        return mTracker;
    }

}
