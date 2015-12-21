package doit.study.droid;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.Observable;
import java.util.Observer;


class QuestionsPagerAdapter extends FragmentStatePagerAdapter {
    private final String TAG = "NSA " + getClass().getName();
    private Question[] mQuestions;
    private FragmentObserver mFragmentObserver = new FragmentObserver();

    private static class FragmentObserver extends Observable {
        @Override
        public void notifyObservers() {
            // Set the changed flag to true, otherwise observers won't be notified.
            setChanged();
            super.notifyObservers();
        }
    }

    public QuestionsPagerAdapter(FragmentManager fm, Question[] quizData) {
        super(fm);
        mQuestions = quizData;
    }

    public void updateFragments(ViewGroup container, int posInFocus){
        Observer curFragment  = (Observer) instantiateItem(container, posInFocus);
        mFragmentObserver.deleteObserver(curFragment);
        mFragmentObserver.notifyObservers();
    }


    @Override
    public Fragment getItem(int position) {
        Log.i(TAG, "getItem, pos=" + position);
        QuizData.Id id = mQuestions[position].getId();
        Fragment fragment = QuestionFragment.newInstance(id);
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
        return mQuestions.length;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        String tags = mQuestions[position].getTags();
        String title = String.format("%s\t%d/%d", tags, position+1, getCount());
        return title;
    }

}
