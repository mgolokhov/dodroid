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
    private final int QUIZ_SIZE = 10;
    private int mRightAnswered = 0;
    private static final int QUESTION_LOADER = 0;
    private InterrogatorPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG) Timber.d("onCreate");
        mSelectionId = R.id.nav_do_it;
        getLayoutInflater().inflate(R.layout.activity_interrogator, mContainerContent);
        getSupportLoaderManager().initLoader(QUESTION_LOADER, null, InterrogatorActivity.this);
        mPager = (ViewPager)findViewById(R.id.view_pager);
        configPagerTabStrip();
        mPagerAdapter = new InterrogatorPagerAdapter(getSupportFragmentManager());
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
        if (DEBUG) Timber.d("saveStat %s", question);
        getContentResolver().update(QuizProvider.QUESTION_URI, Question.getContentValues(question), "_ID = "+question.getId(), null);
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
                    if (DEBUG) Timber.d("swipe to the next page");
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
        return new CursorLoader(this, randQuestionUri, RelationTables.JoinedQuestionTagProjection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (DEBUG) Timber.d("load finished: %d", data.hashCode());
        TextView no_topic = (TextView) findViewById(R.id.no_topic_selected);
        if (data.getCount() == 0) {
            no_topic.setVisibility(View.VISIBLE);
            mPager.setVisibility(View.GONE);
        }
        else {
            mPagerAdapter.setData(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (DEBUG) Timber.d("onLoaderReset");
    }
}

