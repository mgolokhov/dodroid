package doit.study.dodroid;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;



public class QuestionsFragment extends Fragment {


    private final String LOG_TAG = "NSA " + getClass().getName();
    private ArrayList<Question> mQuestions;
    // index for current question
    private Integer mIndex = 0;
    private ArrayList<CheckBox> mCheckBoxes;


    private LinearLayout linearLayout;
    private TextView tvQuestion;
    private LinearLayout linearLayoutAns;

    // references to navigation buttons
    private Button nextButton;
    private Button commitButton;
    private Button prevButton;


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

        Bundle args = getArguments();
        // Mmm, I got a parcel, unpack it...
        mQuestions = args.getParcelableArrayList("questions");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /* You can not use the findViewById method the way you can in an Activity in a Fragment
         * So we get a reference to the view/layout_file that we used for this Fragment
         * That allows use to then reference the views by id in that file
         */
        View view = inflater.inflate(R.layout.fragment_questions, container, false);

        linearLayout = (LinearLayout) view.findViewById(R.id.navigation);
        tvQuestion = (TextView) view.findViewById(R.id.question);
        linearLayoutAns = (LinearLayout) view.findViewById(R.id.answers);

        nextButton = (Button) view.findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextQuestion();
            }
        });

        commitButton = (Button) view.findViewById(R.id.commit_button);
        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswers();
            }
        });

        prevButton = (Button) view.findViewById(R.id.prev_button);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevQuestion();
            }
        });

        // In a fragment this needs to be called here instead of onCreate() to avoid null reference exceptions
        populate();

        return view;
    }



    // Map data from the current Question to the View elements
    public void populate(){
        Question currentQuestion = mQuestions.get(mIndex);
        linearLayout.setBackgroundColor(Color.WHITE);

        tvQuestion.setText(currentQuestion.question);

        linearLayoutAns.removeAllViewsInLayout();

        ArrayList<String> allAnswers = new ArrayList<>();
        allAnswers.addAll(currentQuestion.wrong);
        allAnswers.addAll(currentQuestion.right);
        Collections.shuffle(allAnswers);

        mCheckBoxes = new ArrayList<>();
        // create checkboxes dynamically
        for(int i=0;i<allAnswers.size(); i++) {

            // Can not use "this" keyword for constructor here. Requires a Context and Fragment class does not inherit from Context
            // getContext() method works but requires API level 23 so currently using getActivity() and it worked fine
            CheckBox checkBox = new CheckBox(getActivity());
            checkBox.setText(allAnswers.get(i));
            linearLayoutAns.addView(checkBox);
            mCheckBoxes.add(checkBox);
        }
    }



    public Boolean checkAnswers(){
        Log.i(LOG_TAG, "checkAnswers");
        Boolean goodJob = true;
        for(CheckBox cb: mCheckBoxes){
            Log.i(LOG_TAG, "" + cb.isChecked());
            // you can have multiple right answers
            if (cb.isChecked()) {
                if (!mQuestions.get(mIndex).right.contains(cb.getText().toString())){
                    goodJob = false;
                    break;
                }
            }
            else
            if (mQuestions.get(mIndex).right.contains(cb.getText().toString())){
                goodJob = false;
                break;
            }
        }
        Log.i(LOG_TAG, "Good job: " + goodJob);

        if (goodJob)
            linearLayout.setBackgroundColor(0xFF00FF00); // => green color
        else
            linearLayout.setBackgroundColor(Color.RED);

        return goodJob;
    }

    // callback registered in layout.xml
    public void nextQuestion(){
        mIndex = ++mIndex % mQuestions.size();
        populate();
    }

    // callback registered in layout.xml
    public void prevQuestion(View v){
        if (mIndex == 0)
            mIndex = mQuestions.size()-1;
        else
            --mIndex;
        populate();
    }


    public void prevQuestion(){
        if (mIndex == 0)
            mIndex = mQuestions.size()-1;
        else
            --mIndex;
        populate();
    }


    @Override
    public void onAttach(Context context) {

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }



}
