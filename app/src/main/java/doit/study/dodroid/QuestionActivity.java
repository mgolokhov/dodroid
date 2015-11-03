package doit.study.dodroid;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;


public class QuestionActivity extends Activity {
    private final String LOG_TAG = "NSA " + getClass().getName();
    private ArrayList<Question> mQuestions;
    // index for current question
    private Integer mIndex = 0;
    private ArrayList<CheckBox> mCheckBoxes;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_activity);
        // Mmm, I got a parcel, unpack it...
        mQuestions = getIntent().getParcelableArrayListExtra("questions");
        populate();
    }

    // Map data from the current Question to the View elements
    public void populate(){
        Question currentQuestion = mQuestions.get(mIndex);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.navigation);
        linearLayout.setBackgroundColor(Color.WHITE);

        TextView tvQuestion = (TextView) findViewById(R.id.question);
        tvQuestion.setText(currentQuestion.question);

        LinearLayout linearLayoutAns = (LinearLayout) findViewById(R.id.answers);
        linearLayoutAns.removeAllViewsInLayout();

        ArrayList<String> allAnswers = new ArrayList<>();
        allAnswers.addAll(currentQuestion.wrong);
        allAnswers.addAll(currentQuestion.right);
        Collections.shuffle(allAnswers);

        mCheckBoxes = new ArrayList<>();
        // create checkboxes dynamically
        for(int i=0;i<allAnswers.size(); i++) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(allAnswers.get(i));
            linearLayoutAns.addView(checkBox);
            mCheckBoxes.add(checkBox);
        }
    }

    public Boolean checkAnswers(View v){
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
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.navigation);

        if (goodJob)
            linearLayout.setBackgroundColor(0xFF00FF00); // => green color
        else
            linearLayout.setBackgroundColor(Color.RED);

        return goodJob;
    }

    // callback registered in layout.xml
    public void nextQuestion(View v){
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

}
