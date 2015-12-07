package doit.study.droid;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class QuestionsActivity extends AppCompatActivity implements QuestionFragment.OnStatisticChangeListener{
    private final String LOG_TAG = "NSA " + getClass().getName();
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_layout);

        mPager = (ViewPager)findViewById(R.id.view_pager);
        GlobalData gd = (GlobalData) getApplication();
        mPagerAdapter = new QuestionsPagerAdapter(getSupportFragmentManager(), gd.getQuizData());
        mPager.setAdapter(mPagerAdapter);
        }

    @Override
    public void onStatisticChanged(){
        Log.i(LOG_TAG, "onStatisticChanged");
        mPagerAdapter.notifyDataSetChanged();
    }
}
