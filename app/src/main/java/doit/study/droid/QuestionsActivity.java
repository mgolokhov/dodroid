package doit.study.droid;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import doit.study.droid.sqlite.helper.DatabaseHelper;

public class QuestionsActivity extends AppCompatActivity implements QuestionFragment.OnFragmentChangeListener,
        QuestionFragment.OnAnswerCheckListener {
    private final String TAG = "NSA " + getClass().getName();
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_layout);
        mPager = (ViewPager)findViewById(R.id.view_pager);
        PagerTabStrip pagerTabStrip = (PagerTabStrip) mPager.findViewById(R.id.pager_title_strip);
        pagerTabStrip.setNonPrimaryAlpha(0);
        pagerTabStrip.setTabIndicatorColor(0x000000);
        GlobalData gd = (GlobalData) getApplication();
        List<Integer> questionIds = (List<Integer>) gd.retrieve("questionIds");
        Collections.shuffle(questionIds);
        if (questionIds.size() > 10) {
            questionIds = questionIds.subList(0, 10);
        }
        mPagerAdapter = new QuestionsPagerAdapter(
                getSupportFragmentManager(),
                gd.getQuizData(),
                questionIds);
        mPager.setAdapter(mPagerAdapter);
        }

    @Override
    public void onAnswer(int questionId, boolean isRight) {
        DatabaseHelper mDBHelper = new DatabaseHelper(this);
        mDBHelper.addStats(questionId, isRight);
    }

    @Override
    public void updateFragments() {
        Log.i(TAG, "update");
        int posInFocus = mPager.getCurrentItem();
        ((QuestionsPagerAdapter)mPagerAdapter).updateFragments(mPager, posInFocus);
    }

    @Override
    public void swipeNext() {
        final int posInFocus = mPager.getCurrentItem();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (posInFocus == mPager.getCurrentItem()) {
                    Log.i(TAG, "swipe to the next page");
                    mPager.setCurrentItem(posInFocus + 1);
                }
            }
        }, 2000);
    }
}

