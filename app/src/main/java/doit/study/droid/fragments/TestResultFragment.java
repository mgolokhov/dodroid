package doit.study.droid.fragments;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;

import doit.study.droid.R;
import timber.log.Timber;

public class TestResultFragment extends LifecycleLogFragment {
    private ProgressBar progressBar;
    private boolean mShowed = false;

    {
        DEBUG = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_result_test, parent, false);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        return v;
    }

    @Override
    public void setUserVisibleHint(boolean visible){
        super.setUserVisibleHint(visible);
        if (DEBUG) Timber.d("setUserVisibleHint");
//        if (visible && isResumed()){
            updateProgress();
//        }
    }

    private void updateProgress(){
            ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, 500); // see this max value coming back here, we animate towards that value
            animation.setDuration(5000); //in milliseconds
            animation.setInterpolator(new DecelerateInterpolator());
            animation.start();
    }
}
