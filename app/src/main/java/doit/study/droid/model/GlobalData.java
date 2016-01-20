package doit.study.droid.model;

import android.app.Application;

import doit.study.droid.model.QuizData;
import doit.study.droid.sqlite.helper.DatabaseHelper;

/**
 * Use application singleton
*/
public class GlobalData extends Application {
    @SuppressWarnings("unused")
    private final String TAG = "NSA " + getClass().getName();
    private QuizData mQuizData;

    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseHelper helper = new DatabaseHelper(this);
        mQuizData = new QuizData(helper);
    }

    public QuizData getQuizData() {
        return mQuizData;
    }
}
