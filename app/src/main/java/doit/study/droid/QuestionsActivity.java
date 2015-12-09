package doit.study.droid;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class QuestionsActivity extends AppCompatActivity implements QuestionFragment.OnFragmentChangeListener {
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
        mPagerAdapter = new QuestionsPagerAdapter(getSupportFragmentManager(), gd.getQuizData());
        mPager.setAdapter(mPagerAdapter);
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

