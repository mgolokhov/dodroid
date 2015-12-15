package doit.study.droid;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.List;
import java.util.Observable;
import java.util.Observer;


class QuestionsPagerAdapter extends FragmentStatePagerAdapter {
    private final String TAG = "NSA " + getClass().getName();
    private QuizData mQuizData;
    private FragmentObserver mFragmentObserver = new FragmentObserver();
    private List<Integer> mQuestionIds;

    private static class FragmentObserver extends Observable {
        @Override
        public void notifyObservers() {
            // Set the changed flag to true, otherwise observers won't be notified.
            setChanged();
            super.notifyObservers();
        }
    }

    public QuestionsPagerAdapter(FragmentManager fm, QuizData quizData) {
        super(fm);
        this.mQuizData = quizData;
        this.mQuestionIds = quizData.getQuestionIdsToWorkWith();
    }

    public void updateFragments(ViewGroup container, int posInFocus){
        Observer curFragment  = (Observer) instantiateItem(container, posInFocus);
        mFragmentObserver.deleteObserver(curFragment);
        mFragmentObserver.notifyObservers();
    }


    @Override
    public Fragment getItem(int position) {
        Log.i(TAG, "getItem, pos=" + position);
        int questionId = mQuestionIds.get(position);
        Fragment fragment = QuestionFragment.newInstance(position, questionId);
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
        String title = "";
        for (String tag: mQuizData.getById(mQuizData.idAtPosition(position)).getTags())
            title += tag+" ";
        title += String.format("\t%d/%d", position+1, getCount());
        return title;
    }

}
