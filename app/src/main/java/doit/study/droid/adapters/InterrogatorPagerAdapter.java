package doit.study.droid.adapters;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import doit.study.droid.data.Question;
import doit.study.droid.fragments.InterrogatorFragment;
import doit.study.droid.fragments.TestResultFragment;
import timber.log.Timber;


public class InterrogatorPagerAdapter extends FragmentStatePagerAdapter {
    private static final boolean DEBUG = true;
    private List<Question> mQuestions = new ArrayList<>();
    private int mSize;

    public InterrogatorPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {
//        if (DEBUG) Timber.d("getItem, pos=%d, question=%s", position, mQuestions.get(position));
        if (position < mQuestions.size()) {
            return InterrogatorFragment.newInstance(mQuestions.get(position));
        }
        else {
            return new TestResultFragment();
        }
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position){
        if (DEBUG) Timber.d("instantiateItem, pos=%d", position);
        return super.instantiateItem(container, position);
    }


    @Override
    public int getCount() {
//        if (DEBUG) Timber.d("Size: %d", mSize);
        return mSize;
    }

    // don't know why, but getPageTitle called before getItem
    @Override
    public CharSequence getPageTitle(int position) {
        if (position < mQuestions.size()) {
            if (DEBUG) Timber.d("getPageTitle pos: %d, questions: %s", position, mQuestions.get(position).getId());
            StringBuffer title = new StringBuffer();
            // at exit pager asks title, cursor invalid
            for (String tag : mQuestions.get(position).getTags())
                title.append(tag).append(" ");
            title.append(String.format(" %d/%d", position + 1, mQuestions.size()));
            return title;
        }
        else {
            return "Your Force";
        }
    }

    public void addResultPage(){
        mSize++;
        notifyDataSetChanged();
    }

    public void setData(Cursor newCursor){
        if (DEBUG) Timber.d("setData:id:############");
        if (mQuestions.size() == 0) {
            while (newCursor.moveToNext()) {
                Question q = Question.newInstance(newCursor);
                if (DEBUG) Timber.d("id: %d %s %s", q.getId(), q.getTags(), q.getText());
                mQuestions.add(q);
            }
            mSize = mQuestions.size();
            notifyDataSetChanged();
        }
    }
}
