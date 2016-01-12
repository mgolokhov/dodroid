package doit.study.droid;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Collections;
import java.util.List;

import doit.study.droid.sqlite.helper.DatabaseHelper;

public class QuestionsActivity extends AppCompatActivity implements QuestionFragment.OnFragmentChangeListener{
    private final String TAG = "NSA " + getClass().getName();
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_layout);
        mPager = (ViewPager)findViewById(R.id.view_pager);
        configPagerTabStrip();

        SharedPreferences sp = getSharedPreferences("wrong_right_counters", Context.MODE_PRIVATE);
        SharedPreferences.Editor m = sp.edit();
        m.putInt("wrong", 0);
        m.putInt("right", 0);
        m.commit();

        GlobalData gd = (GlobalData) getApplication();
        QuizData quizData = gd.getQuizData();
        List<Integer> questionIds = quizData.getRandSelectedQuestionIds(10);
        mPagerAdapter = new QuestionsPagerAdapter(
                getSupportFragmentManager(),
                quizData,
                questionIds);
        mPager.setAdapter(mPagerAdapter);
       }


    private void configPagerTabStrip(){
        PagerTabStrip pagerTabStrip = (PagerTabStrip) mPager.findViewById(R.id.pager_title_strip);
        // show one title
        pagerTabStrip.setNonPrimaryAlpha(0);
        // set the black underlining
        pagerTabStrip.setTabIndicatorColor(0x000000);
    }


    @Override
    public void updateFragments() {
        Log.i(TAG, "update");
        int posInFocus = mPager.getCurrentItem();
        ((QuestionsPagerAdapter)mPagerAdapter).updateFragments(mPager, posInFocus);
    }

    @Override
    public void swipeToNext(int delay) {
        final int posInFocus = mPager.getCurrentItem();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (posInFocus == mPager.getCurrentItem()) {
                    Log.i(TAG, "swipe to the next page");
                    mPager.setCurrentItem(posInFocus + 1);
                }
            }
        }, delay);
    }
}

