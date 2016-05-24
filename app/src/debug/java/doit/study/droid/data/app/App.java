package doit.study.droid.data.app;

import doit.study.droid.app.BaseApp;
import timber.log.Timber;

public class App extends BaseApp {

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
