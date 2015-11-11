package doit.study.dodroid;

import android.app.Application;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class GlobalData extends Application {
    private final String LOG_TAG = "NSA " + getClass().getName();
    // Link to the resource file, in our case it's a json file
    // I think we can say it some kind of descriptor, so it's an integer
    private final Integer mTestFile = R.raw.quiz;
    // Actually our quiz is a list of questions
    private ArrayList<Question> mQuestions = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        parseTests(readFile());
    }

    public Question getQuestion(Integer index){
        return mQuestions.get(index);
    }
    public ArrayList<Question> getQuestions(){
        return mQuestions;
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
                JSONArray tags = currentQuestion.optJSONArray("tags");
                if (tags != null)
                    for(int j=0; j<tags.length(); j++)
                        aQuestion.tags.add(tags.get(j).toString());
                else
                    aQuestion.tags.add("Other");
                mQuestions.add(aQuestion);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(LOG_TAG, mQuestions.toString());
    }
}
