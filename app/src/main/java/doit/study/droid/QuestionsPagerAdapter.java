package doit.study.droid;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import doit.study.droid.data.Question;


public class QuestionsPagerAdapter extends FragmentStatePagerAdapter {
    private static final boolean DEBUG = false;
    private final String TAG = "NSA " + getClass().getName();
    private Cursor mCursor;
    private Question mQuestion;

    public QuestionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {
        if (DEBUG) Log.d(TAG, "getItem, pos=" + position);
        mCursor.moveToPosition(position);
        mQuestion = Question.newInstance(mCursor);
        Fragment fragment = QuestionFragment.newInstance(mQuestion);
        return fragment;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position){
        if (DEBUG) Log.d(TAG, "instantiateItem, pos="+position);
        return super.instantiateItem(container, position);
    }


    @Override
    public int getCount() {
        if (DEBUG) Log.d(TAG, "counter "+ (mCursor == null ? 0 : mCursor.getCount()));
        if (mCursor != null)
            return mCursor.getCount();
        else
            return 0;
    }

    // don't know why, but getPageTitle called before getItem
    @Override
    public CharSequence getPageTitle(int position) {
        if (DEBUG) Log.d(TAG, "title pos: "+position+" questions: "+mQuestion);
        StringBuffer title = new StringBuffer();
        // at exit pager asks title, cursor invalid
        if (mCursor != null) {
            mCursor.moveToPosition(position);
            for (String tag : Question.newInstance(mCursor).getTags())
                title.append(tag).append(" ");
            title.append(String.format(" %d/%d", position + 1, getCount()));
        }
        return title;
    }


    public void swapCursor(Cursor newCursor) {
        if (DEBUG) Log.d(TAG, "swap cursor");
        mCursor = newCursor;
        notifyDataSetChanged();
    }
}
