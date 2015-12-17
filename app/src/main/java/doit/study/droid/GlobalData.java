package doit.study.droid;

import android.app.Application;

import doit.study.droid.sqlite.helper.DatabaseHelper;

/**
 * Use application singleton
*/
public class GlobalData extends Application {
    @SuppressWarnings("unused")
    private final String TAG = "NSA " + getClass().getName();
    private QuizData mQuizData;
    private InterActivityDataHolder mDataHolder = new InterActivityDataHolder();

    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseHelper helper = new DatabaseHelper(this);
        mQuizData = new QuizData(helper);
    }

    public QuizData getQuizData(){
        return mQuizData;
    }

    public void save(String key, Object obj) {
        mDataHolder.save(key, obj);
    }

    public Object retrieve(String key) {
        return mDataHolder.retrieve(key);
    }
}
