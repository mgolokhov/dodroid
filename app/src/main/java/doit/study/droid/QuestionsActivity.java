package doit.study.droid;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import doit.study.droid.data.Question;
import doit.study.droid.data.QuizProvider;


public class QuestionsActivity extends AppCompatActivity implements QuestionFragment.OnFragmentActivityChatter, LoaderManager.LoaderCallbacks<Cursor> {
    private static final boolean DEBUG = true;
    private final String TAG = "NSA " + getClass().getName();
    private ViewPager mPager;
    private final int QUIZ_SIZE = 10;
    private int mRightAnswered = 0;
    private static final int QUESTION_LOADER = 0;
    private QuestionsPagerAdapter mPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_layout);
        getSupportLoaderManager().initLoader(QUESTION_LOADER, null, QuestionsActivity.this);
        mPager = (ViewPager)findViewById(R.id.view_pager);
        configPagerTabStrip();
        mPagerAdapter = new QuestionsPagerAdapter(getSupportFragmentManager());
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
    public void saveStat(Question question) {
        //mQuizData.setQuestion(question);
    }

    @Override
    public void updateProgress(){
        setTitle(String.format("Progress: %d%%", (++mRightAnswered)*100/QUIZ_SIZE));
    }

    @Override
    public void swipeToNext(int delay) {
        final int posInFocus = mPager.getCurrentItem();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (posInFocus == mPager.getCurrentItem()) {
                    if (DEBUG) Log.d(TAG, "swipe to the next page");
                    mPager.setCurrentItem(posInFocus + 1);
                }
            }
        }, delay);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri randQuestionUri = QuizProvider.QUESTION_URI
                .buildUpon()
                .appendPath("rand").appendPath(Integer.toString(QUIZ_SIZE))
                .build();
        return new CursorLoader(this, randQuestionUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mPagerAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPagerAdapter.swapCursor(null);
    }
}

