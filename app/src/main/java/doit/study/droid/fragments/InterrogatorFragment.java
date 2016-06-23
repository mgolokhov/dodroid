package doit.study.droid.fragments;

import android.app.Activity;
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
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
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
import java.util.Random;

import doit.study.droid.BuildConfig;
import doit.study.droid.R;
import doit.study.droid.activities.SettingsActivity;
import doit.study.droid.app.App;
import doit.study.droid.data.Question;
import doit.study.droid.utils.Sound;
import doit.study.droid.utils.Views;
import timber.log.Timber;


public class InterrogatorFragment extends LifecycleLogFragment implements View.OnClickListener{
    private static final boolean DEBUG = false;
    private static final int REPORT_DIALOG_REQUEST_CODE = 0;
    public static final String REPORT_DIALOG_TAG = "fragment_dialog_dislike";
    // Callbacks
    private OnFragmentActivityChatter mOnFragmentActivityChatter;
    // Keys for bundle to save state
    private static final String QUESTION_KEY = "doit.study.dodroid.id_key";
    private static final String VOTE_STATE_KEY = "doit.study.dodroid.vote_state_key";
    private static final String QUESTION_STATE_KEY = "doit.study.dodroid.question_state_key";
    // Model stuff
    private static final int SWIPE_DELAY = 2000;
    private static final int ATTEMPTS_LIMIT = 2;

    private enum State {NEW, TRIED, ANSWERED_RIGHT, ANSWERED_WRONG}
    private State mState = State.NEW;
    private enum Vote {NONE, LIKED, DISLIKED}
    private Vote mVote = Vote.NONE;
    private Question mCurrentQuestion;
    private Sound mSound;
    private boolean mIsSoundOn;

    private String[] mFeedbackWrongAnswered;
    private String[] mFeedbackRightAnswered;
    // View stuff
    private View mView;
    private List<CheckBox> mvCheckBoxes;
    private FloatingActionButton mvCommitButton;
    private TextView mvQuestionText;
    private ViewGroup mvAnswersLayout;
    private Toast mvToast;


    // Host Activity must implement these interfaces
    public interface OnFragmentActivityChatter {
        void swipeToNext(int delay);
        void saveStat(Question question);
        void updateProgress();
    }

