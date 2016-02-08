package doit.study.droid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import doit.study.droid.model.GlobalData;
import doit.study.droid.model.Question;
import doit.study.droid.model.QuizData;


public class QuestionFragment extends LifecycleLoggingFragment implements View.OnClickListener, Observer {
    private static final boolean DEBUG = true;
    // Callbacks
    private OnFragmentActivityChatter mOnFragmentActivityChatter;
    // Keys for bundle to save state
    private static final String ID_KEY = "doit.study.dodroid.id_key";
    private static final String ANSWER_STATE_KEY = "doit.study.dodroid.answer_state_key";
    private Sound mSound;
    // Model stuff
    private QuizData mQuizData;
    private Question mCurrentQuestion;
    private boolean mGotRightAnswer;
    private boolean mIsSoundOn;
    // View stuff
    private View mView;
    private List<CheckBox> mvCheckBoxes;
    private FloatingActionButton mvCommitButton;
    private TextView mvQuestionText;
    private LinearLayout mvAnswersLayout;
    private TextView mvRight;
    private TextView mvWrong;
    private Toast mvToast;
    private int mWrongCounterForHint;
    ////////////////////////////////////////////////
    // Host Activity must implement these interfaces
    ////////////////////////////////////////////////
    public interface OnFragmentActivityChatter {
        void updateFragments();
        void swipeToNext(int delay);
        int getTotalRightCounter();
        int getTotalWrongCounter();
        int incTotalWrongCounter();
        int incTotalRightCounter();
    }
    //////////////////////////////////////////////////

    public static QuestionFragment newInstance(int questionId) {
        if (DEBUG) Log.i("NSA", "newInstance "+questionId);
        // Add Bundle args if needed here before returning new instance of this class
        QuestionFragment fragment = new QuestionFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ID_KEY, questionId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void update(Observable observable, Object data) {
        if (DEBUG) Log.i(TAG, "update "+ID);
        // Will be called for cached fragments
        updateDynamicViews(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_question, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch(menuItem.getItemId()){
            case(R.id.doc_reference):{
                checkDocRef();
                return true;
            }
            case(R.id.action_settings):{
                startActivity(new Intent(getContext(), SettingsActivity.class));
                return true;
            }
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void checkDocRef(){
        if (mCurrentQuestion.getDocRef().isEmpty())
            Toast.makeText(getActivity(), "Not yet", Toast.LENGTH_SHORT).show();
        else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(mCurrentQuestion.getDocRef()));
            startActivity(intent);
        }
    }

    @Override
    public void onAttach(Context activity){
        // for a logging purpose
        ID = getArguments().getInt(ID_KEY);
        super.onAttach(activity);
        try {
            mOnFragmentActivityChatter = (OnFragmentActivityChatter) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentActivityChatter");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        // Do non-graphical initialisations, get data
        updateStaticModel();
        mSound = Sound.newInstance(getContext());
    }


    private void updateStaticModel() {
        Activity activity = getActivity();
        mQuizData = ((GlobalData)activity.getApplication()).getQuizData();
        int questionId = getArguments().getInt(ID_KEY);
        mCurrentQuestion = mQuizData.getQuestionById(questionId);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mkViewLinks(inflater, container);
        updateAllViews(savedInstanceState);
        return mView;
    }


    private void mkViewLinks(LayoutInflater inflater, ViewGroup container){
        if (DEBUG) Log.i(TAG, "mkViewLinks "+ID);
        // You can not use the findViewById method the way you can in an Activity in a Fragment
        // So we get a reference to the view/layout_file that we used for this Fragment
        // That allows use to then reference the views by id in that file
        mView = inflater.inflate(R.layout.fragment_questions, container, false);
        mvQuestionText = (TextView) mView.findViewById(R.id.question);
        mvAnswersLayout = (LinearLayout) mView.findViewById(R.id.answers);
        mvCommitButton = (FloatingActionButton) mView.findViewById(R.id.commit_button);
        // You can not add onclick listener to a button in a fragment's xml
        // So we implement OnClickListener interface, check onClick() method
        mvCommitButton.setOnClickListener(this);
        mvRight = (TextView) mView.findViewById(R.id.right_counter);
        mvWrong = (TextView) mView.findViewById(R.id.wrong_counter);
    }


    // Map data from the current Question to the View elements
    private void updateAllViews(Bundle savedInstanceState) {
        if (DEBUG) Log.i(TAG, "updateAllViews "+ID);

        updateDynamicViews(null);
        mvQuestionText.setText(mCurrentQuestion.getText());
        mvRight.setTextColor(Color.GREEN);
        mvWrong.setTextColor(Color.RED);

        if (savedInstanceState != null) {
            mGotRightAnswer = savedInstanceState.getInt(ANSWER_STATE_KEY) == 1;
            if (mGotRightAnswer) {
                if (DEBUG) Log.d(TAG, "mGotRightAnswer " + mGotRightAnswer);
                setCommitButton();
            }

        }

        // Create checkboxes dynamically
        if (mvAnswersLayout.getChildCount() == 0) {
            mvAnswersLayout.removeAllViewsInLayout();
            List<String> allAnswers = new ArrayList<>();
            allAnswers.addAll(mCurrentQuestion.getRightAnswers());
            allAnswers.addAll(mCurrentQuestion.getWrongAnswers());
            Collections.shuffle(allAnswers);

            mvCheckBoxes = new ArrayList<>();
            for (String answer : allAnswers) {
                // Can not use "this" keyword for constructor here.
                // Requires a Context and Fragment class does not inherit from Context
                CheckBox checkBox = new CheckBox(getContext());
                checkBox.setText(answer);
                mvCheckBoxes.add(checkBox);
                mvAnswersLayout.addView(checkBox);
            }
        }
    }

    private void updateDynamicViews(Boolean isRight){
        if (isRight == null){
            mvRight.setText(String.format("%d", mOnFragmentActivityChatter.getTotalRightCounter()));
            mvWrong.setText(String.format("%d", mOnFragmentActivityChatter.getTotalWrongCounter()));
        }
        else if (isRight) {
            mvRight.setText(String.format("%d", mOnFragmentActivityChatter.getTotalRightCounter()));
            setCommitButton();
        }
        else {
            mvWrong.setText(String.format("%d", mOnFragmentActivityChatter.getTotalWrongCounter()));
            if (mWrongCounterForHint >= 2)
                Snackbar.make(getView(), "Check documentation", Snackbar.LENGTH_LONG)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                checkDocRef();
                            }
                        })
                        .show();
        }
    }

