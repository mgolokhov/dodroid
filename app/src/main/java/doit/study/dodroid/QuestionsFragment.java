package doit.study.dodroid;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;


public class QuestionsFragment extends Fragment implements View.OnClickListener {
    private final String LOG_TAG = "NSA " + getClass().getName();
    // index for current question
    private Integer mIndex = 0;
    private Question mCurrentQuestion;
    private ArrayList<Question> mQuestions;
    private ArrayList<CheckBox> mCheckBoxes;
    private View mView;


    private LinearLayout mNavigationLayout;
    private TextView mQuestionText;
    private LinearLayout mAnswersLayout;


    // Provided stub factory method
    public static QuestionsFragment newInstance() {

        QuestionsFragment fragment = new QuestionsFragment();

        // add Bundle args if needed here before returning new instance of this class

        return fragment;
    }

    public QuestionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        GlobalData gd = (GlobalData) getActivity().getApplication();
        mQuestions = gd.getQuestions();
        mCurrentQuestion = mQuestions.get(mIndex);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /* You can not use the findViewById method the way you can in an Activity in a Fragment
         * So we get a reference to the view/layout_file that we used for this Fragment
         * That allows use to then reference the views by id in that file
         */
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_questions, container, false);

            mNavigationLayout = (LinearLayout) mView.findViewById(R.id.navigation);
            mQuestionText = (TextView) mView.findViewById(R.id.question);
            mAnswersLayout = (LinearLayout) mView.findViewById(R.id.answers);
            // You can not add onclick listener to a button in a fragment's xml
            // So we implement OnClickListener interface, check onClick() method
            mView.findViewById(R.id.commit_button).setOnClickListener(this);
            mView.findViewById(R.id.next_button).setOnClickListener(this);
            mView.findViewById(R.id.prev_button).setOnClickListener(this);

            // In a fragment this needs to be called here instead of onCreate() to avoid null reference exceptions
            populate();
        }

        return mView;
    }

    // Map data from the current Question to the View elements
    public void populate() {
        mNavigationLayout.setBackgroundColor(Color.WHITE);
        mQuestionText.setText(mCurrentQuestion.question);

        mAnswersLayout.removeAllViewsInLayout();

        ArrayList<String> allAnswers = new ArrayList<>();
        allAnswers.addAll(mCurrentQuestion.wrong);
        allAnswers.addAll(mCurrentQuestion.right);
        Collections.shuffle(allAnswers);

        mCheckBoxes = new ArrayList<>();
        // create checkboxes dynamically
        for (int i = 0; i < allAnswers.size(); i++) {

            // Can not use "this" keyword for constructor here. Requires a Context and Fragment class does not inherit from Context
            // getContext() method works but requires API level 23 so currently using getActivity() and it worked fine
            CheckBox checkBox = new CheckBox(getActivity());
            checkBox.setText(allAnswers.get(i));
            mAnswersLayout.addView(checkBox);
            mCheckBoxes.add(checkBox);
        }
    }


    public Boolean checkAnswers() {
        Boolean goodJob = true;
        for (CheckBox cb : mCheckBoxes) {
            // you can have multiple right answers
            if (cb.isChecked()) {
                if (!mCurrentQuestion.right.contains(cb.getText().toString())) {
                    goodJob = false;
                    break;
                }
            } else if (mCurrentQuestion.right.contains(cb.getText().toString())) {
                goodJob = false;
                break;
            }
        }
        Log.i(LOG_TAG, "Good job: " + goodJob);
        Toast toast;
        if (goodJob) {
            toast = Toast.makeText(getActivity(), "Right", Toast.LENGTH_LONG);
            mNavigationLayout.setBackgroundColor(0xFF00FF00); // => green color
        }
        else {
            toast = Toast.makeText(getActivity(), "Wrong", Toast.LENGTH_LONG);
            mNavigationLayout.setBackgroundColor(Color.RED);
        }
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        return goodJob;
    }

    public void nextQuestion() {
        mIndex = ++mIndex % mQuestions.size();
        mCurrentQuestion = mQuestions.get(mIndex);
        populate();
    }

    public void prevQuestion() {
        Log.i(LOG_TAG, "prevQuestion");
        if (mIndex == 0)
            mIndex = mQuestions.size() - 1;
        else
            --mIndex;
        mCurrentQuestion = mQuestions.get(mIndex);
        populate();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.commit_button):
                checkAnswers();
                break;
            case (R.id.prev_button):
                prevQuestion();
                break;
            case (R.id.next_button):
                nextQuestion();
                break;
        }
    }
}