    public static InterrogatorFragment newInstance(Question question) {
        if (DEBUG) Timber.d("newInstance %s", question);
        InterrogatorFragment fragment = new InterrogatorFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(QUESTION_KEY, question);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_question, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch(menuItem.getItemId()){
            case R.id.doc_reference: {
                openDocumentation();
                return true;
            }
            case R.id.action_settings: {
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
        setHasOptionsMenu(true);
        mFeedbackRightAnswered = getResources().getStringArray(R.array.feedback_right_answer);
        mFeedbackWrongAnswered = getResources().getStringArray(R.array.feedback_wrong_answer);
        mSound = Sound.newInstance(getContext());
        if (savedInstanceState != null) {
            mState = (State) savedInstanceState.getSerializable(QUESTION_STATE_KEY);
            mVote = (Vote) savedInstanceState.getSerializable(VOTE_STATE_KEY);
            mCurrentQuestion = savedInstanceState.getParcelable(QUESTION_KEY);
        }
        else
            mCurrentQuestion = getArguments().getParcelable(QUESTION_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mkViewLinks(inflater, container);
        updateViews(savedInstanceState);
        return mView;
    }


    private void mkViewLinks(LayoutInflater inflater, ViewGroup container){
        if (DEBUG) Timber.d("mkViewLinks %s", hashCode());
        mView = inflater.inflate(R.layout.fragment_interrogator, container, false);
        mvQuestionText = (TextView) mView.findViewById(R.id.question);
        mvAnswersLayout = (ViewGroup) mView.findViewById(R.id.answers);
        mvCommitButton = (FloatingActionButton) mView.findViewById(R.id.commit_button);
        mvCommitButton.setOnClickListener(this);
        mView.findViewById(R.id.thump_up_button).setOnClickListener(this);
        mView.findViewById(R.id.thump_down_button).setOnClickListener(this);
    }


    // Map data from the current Question to the View elements
    private void updateViews(Bundle savedInstanceState) {
        mvQuestionText.setText(mCurrentQuestion.getText());
        updateAnswers(savedInstanceState);
        updateCommitButton();
        wodooWithPadding();
    }

    private void wodooWithPadding(){
        // add padding to the answers list after the view is build
        // so floating button doesn't overlay content
        final LinearLayout vCtrlPanel = (LinearLayout) mView.findViewById(R.id.ctrl_panel);
        ViewTreeObserver vto = vCtrlPanel.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = vCtrlPanel.getViewTreeObserver();
                int bottom_padding = vCtrlPanel.getHeight();
                if (DEBUG) Timber.d("height %d", bottom_padding);
                mvAnswersLayout.setPadding(0, 0, 0, bottom_padding);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeOnGlobalLayoutListener(this);
                } else {
                    obs.removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    private void updateAnswers(Bundle savedInstanceState){
        boolean isDisabled = mState == State.ANSWERED_RIGHT || mState == State.ANSWERED_WRONG;

        if (isDisabled && mvAnswersLayout.getChildCount()!=0) {
            for(CheckBox c: mvCheckBoxes)
                c.setEnabled(false);
        }
        else {
            mvAnswersLayout.removeAllViewsInLayout();
            mvCheckBoxes = new ArrayList<>();
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            for (String answer : generateAnswers()) {
                View v = inflater.inflate(R.layout.fragment_interrogator_answer_item, mvAnswersLayout, true);
                CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkbox_id);
                checkBox.setText(answer);
                checkBox.setId(Views.generateViewId());
                if (null != savedInstanceState) {
                    checkBox.setChecked(savedInstanceState.getInt(answer) == 1);
                    if (isDisabled)
                        checkBox.setEnabled(false);
                }
                mvCheckBoxes.add(checkBox);
            }
        }
    }

    private List<String> generateAnswers(){
        List<String> allAnswers = new ArrayList<>();
        allAnswers.addAll(mCurrentQuestion.getRightAnswers());
        allAnswers.addAll(mCurrentQuestion.getWrongAnswers());
        Collections.shuffle(allAnswers);
        return allAnswers;
    }


    private void updateCommitButton(){
        switch(mState){
            case ANSWERED_RIGHT:
                mvCommitButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_sentiment_satisfied_black_24dp));
                break;
            case ANSWERED_WRONG:
                mvCommitButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_sentiment_dissatisfied_black_24dp));
                break;
            default:
                return;
        }
        mvCommitButton.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
        mvCommitButton.setEnabled(false);
    }

    private void checkAnswer() {
        if (DEBUG) Timber.d("checkAnswer %d", hashCode());
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

    private void updateModel(){
        if (mState == State.ANSWERED_RIGHT) {
            mCurrentQuestion.incRightCounter();
            mOnFragmentActivityChatter.updateProgress();
        }
        else {
            if (mCurrentQuestion.incWrongCounter() >= ATTEMPTS_LIMIT)
                mState = State.ANSWERED_WRONG;
        }
    }

    private void showResultToast(){
        if (mvToast != null)
            mvToast.cancel();
        mvToast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
        mvToast.setGravity(Gravity.CENTER, 0, 0);
        TextView v = (TextView) mvToast.getView().findViewById(android.R.id.message);
        Random rand = new Random();
        if (mState == State.ANSWERED_RIGHT) {
            mvToast.setText(mFeedbackRightAnswered[rand.nextInt(mFeedbackRightAnswered.length)]);
            v.setTextColor(ContextCompat.getColor(getActivity(), R.color.toastRight));
        } else {
            mvToast.setText(mFeedbackWrongAnswered[rand.nextInt(mFeedbackWrongAnswered.length)]);
            v.setTextColor(ContextCompat.getColor(getActivity(), R.color.toastWrong));
        }
        mvToast.show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(QUESTION_STATE_KEY, mState);
        outState.putSerializable(VOTE_STATE_KEY, mVote);
        outState.putParcelable(QUESTION_KEY, mCurrentQuestion);
        for(CheckBox cb: mvCheckBoxes) {
            outState.putInt(cb.getText().toString(), cb.isChecked() ? 1 : 0);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getContext());
        mIsSoundOn = SP.getBoolean(getString(R.string.pref_sound), true);
    }

    @Override
    public void onClick(View v) {
        if (DEBUG) Timber.d(String.valueOf(v.getId()));
        switch (v.getId()) {
            case R.id.commit_button:
                handleCommitButton();
                break;
            case R.id.thump_up_button:
                handleThumpUpButton();
                break;
            case R.id.thump_down_button:
                handleThumpDownButton();
                break;
            default:
                break;
        }
    }

    private void handleThumpUpButton(){
        if (isVoted()) return;
        mVote = Vote.LIKED;
        sendReport(getString(R.string.report_because), getString(R.string.like), mCurrentQuestion.getText());
        String mes = getResources().getString(R.string.thank_upvote);
        Toast t = Toast.makeText(getActivity(), mes, Toast.LENGTH_SHORT);
        t.setGravity(Gravity.CENTER, 0, 0);
        t.show();
    }

    private void handleThumpDownButton(){
        if (isVoted()) return;
        DislikeDialogFragment dislikeDialog = DislikeDialogFragment.newInstance(mCurrentQuestion.getText());
        dislikeDialog.setTargetFragment(this, REPORT_DIALOG_REQUEST_CODE);
        dislikeDialog.show(getFragmentManager(), REPORT_DIALOG_TAG);
    }

    private boolean isVoted(){
        if (mVote != Vote.NONE) {
            String mes = getResources().getString(R.string.already_voted);
            Toast t = Toast.makeText(getActivity(), mes, Toast.LENGTH_SHORT);
            t.setGravity(Gravity.CENTER, 0, 0);
            t.show();
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;
        if (requestCode == REPORT_DIALOG_REQUEST_CODE) {
            if (data != null) {
                String label = mCurrentQuestion.getText() + data.getStringExtra(DislikeDialogFragment.EXTRA_CAUSE);
                sendReport(getString(R.string.report_because), getString(R.string.dislike), label);
                mVote = Vote.DISLIKED;
                String mes = getResources().getString(R.string.report_was_sent);
                Toast t = Toast.makeText(getActivity(), mes, Toast.LENGTH_SHORT);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
            }
        }
    }

    private void sendReport(String category, String action, String label){
        if (!BuildConfig.DEBUG) {
            Tracker tracker = ((App) getActivity().getApplication()).getTracker();
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action)
                    .setLabel(label)
                    .build());
        }
    }


    private void handleCommitButton(){
        checkAnswer();
        showResultToast();
        showDocumentationSnackBar();
        updateModel();
        updateViews(null);
        if (mIsSoundOn)
            mSound.play(mState == State.ANSWERED_RIGHT);
        if (mState == State.ANSWERED_RIGHT) {
            mOnFragmentActivityChatter.swipeToNext(SWIPE_DELAY);
        }
    }

    private void showDocumentationSnackBar(){
        if (mState != State.ANSWERED_RIGHT) {
            String mes = getResources().getString(R.string.check_docs);
            Snackbar.make(mView, mes, Snackbar.LENGTH_LONG)
                    .setAction(getResources().getString(R.string.go), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openDocumentation();
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onPause() {
        if (DEBUG) Timber.d("onPause, should be save");
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
