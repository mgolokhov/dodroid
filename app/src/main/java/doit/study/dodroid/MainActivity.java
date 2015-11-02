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


public class MainActivity extends Activity {
    // Shia LaBeouf - Just Do it! (Auto-tuned)
    private final String URL = "http://www.youtube.com/watch?v=gJscrxxl_Bg";
    private final String LOG_TAG = "NSA " + getClass().getName();
    private final Integer mTestFile = R.raw.tests;
    private ArrayList<Question> mQuestions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button needMotivationButton = (Button) findViewById(R.id.need_motivation);
        needMotivationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent motivationIntent = new Intent(Intent.ACTION_VIEW);
                motivationIntent.setData(Uri.parse(URL));
                startActivity(motivationIntent);
            }
        });
    }

    public void doitButtonHandler(View v){
        Log.i(LOG_TAG, "doit clicked");
        String s = readFile();
        parseTests(s);
        Intent intent = new Intent(MainActivity.this, QuestionActivity.class);
        intent.putParcelableArrayListExtra("questions", mQuestions);
        startActivity(intent);
    }

    // Read raw data from resource file
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
