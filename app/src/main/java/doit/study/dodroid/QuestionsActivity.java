package doit.study.dodroid;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import java.util.ArrayList;

public class QuestionsActivity extends FragmentActivity implements QuestionFragment.OnStatisticChangeListener{
    private final String LOG_TAG = "NSA " + getClass().getName();
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private ArrayList<Question> mQuestions;
    private UserStatistic mStatistic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_layout);

        mPager = (ViewPager)findViewById(R.id.view_pager);
        GlobalData gd = (GlobalData) getApplication();
        mQuestions = gd.getQuestions();
        mStatistic = new UserStatistic();
        mPagerAdapter = new QuestionsPagerAdapter(getSupportFragmentManager(), mQuestions, mStatistic);
        mPager.setAdapter(mPagerAdapter);
        }

    @Override
    public void onStatisticChanged(){
        Log.i(LOG_TAG, "onStatisticChanged");
        mPagerAdapter.notifyDataSetChanged();
    }
}
