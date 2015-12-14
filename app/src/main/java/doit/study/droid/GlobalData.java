package doit.study.droid;

import android.app.Application;

import doit.study.droid.sqlite.helper.DatabaseHelper;

/**
 * Use application singleton
*/
public class GlobalData extends Application {
    private final String TAG = "NSA " + getClass().getName();
    // Link to the resource file, in our case it's a json file
    // I think we can say it some kind of descriptor, so it's an integer
    private final Integer mTestFile = R.raw.quiz;
    private QuizData mQuizData;

    @Override
    public void onCreate() {
        super.onCreate();
        //parseTests(readFile());
        DatabaseHelper helper = new DatabaseHelper(this);
        helper.importFromFile();
        mQuizData = new QuizData(helper);
    }

    public QuizData getQuizData(){
        return mQuizData;
    }

}
