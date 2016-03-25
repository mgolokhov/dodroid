package doit.study.droid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
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
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import doit.study.droid.model.GlobalData;
import doit.study.droid.model.Question;


public class QuestionFragment extends LifecycleLoggingFragment implements View.OnClickListener{
    private static final boolean DEBUG = true;
    // Callbacks
    private OnFragmentActivityChatter mOnFragmentActivityChatter;
    // Keys for bundle to save state
    private static final String ID_KEY = "doit.study.dodroid.id_key";
    private static final String QUESTION_STATE_KEY = "doit.study.dodroid.question_state_key";
    // Model stuff
    private static final int SWIPE_BY_RIGHT_ANSWER_DELAY = 2000;
    private static final int ATTEMPTS_LIMIT = 2;
    private enum State {NEW, TRIED, ANSWERED_RIGHT, ANSWERED_WRONG}
    private State mState = State.NEW;
    private Question mCurrentQuestion;
    private Sound mSound;
    private boolean mIsSoundOn;
    // View stuff
    private View mView;
    private List<CheckBox> mvCheckBoxes;
    private FloatingActionButton mvCommitButton;
    private TextView mvQuestionText;
    private ViewGroup mvAnswersLayout;
    private Toast mvToast;
    private Menu mvMenu;

    // Host Activity must implement these interfaces
    public interface OnFragmentActivityChatter {
        void swipeToNext(int delay);
        void saveStat(Question question);
        void updateProgress();
        Question getQuestion(int id);
    }

    public static QuestionFragment newInstance(int questionId) {
        if (DEBUG) Log.d("NSA", "newInstance " + questionId);
        QuestionFragment fragment = new QuestionFragment();
        // for a logging purpose
        fragment.ID = questionId;
        Bundle bundle = new Bundle();
        bundle.putInt(ID_KEY, questionId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_question, menu);
        mvMenu = menu;
        if (mState == State.ANSWERED_WRONG) {
            MenuItem mi = mvMenu.findItem(R.id.doc_reference);
            mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch(menuItem.getItemId()){
            case(R.id.doc_reference):{
                openDocumentation();
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

    private void openDocumentation(){
        if (mCurrentQuestion.getDocRef().isEmpty())
            Toast.makeText(getActivity(), "Not yet for this question", Toast.LENGTH_SHORT).show();
        else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(mCurrentQuestion.getDocRef()));
            startActivity(intent);
        }
    }

    @Override
    public void onAttach(Context activity){
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
        mCurrentQuestion = mOnFragmentActivityChatter.getQuestion(getArguments().getInt(ID_KEY));
        mSound = Sound.newInstance(getContext());
        if (savedInstanceState != null)
            mState = (State) savedInstanceState.getSerializable(QUESTION_STATE_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mkViewLinks(inflater, container);
        updateStaticViews();
        updateDynamicViews();
        return mView;
    }


    private void mkViewLinks(LayoutInflater inflater, ViewGroup container){
        if (DEBUG) Log.d(TAG, "mkViewLinks " + ID);

        mView = inflater.inflate(R.layout.fragment_questions, container, false);
        mvQuestionText = (TextView) mView.findViewById(R.id.question);
        mvAnswersLayout = (ViewGroup) mView.findViewById(R.id.answers);
        mvCommitButton = (FloatingActionButton) mView.findViewById(R.id.commit_button);
        mvCommitButton.setOnClickListener(this);
        mView.findViewById(R.id.thump_up_button).setOnClickListener(this);
        mView.findViewById(R.id.thump_down_button).setOnClickListener(this);
    }


    // Map data from the current Question to the View elements
    private void updateStaticViews() {
        if (DEBUG) Log.d(TAG, "updateStaticViews " + ID);
        if (DEBUG) Log.d(TAG, "mState " + mState);

        mvQuestionText.setText(mCurrentQuestion.getText());

        // Create checkboxes dynamically
        if (mvAnswersLayout.getChildCount() == 0) {
            mvAnswersLayout.removeAllViewsInLayout();
            List<String> allAnswers = new ArrayList<>();
            allAnswers.addAll(mCurrentQuestion.getRightAnswers());
            allAnswers.addAll(mCurrentQuestion.getWrongAnswers());
            Collections.shuffle(allAnswers);

            mvCheckBoxes = new ArrayList<>();
            for (String answer : allAnswers) {
                CheckBox checkBox = new CheckBox(getContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.gravity = Gravity.CENTER;
                checkBox.setLayoutParams(lp);
                checkBox.setGravity(Gravity.CENTER);
                checkBox.setText(answer);
                mvCheckBoxes.add(checkBox);
                mvAnswersLayout.addView(checkBox,lp);
            }
        }

        // add padding to the answers list after the view is build
        // so floating button doesn't overlay content
        final LinearLayout vCtrlPanel = (LinearLayout) mView.findViewById(R.id.ctrl_panel);
        ViewTreeObserver vto = vCtrlPanel.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = vCtrlPanel.getViewTreeObserver();
                int bottom_padding = vCtrlPanel.getHeight();
                if (DEBUG) Log.d(TAG, "height "+bottom_padding);
                mvAnswersLayout.setPadding(0, 0, 0, bottom_padding);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeOnGlobalLayoutListener(this);
                } else {
                    obs.removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    private void updateDynamicViews(){
        updateCommitButton();
    }


    private void updateCommitButton(){
        switch(mState){
            case ANSWERED_RIGHT:
                mvCommitButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_sentiment_satisfied_black_24dp));
                break;
            case ANSWERED_WRONG:
                mvCommitButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_sentiment_dissatisfied_black_24dp));
                if (mvMenu != null) {
                    MenuItem mi = mvMenu.findItem(R.id.doc_reference);
                    mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                }
                break;
            default:
                return;
        }
        mvCommitButton.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
        mvCommitButton.setEnabled(false);
    }

    private void checkAnswer() {
        if (DEBUG) Log.d(TAG, "checkAnswer " + ID);
        mState = State.ANSWERED_RIGHT;
        for (CheckBox cb : mvCheckBoxes) {
            // You can have multiple right answers
            String cbText = cb.getText().toString();
            if (cb.isChecked()) {
                if (!mCurrentQuestion.getRightAnswers().contains(cbText)) {
                    mState = State.TRIED;
                    break;
                }
            } else if (mCurrentQuestion.getRightAnswers().contains(cbText)) {
                mState = State.TRIED;
                break;
            }
        }
    }

    private void updateDynamicModel(){
        if (mState == State.ANSWERED_RIGHT) {
            mCurrentQuestion.incRightCounter();
            mOnFragmentActivityChatter.updateProgress();
        }
        else {
            if (mCurrentQuestion.incWrongCounter() >= ATTEMPTS_LIMIT)
                mState = State.ANSWERED_WRONG;
        }
    }

    private void showToast(){
        if (mvToast != null)
            mvToast.cancel();
        mvToast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
        mvToast.setGravity(Gravity.CENTER, 0, 0);
        TextView v = (TextView) mvToast.getView().findViewById(android.R.id.message);
        if (mState == State.ANSWERED_RIGHT) {
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
        outState.putSerializable(QUESTION_STATE_KEY, mState);
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getContext());
        mIsSoundOn = SP.getBoolean(getString(R.string.pref_sound), true);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, ""+v.getId());
        switch (v.getId()) {
            case (R.id.commit_button):
                handleCommitButton();
                break;
            case (R.id.thump_up_button):
                handleThumpUpButton();
                break;
            case (R.id.thump_down_button):
                handleThumpDownButton();
                break;
        }
    }

