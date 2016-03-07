package doit.study.droid;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

import doit.study.droid.model.GlobalData;
import doit.study.droid.model.Question;
import doit.study.droid.model.QuizData;

public class QuestionsActivity extends AppCompatActivity implements QuestionFragment.OnFragmentActivityChatter {
    private final String TAG = "NSA " + getClass().getName();
    private ViewPager mPager;
    private QuizData mQuizData;
    private int QUIZ_SIZE = 10;
    private int right_answered = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_layout);
        mPager = (ViewPager)findViewById(R.id.view_pager);
        configPagerTabStrip();

        GlobalData gd = (GlobalData) getApplication();
        mQuizData = gd.getQuizData();
        List<Integer> questionIds = mQuizData.getRandSelectedQuestionIds(QUIZ_SIZE);

        PagerAdapter pagerAdapter = new QuestionsPagerAdapter(
                getSupportFragmentManager(),
                mQuizData,
                questionIds);
        mPager.setAdapter(pagerAdapter);
       }


    private void configPagerTabStrip(){
        PagerTabStrip pagerTabStrip = (PagerTabStrip) mPager.findViewById(R.id.pager_title_strip);
        // show one title
        pagerTabStrip.setNonPrimaryAlpha(0);
        // set the black underlining
        pagerTabStrip.setTabIndicatorColor(0x000000);
    }


    @Override
    public void saveStat(Question question) {
        mQuizData.setQuestion(question);
    }

    @Override
    public void updateProgress(){
        setTitle(String.format("Progress: %d%%", (++right_answered)*100/QUIZ_SIZE));
    }

    @Override
    public void swipeToNext(int delay) {
        final int posInFocus = mPager.getCurrentItem();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (posInFocus == mPager.getCurrentItem()) {
                    Log.d(TAG, "swipe to the next page");
                    mPager.setCurrentItem(posInFocus + 1);
                }
            }
        }, delay);
    }

    @Override
    public Question getQuestion(int id) {
        return mQuizData.getQuestionById(id);
    }
}

