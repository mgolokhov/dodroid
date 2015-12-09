package doit.study.droid;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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
import java.util.Observable;
import java.util.Observer;


public class QuestionFragment extends LifecycleLoggingFragment implements View.OnClickListener, Observer {
    private static final boolean DEBUG = true;
    private OnFragmentChangeListener mCallback;
    // keys for bundle, to save state
    private static final String ID_KEY = "doit.study.dodroid.id_key";
    private static final String COMMIT_BUTTON_STATE_KEY = "doit.study.dodroid.commit_button_state_key";
    // model stuff
    private Question mCurrentQuestion;
    private QuizData mQuizData;
    private int mPosition;
    private int isEnabledCommitButton = 1;
    // view stuff
    private View mView;
    private ArrayList<CheckBox> mvCheckBoxes;
    private Button mvCommitButton;
    private TextView mvQuestionText;
    private LinearLayout mvAnswersLayout;
    private TextView mvCurrentQuestionNum;
    private TextView mvTotalQuestionNum;
    private TextView mvRight;
    private TextView mvWrong;


    // Factory method
    public static QuestionFragment newInstance(int position) {
        if (DEBUG) Log.i("NSA", "newInstance "+position);
        // add Bundle args if needed here before returning new instance of this class
        QuestionFragment fragment = new QuestionFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ID_KEY, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    // Container Activity must implement this interface
    public interface OnFragmentChangeListener {
        void updateFragments();
        void swipeNext();
    }

    @Override
    public void update(Observable observable, Object data) {
        populate();
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
                if (mCurrentQuestion.getDocRef().isEmpty())
                    Toast.makeText(getActivity(), "Not yet", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(mCurrentQuestion.getDocRef()));
                    startActivity(intent);
                }
                return true;
            }
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onAttach(Context activity){
        // for logging purpose
        ID = ((Integer) getArguments().getInt(ID_KEY)).toString();
        super.onAttach(activity);
        try {
            mCallback = (OnFragmentChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentChangeListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mPosition = getArguments().getInt(ID_KEY);
        mQuizData = ((GlobalData)getActivity().getApplication()).getQuizData();
        mCurrentQuestion = mQuizData.getById(mPosition);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mkViewsLinks(inflater, container);
        updateModel();
        updateView();
        populate();
        if (savedInstanceState != null) {
            Log.i(TAG, "isEnabledCommitButton " + savedInstanceState.getInt("COMMIT_BUTTON_ENABLED"));
            isEnabledCommitButton = savedInstanceState.getInt(COMMIT_BUTTON_STATE_KEY);
            mvCommitButton.setEnabled(isEnabledCommitButton == 1);
        }
        return mView;
    }

    private void mkViewsLinks(LayoutInflater inflater, ViewGroup container){
        if (DEBUG) Log.i(TAG, "mkViewsLinks "+ID);
        // You can not use the findViewById method the way you can in an Activity in a Fragment
        // So we get a reference to the view/layout_file that we used for this Fragment
        // That allows use to then reference the views by id in that file
        mView = inflater.inflate(R.layout.fragment_questions, container, false);
        mvQuestionText = (TextView) mView.findViewById(R.id.question);
        mvAnswersLayout = (LinearLayout) mView.findViewById(R.id.answers);
        mvCommitButton = (Button) mView.findViewById(R.id.commit_button);
        // You can not add onclick listener to a button in a fragment's xml
        // So we implement OnClickListener interface, check onClick() method
        mvCommitButton.setOnClickListener(this);
        mvRight = (TextView) mView.findViewById(R.id.right_counter);
        mvWrong = (TextView) mView.findViewById(R.id.wrong_counter);
        mvCurrentQuestionNum = (TextView) mView.findViewById(R.id.current_question_num);
        mvTotalQuestionNum = (TextView) mView.findViewById(R.id.total_question_num);
    }

    private void updateView(){

    }

    private void updateModel(){

    }

    // Map data from the current Question to the View elements
    public void populate() {
        if (DEBUG) Log.i(TAG, "populate "+ID);
        mvQuestionText.setText(mCurrentQuestion.getText());
        mvCurrentQuestionNum.setText("" + (mPosition+1));
        mvTotalQuestionNum.setText("/" + (mQuizData.size()-1));
        mvRight.setText("" + mQuizData.getTotalRightCounter());
        mvRight.setTextColor(Color.GREEN);
        mvWrong.setText(" " + mQuizData.getTotalWrongCounter());
        mvWrong.setTextColor(Color.RED);

        mvAnswersLayout.removeAllViewsInLayout();
        ArrayList<String> allAnswers = new ArrayList<>();
        allAnswers.addAll(mCurrentQuestion.getRightItems());
        allAnswers.addAll(mCurrentQuestion.getWrongItems());
        Collections.shuffle(allAnswers);

        mvCheckBoxes = new ArrayList<>();
        // create checkboxes dynamically
        for (int i = 0; i < allAnswers.size(); i++) {
            // Can not use "this" keyword for constructor here. Requires a Context and Fragment class does not inherit from Context
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(allAnswers.get(i));
            mvAnswersLayout.addView(checkBox);
            mvCheckBoxes.add(checkBox);
        }
    }


    public Boolean checkAnswers() {
        if (DEBUG) Log.i(TAG, "checkAnswers "+ID);
        Boolean goodJob = true;
        for (CheckBox cb : mvCheckBoxes) {
            // you can have multiple right answers
            String cbText = cb.getText().toString();
            if (cb.isChecked()) {
                if (!mCurrentQuestion.getRightItems().contains(cbText)) {
                    goodJob = false;
                    break;
                }
            } else if (mCurrentQuestion.getRightItems().contains(cbText)) {
                goodJob = false;
                break;
            }
        }
        Toast toast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if (goodJob) {
            toast.setText("Right");
            v.setTextColor(Color.GREEN);
            //mvCommitButton.setBackgroundColor(0xFF00FF00); // => green color
            mQuizData.incrementRightCounter(mPosition);
            mvRight.setText("" + mQuizData.getTotalRightCounter());
            mvCommitButton.setEnabled(false);
            isEnabledCommitButton = 0;
        }
        else {
            toast.setText("Wrong");
            //mvCommitButton.setBackgroundColor(Color.RED);
            v.setTextColor(Color.RED);
            mQuizData.incrementWrongCounter(mPosition);
            mvWrong.setText(" " + mQuizData.getTotalWrongCounter());

        }
        if (goodJob)
            mCallback.swipeNext();
        mCallback.updateFragments();
        toast.show();
        return goodJob;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(COMMIT_BUTTON_STATE_KEY, isEnabledCommitButton);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.commit_button):
                checkAnswers();
                break;
        }
    }
}
