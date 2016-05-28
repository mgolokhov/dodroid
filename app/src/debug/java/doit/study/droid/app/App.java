package doit.study.droid.app;

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
    }
}
