package doit.study.droid;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import doit.study.droid.data.Question;


public class QuestionsPagerAdapter extends FragmentStatePagerAdapter {
    private static final boolean DEBUG = true;
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
        if (mCursor != null)
            return mCursor.getCount();
        else
            return 0;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        StringBuffer title = new StringBuffer();
        for (String tag: mQuestion.getTags())
            title.append(tag).append(" ");
        title.append(String.format(" %d/%d", position+1, getCount()));
//        Question q = mQuizData.getQuestionById(mQuestionIds.get(position));
//        int rCnt = q.getRightCounter();
//        int wCnt = q.getWrongCounter();
//        Question.Status st = q.getStatus();
//        title.append(String.format(" %d/%d/%s", rCnt, wCnt, st));
        return title;
    }


    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return mCursor;
    }
}
