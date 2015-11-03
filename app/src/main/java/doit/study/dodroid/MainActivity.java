package doit.study.dodroid;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

// Entry point for the app.
// Because we set in manifest action=MAIN category=LAUNCHER
public class MainActivity extends Activity {
    // Shia LaBeouf - Just Do it! (Auto-tuned)
    private final String URL = "http://www.youtube.com/watch?v=gJscrxxl_Bg";
    // Define logging tag so it easier to filter messages
    private final String LOG_TAG = "NSA " + getClass().getName();
    // Link to the resource file, in our case it's a json file
    // I think we can say it some kind of descriptor, so it's an integer
    private final Integer mTestFile = R.raw.tests;
    // Actually our quiz is a list of questions
    private ArrayList<Question> mQuestions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button needMotivationButton = (Button) findViewById(R.id.need_motivation);
        // Define\register callback in the code
        needMotivationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create implicit intent
                Intent motivationIntent = new Intent(Intent.ACTION_VIEW);
                // Parser defines type of data
                motivationIntent.setData(Uri.parse(URL));
                // Action type + data type (extracted by uri parser)
                // should start youtube app or browser app
                startActivity(motivationIntent);
            }
        });
    }

    // Also is a callback, but registered in the layout.xml
    public void doitButtonHandler(View v){
        Log.i(LOG_TAG, "doit clicked");
        String s = readFile();
        parseTests(s);
        // Create explicit Intent
        Intent intent = new Intent(MainActivity.this, QuestionActivity.class);
        // Here is some fun
        // It was easy to send simple data Integers, Strings...
        // How about complex objects? ... serialization vs parcelable, check Question.java
        // So send big parcel to the activity QuestionActivity
        intent.putParcelableArrayListExtra("questions", mQuestions);
        startActivity(intent);
    }

    // Read raw data from resource file
    // Yeah, just read file, return contents
    private String readFile(){
        InputStream inputStream = getApplicationContext().getResources().openRawResource(
                mTestFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream));

        StringBuffer buffer = new StringBuffer("");
        try {
            String line;
            while ((line = reader.readLine()) != null) {

                buffer.append(line);
            }

        } catch (IOException e) {
            Log.i(LOG_TAG, "IOException");
        }
        Log.i(LOG_TAG, buffer.toString());
        return buffer.toString();
    }

    // Parse and map json data to the Question object
    // Many questions => List of questions
    private void parseTests(String data){
        try {
            JSONArray questions = new JSONArray(data);
            for(int i=0; i < questions.length(); i++) {
                Question aQuestion = new Question();
                JSONObject currentQuestion = questions.getJSONObject(i);
                aQuestion.question = currentQuestion.getString("question");
                JSONArray wrongAnswers = currentQuestion.getJSONArray("wrong");
                for(int j=0; j<wrongAnswers.length(); j++){
                    aQuestion.wrong.add(wrongAnswers.get(j).toString());
                }
                JSONArray rightAnswers = currentQuestion.getJSONArray("right");
                for(int j=0; j<rightAnswers.length(); j++){
                    aQuestion.right.add(rightAnswers.get(j).toString());
                }
                mQuestions.add(aQuestion);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(LOG_TAG, mQuestions.toString());
    }

}
