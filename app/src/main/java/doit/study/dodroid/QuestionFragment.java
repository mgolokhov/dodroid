package doit.study.dodroid;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;


public class QuestionFragment extends Fragment implements View.OnClickListener {
    private final String LOG_TAG = "NSA " + getClass().getName();
    private static final String QUESTION_KEY = "doit.study.dodroid.question_kye";
    private static final String TOTAL_NUM_KEY = "doit.study.dodroid.total_num_key";
    private static final String CUR_NUM_KEY = "doit.study.dodroid.cur_num_key";
    private Question mCurrentQuestion;
    private ArrayList<CheckBox> mCheckBoxes;
    private View mView;
    private Integer right_answer_counter = 0;
    private Integer wrong_answer_counter = 0;
    private Integer total_question_num = 0;
    private Integer current_question_num = 0;


    private Button mCommitButton;
    private TextView mQuestionText;
    private LinearLayout mAnswersLayout;


    // Factory method
    public static QuestionFragment newInstance(Question question, int total_num, int cur_num) {
        // add Bundle args if needed here before returning new instance of this class
        QuestionFragment fragment = new QuestionFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(QUESTION_KEY, question);
        bundle.putInt(TOTAL_NUM_KEY, total_num);
        bundle.putInt(CUR_NUM_KEY, cur_num);
        fragment.setArguments(bundle);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mCurrentQuestion = getArguments().getParcelable(QUESTION_KEY);
        total_question_num = getArguments().getInt(TOTAL_NUM_KEY);
        current_question_num = getArguments().getInt(CUR_NUM_KEY);
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

            mCommitButton = (Button) mView.findViewById(R.id.commit_button);
            mQuestionText = (TextView) mView.findViewById(R.id.question);
            mAnswersLayout = (LinearLayout) mView.findViewById(R.id.answers);
            // You can not add onclick listener to a button in a fragment's xml
            // So we implement OnClickListener interface, check onClick() method
            mView.findViewById(R.id.commit_button).setOnClickListener(this);

            // In a fragment this needs to be called here instead of onCreate() to avoid null reference exceptions
            populate();
        }

        return mView;
    }

    // Map data from the current Question to the View elements
    public void populate() {
        mCommitButton.setBackgroundResource(android.R.drawable.btn_default);
        mQuestionText.setText(mCurrentQuestion.question);
        TextView mCurrentQuestionNum = (TextView) mView.findViewById(R.id.current_question_num);
        mCurrentQuestionNum.setText(current_question_num.toString());
        TextView mTotalQuestionNum = (TextView) mView.findViewById(R.id.total_question_num);
        mTotalQuestionNum.setText("/"+total_question_num);
        TextView mRight = (TextView) mView.findViewById(R.id.right_counter);
        mRight.setText(right_answer_counter.toString());
        mRight.setTextColor(Color.GREEN);
        TextView mWrong = (TextView) mView.findViewById(R.id.wrong_counter);
        mWrong.setText(" "+wrong_answer_counter.toString());
        mWrong.setTextColor(Color.RED);

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
            toast = Toast.makeText(getActivity(), "Right", Toast.LENGTH_SHORT);
            mCommitButton.setBackgroundColor(0xFF00FF00); // => green color
            right_answer_counter++;
            TextView mRight = (TextView) mView.findViewById(R.id.right_counter);
            mRight.setText(right_answer_counter.toString());
        }
        else {
            toast = Toast.makeText(getActivity(), "Wrong", Toast.LENGTH_SHORT);
            mCommitButton.setBackgroundColor(Color.RED);
            wrong_answer_counter++;
            TextView mWrong = (TextView) mView.findViewById(R.id.wrong_counter);
            mWrong.setText("/"+wrong_answer_counter.toString());

        }
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        return goodJob;
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
