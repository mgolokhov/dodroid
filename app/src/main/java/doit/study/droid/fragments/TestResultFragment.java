package doit.study.droid.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.Arrays;

import doit.study.droid.R;
import timber.log.Timber;

public class TestResultFragment extends LifecycleLogFragment {
    private static final String WRONG_CNT_KEY = "doit.study.dodroid.wrong_cnt_key";
    private static final String RIGHT_CNT_KEY = "doit.study.dodroid.right_cnt_key";
    private CircularProgressBar mProgressBar;
    private TextView mPercentage;
    private TextView mTextSummary;
    private TextView mWrongCnt;
    private TextView mRightCnt;
    View mView;
    private AnimatorSet mAnimatorSet;

    {
        DEBUG = true;
    }

    public static Fragment newInstance(int wrongCnt, int rightCnt){
        TestResultFragment testResultFragment = new TestResultFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(WRONG_CNT_KEY, wrongCnt);
        bundle.putInt(RIGHT_CNT_KEY, rightCnt);
        testResultFragment.setArguments(bundle);
        return testResultFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (DEBUG) Timber.d("onCreateView");
        mView = inflater.inflate(R.layout.fragment_result_test, parent, false);
        mProgressBar = (CircularProgressBar) mView.findViewById(R.id.progressBar);
        mPercentage = (TextView) mView.findViewById(R.id.percentage);
        mTextSummary = (TextView) mView.findViewById(R.id.textSummary);
        showTextSummary();
        mRightCnt = (TextView) mView.findViewById(R.id.right_cnt);
        int val = getArguments().getInt(RIGHT_CNT_KEY);
        mRightCnt.setText(String.format(getResources().getString(R.string.test_result_correct), val));
        mWrongCnt = (TextView) mView.findViewById(R.id.wrong_cnt);
        val = getArguments().getInt(WRONG_CNT_KEY);
        mWrongCnt.setText(String.format(getResources().getString(R.string.test_result_wrong), val));
        return mView;
    }

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (DEBUG) Timber.d("setUserVisibleHint");
        if (visible && isResumed()) {
            if (DEBUG) Timber.d("visible");
            mView.post(new Runnable() {
                @Override
                public void run() {
                    animateProgress();
                }
            });
        }
    }

    private void animateProgress() {
        if (DEBUG) Timber.d("animateProgress");
        final int start = 0;
        final int end = getArguments().getInt(RIGHT_CNT_KEY) * 100 /
                (getArguments().getInt(RIGHT_CNT_KEY)+getArguments().getInt(WRONG_CNT_KEY));
        final int duration = 3000; // in milliseconds


        ObjectAnimator progressBarAnimation = ObjectAnimator.ofFloat(mProgressBar, "progress", start, end);
        progressBarAnimation.setDuration(duration);
        progressBarAnimation.setInterpolator(new DecelerateInterpolator());


        ValueAnimator percentageAnimator = new ValueAnimator();
        percentageAnimator.setObjectValues(start, end);
        percentageAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                mPercentage.setText(String.format("%d%%", animation.getAnimatedValue()));
            }
        });
        percentageAnimator.setEvaluator(new TypeEvaluator<Integer>() {
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                return Math.round(startValue + (endValue - startValue) * fraction);
            }
        });
        percentageAnimator.setDuration(duration);

        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0, 1);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0, 1);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0, 1);

        ObjectAnimator textSummaryAnimator = ObjectAnimator.ofPropertyValuesHolder(mTextSummary, alpha, scaleX, scaleY);
        textSummaryAnimator.setDuration(duration+2000); // show text summary a bit slower


        // move from left side off-screen
        ObjectAnimator wrongCntAnimation = ObjectAnimator.ofFloat(mWrongCnt, "X", -mWrongCnt.getWidth(), mWrongCnt.getLeft());
        wrongCntAnimation.setDuration(duration);
        wrongCntAnimation.setInterpolator(new BounceInterpolator());

        // move from right side off-screen
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        ObjectAnimator rightCntAnimation = ObjectAnimator.ofFloat(mRightCnt, "X", screenWidth +  mRightCnt.getWidth(),  mRightCnt.getLeft());
        rightCntAnimation.setDuration(duration);
        rightCntAnimation.setInterpolator(new BounceInterpolator());


        mAnimatorSet = new AnimatorSet();
//        mProgressBar.setProgressWithAnimation(65, duration);
        mAnimatorSet.play(progressBarAnimation)
                .with(percentageAnimator)
                .with(textSummaryAnimator)
                .with(rightCntAnimation)
                .with(wrongCntAnimation);
        mAnimatorSet.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        mView.post(new Runnable() {
            @Override
            public void run() {
                animateProgress();
            }
        });
    }

    @Override
    public void onPause() {
        mAnimatorSet.cancel();
        super.onPause();
    }

    private void showTextSummary(){
        final int end = getArguments().getInt(RIGHT_CNT_KEY) * 100 /
                (getArguments().getInt(RIGHT_CNT_KEY)+getArguments().getInt(WRONG_CNT_KEY));
        if (end <= 40)
            mTextSummary.setText(getResources().getString(R.string.test_result_summary40));
        else if (end > 40 && end <= 70)
            mTextSummary.setText(getResources().getString(R.string.test_result_summary70));
        else if (end > 70 && end < 100)
            mTextSummary.setText(getResources().getString(R.string.test_result_summary99));
        else if (end == 100)
            mTextSummary.setText(getResources().getString(R.string.test_result_summary100));
    }

    @SuppressWarnings("unused")
    private void printViewCoordinates(String s, View v){
        Rect myViewRect = new Rect();
        v.getGlobalVisibleRect(myViewRect);
        float x = myViewRect.left;
        float y = myViewRect.top;
        int [] screen = new int [2];
        int [] window = new int [2];
        v.getLocationOnScreen(screen);
        v.getLocationInWindow(window);
        Timber.d("%s:: abs screen: %s; abs win: %s; left, top:  %d %d; right, bottom: %d %d; width %d",
                s,
                Arrays.toString(screen),
                Arrays.toString(window),
                mWrongCnt.getLeft(),
                mWrongCnt.getTop(),
                mWrongCnt.getRight(),
                mWrongCnt.getBottom(),
                mWrongCnt.getWidth());
    }
}