    private void handleThumpUpButton(){
        Tracker tracker = ((GlobalData) getActivity().getApplication()).getTracker();
        tracker.send(new HitBuilders.EventBuilder()
                        .setCategory(getString(R.string.report_because))
                        .setAction("like")
                        .setLabel(mCurrentQuestion.getText())
                        .build());
        Toast.makeText(getActivity(), "Thx:)", Toast.LENGTH_SHORT).show();
    }

    private void handleThumpDownButton(){
        DislikeDialog dislikeDialog = DislikeDialog.newInstance(mCurrentQuestion.getText());
        dislikeDialog.show(getFragmentManager(), "dislike_dialog");
        //Toast.makeText(getActivity(), "handleThumpDownButton", Toast.LENGTH_SHORT).show();
    }

    private void handleCommitButton(){
        checkAnswer();
        showToast();
        showSnackBar();
        updateDynamicModel();
        updateDynamicViews();
        if (mIsSoundOn)
            mSound.play(mState == State.ANSWERED_RIGHT);
        if (mState == State.ANSWERED_RIGHT) {
            mOnFragmentActivityChatter.swipeToNext(SWIPE_BY_RIGHT_ANSWER_DELAY);
        }
    }

    private void showSnackBar(){
        if (mState != State.ANSWERED_RIGHT)
            Snackbar.make(mView, "Check documentation", Snackbar.LENGTH_LONG)
                    .setAction("GO", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openDocumentation();
                        }
                    })
                    .show();
    }

    @Override
    public void onPause() {
        mOnFragmentActivityChatter.saveStat(mCurrentQuestion);
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
