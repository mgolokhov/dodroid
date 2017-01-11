package doit.study.droid.app;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class App extends BaseApp {

    @Override
    public void onCreate() {
        super.onCreate();
        startTracking();
        Timber.plant(new CrashReportingTree());
        Fabric.with(this, new Crashlytics());
    }

    private static class CrashReportingTree extends Timber.Tree {

        @Override
        protected boolean isLoggable(int priority) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
                return false;
            }
            // warn, err, wtf
            return true;
        }


        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (isLoggable(priority)) {

                Crashlytics.log(priority, tag, message);

                if (t != null) {
                    Crashlytics.logException(t);
                }
            }
        }
    }
}
