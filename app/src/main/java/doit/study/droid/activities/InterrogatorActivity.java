package doit.study.droid.activities;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import doit.study.droid.R;
import doit.study.droid.adapters.InterrogatorPagerAdapter;
import doit.study.droid.data.Question;
import doit.study.droid.data.QuizProvider;
import doit.study.droid.data.RelationTables;
import doit.study.droid.fragments.InterrogatorFragment;
import timber.log.Timber;


public class InterrogatorActivity extends DrawerBaseActivity implements InterrogatorFragment.OnFragmentActivityChatter, LoaderManager.LoaderCallbacks<Cursor> {
    private static final boolean DEBUG = false;
    private ViewPager mPager;
    // counters for current test
    private static final String WRONG_CNT_KEY = "doit.study.dodroid.wrong_cnt_key";
    private static final String RIGHT_CNT_KEY = "doit.study.dodroid.right_cnt_key";
    private int mRightCnt;
    private int mWrongCnt;
    private final int QUIZ_SIZE = 10; // default quiz size
    private int mQuizSize; // actual size can be lesser
    private static final String PROGRESS_KEY = "doit.study.dodroid.progress_key";
    private int mProgress; // quantity of answered questions
    private static final int QUESTION_LOADER = 0;
    private InterrogatorPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG) Timber.d("onCreate");
        mSelectionId = R.id.nav_do_it;
        getLayoutInflater().inflate(R.layout.activity_interrogator, mContainerContent);
        getSupportLoaderManager().initLoader(QUESTION_LOADER, null, InterrogatorActivity.this);
        mPager = (ViewPager) findViewById(R.id.view_pager);
        configPagerTabStrip();
        mPagerAdapter = new InterrogatorPagerAdapter(getSupportFragmentManager(), this);
        mPager.setAdapter(mPagerAdapter);
        setTitle(getResources().getQuantityString(R.plurals.numberOfQuestionsInTest, mProgress, mProgress));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(WRONG_CNT_KEY, mWrongCnt);
        outState.putInt(RIGHT_CNT_KEY, mRightCnt);
        outState.putInt(PROGRESS_KEY, mProgress);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mRightCnt = savedInstanceState.getInt(RIGHT_CNT_KEY);
        mWrongCnt = savedInstanceState.getInt(WRONG_CNT_KEY);
        mProgress = savedInstanceState.getInt(PROGRESS_KEY);
        if (mProgress == 0) {
            showProgress();
        }
    }

    private void configPagerTabStrip() {
        PagerTabStrip pagerTabStrip = (PagerTabStrip) mPager.findViewById(R.id.pager_title_strip);
        // show one title
        pagerTabStrip.setNonPrimaryAlpha(0);
        // set the black underlining
        pagerTabStrip.setTabIndicatorColor(0x000000);
    }


    @Override
    public void saveStat(Question question) {
        if (DEBUG) Timber.d("saveStat %s", question);
        getContentResolver().update(QuizProvider.QUESTION_URI, Question.getContentValues(question), "_ID = " + question.getId(), null);
    }

    @Override
    public void updateProgress(boolean isRight) {
        if (isRight)
            ++mRightCnt;
        else
            ++mWrongCnt;
        --mProgress;
        setTitle(getResources().getQuantityString(R.plurals.numberOfQuestionsInTest, mProgress, mProgress));
        if (mProgress == 0) {
            showProgress();
        }
    }

    private void showProgress(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (DEBUG) Timber.d("swipe to the result page");
                mPagerAdapter.addResultPage(mRightCnt, mWrongCnt);
                setTitle("Test completed");
                mPager.setCurrentItem(mQuizSize, true);
            }
        }, 2000);
    }

    @Override
    public void swipeToNext(int delay) {
        final int posInFocus = mPager.getCurrentItem();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (posInFocus == mPager.getCurrentItem()) {
                    if (DEBUG) Timber.d("swipe to the next page");
                    mPager.setCurrentItem(posInFocus + 1, true);
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
        return new CursorLoader(this, randQuestionUri, RelationTables.JoinedQuestionTagProjection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (DEBUG) Timber.d("load finished: %d", data.hashCode());
        int size = data.getCount();
        if (size == 0) {
            TextView noTopic = (TextView) findViewById(R.id.no_topic_selected);
            noTopic.setVisibility(View.VISIBLE);
            mPager.setVisibility(View.GONE);
        }
        // load just once
        else if (mQuizSize == 0) {
            mProgress = mQuizSize = size;
            mPagerAdapter.setData(data);
            setTitle(getResources().getQuantityString(R.plurals.numberOfQuestionsInTest, mProgress, mProgress));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (DEBUG) Timber.d("onLoaderReset");
    }
}

