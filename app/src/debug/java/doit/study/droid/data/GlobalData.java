package doit.study.droid.data;

import android.os.SystemClock;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class GlobalData extends MyBaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree(){
            @Override
            protected String createStackElementTag(StackTraceElement element) {
                return "NSA:" + super.createStackElementTag(element) + ":" + element.getLineNumber();
            }
        });
        Timber.d("Debug mode with logging");
//        SystemClock.sleep(TimeUnit.SECONDS.toMillis(1));
    }
}
