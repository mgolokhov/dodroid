package doit.study.droid;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import doit.study.droid.model.QuizData;


public class QuestionsPagerAdapter extends FragmentStatePagerAdapter {
    private final String TAG = "NSA " + getClass().getName();
    private QuizData mQuizData;
    private final List<Integer> mQuestionIds;
    private FragmentObserver mFragmentObserver = new FragmentObserver();

    private static class FragmentObserver extends Observable {
        @Override
        public void notifyObservers() {
            // Set the changed flag to true, otherwise observers won't be notified.
            setChanged();
            super.notifyObservers();
        }
    }

    public QuestionsPagerAdapter(FragmentManager fm, QuizData quizData, List<Integer> questionIds) {
        super(fm);
        mQuizData = quizData;
        mQuestionIds = questionIds;
    }

    public void updateFragments(ViewGroup container, int posInFocus){
        Observer curFragment  = (Observer) instantiateItem(container, posInFocus);
        mFragmentObserver.deleteObserver(curFragment);
        mFragmentObserver.notifyObservers();
    }


    @Override
    public Fragment getItem(int position) {
        Log.i(TAG, "getItem, pos=" + position);
        Fragment fragment = QuestionFragment.newInstance(mQuestionIds.get(position));
        mFragmentObserver.addObserver((Observer) fragment);
        return fragment;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position){
        Log.i(TAG, "instantiateItem, pos="+position);
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mFragmentObserver.deleteObserver((Observer)object);
        super.destroyItem(container, position, object);
    }

    @Override
    public int getCount() {
        return mQuestionIds.size();
    }


    @Override
    public CharSequence getPageTitle(int position) {
        StringBuffer title = new StringBuffer();
        for (String tag: mQuizData.getQuestionById(mQuestionIds.get(position)).getTags())
            title.append(tag+" ");
        title.append(String.format(" %d/%d", position+1, getCount()));
//        Question q = mQuizData.getQuestionById(mQuestionIds.get(position));
//        int rCnt = q.getRightCounter();
//        int wCnt = q.getWrongCounter();
//        Question.Status st = q.getStatus();
//        title.append(String.format(" %d/%d/%s", rCnt, wCnt, st));
        return title;
    }

}