    private void setCommitButton(){
        if (mGotRightAnswer) {
            mvCommitButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_insert_emoticon_black_48dp));
            mvCommitButton.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
            mvCommitButton.setEnabled(false);
        }
    }

    private boolean isRightAnswer() {
        if (DEBUG) Log.i(TAG, "isRightAnswer " + ID);
        boolean goodJob = true;
        for (CheckBox cb : mvCheckBoxes) {
            // You can have multiple right answers
            String cbText = cb.getText().toString();
            if (cb.isChecked()) {
                if (!mCurrentQuestion.getRightAnswers().contains(cbText)) {
                    goodJob = false;
                    break;
                }
            } else if (mCurrentQuestion.getRightAnswers().contains(cbText)) {
                goodJob = false;
                break;
            }
        }
        return goodJob;
    }

    private void updateDynamicModel(boolean isRight){
        if (isRight) {
            mCurrentQuestion.incRightCounter();
            mOnFragmentActivityChatter.incTotalRightCounter();
            mGotRightAnswer = true;
        }
        else {
            mWrongCounterForHint++;
            mCurrentQuestion.incWrongCounter();
            mOnFragmentActivityChatter.incTotalWrongCounter();
        }
    }

    private void showToast(boolean isRight){
        if (mvToast != null)
            mvToast.cancel();
        mvToast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
        mvToast.setGravity(Gravity.CENTER, 0, 0);
        TextView v = (TextView) mvToast.getView().findViewById(android.R.id.message);
        if (isRight) {
            mvToast.setText("Right");
            v.setTextColor(Color.GREEN);
        } else {
            mvToast.setText("Wrong");
            v.setTextColor(Color.RED);
        }
        mvToast.show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ANSWER_STATE_KEY, mGotRightAnswer ? 1 : 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getContext());
        mIsSoundOn = SP.getBoolean(getString(R.string.pref_sound), true);
    }

    @Override
    public void onClick(View v) {
        int delay = 2000;
        switch (v.getId()) {
            case (R.id.commit_button):
                boolean isRight = isRightAnswer();
                showToast(isRight);
                updateDynamicModel(isRight);
                updateDynamicViews(isRight);
                if (mIsSoundOn)
                    mSound.play(isRight);
                mOnFragmentActivityChatter.updateFragments();
                if (isRight) {
                    mOnFragmentActivityChatter.swipeToNext(delay);
                }
                break;
        }
    }

    @Override
    public void onPause() {
        mQuizData.setQuestion(mCurrentQuestion);
        mSound.stop();
        super.onPause();
    }

    @Override
    public void onStop() {
        mSound.release();
        super.onStop();
    }

    @Override
    public void onDetach() {
        // with setRetainInstance(true) can be a leak
        mOnFragmentActivityChatter = null;
        super.onDetach();
    }
}
