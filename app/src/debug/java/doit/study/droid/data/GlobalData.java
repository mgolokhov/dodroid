package doit.study.droid.data;

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
    }
}
