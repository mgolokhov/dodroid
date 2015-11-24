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

/**
 * Use application singleton
*/
public class GlobalData extends Application {
    private final String LOG_TAG = "NSA " + getClass().getName();
    // Link to the resource file, in our case it's a json file
    // I think we can say it some kind of descriptor, so it's an integer
    private final Integer mTestFile = R.raw.quiz;
    private QuizData mQuizData = new QuizData();

    @Override
    public void onCreate() {
        super.onCreate();
        parseTests(readFile());
    }

    public QuizData getQuizData(){
        return mQuizData;
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
        return buffer.toString();
    }

    // Parse and map json data to the Question object
    // Many questions => List of questions
    private void parseTests(String data){
        try {
            JSONArray questions = new JSONArray(data);
            for(int i=0; i < questions.length(); i++) {
                JSONObject currentQuestion = questions.getJSONObject(i);
                int id = Integer.parseInt(currentQuestion.getString("ID"));
                String questionText= currentQuestion.getString("question");
                JSONArray wrongAnswers = currentQuestion.getJSONArray("wrong");
                ArrayList<String> wrongItems = new ArrayList<>();
                for(int j=0; j<wrongAnswers.length(); j++){
                    wrongItems.add(wrongAnswers.get(j).toString());
                }
                JSONArray rightAnswers = currentQuestion.getJSONArray("right");
                ArrayList<String> rightItems = new ArrayList<>();
                for(int j=0; j<rightAnswers.length(); j++){
                    rightItems.add(rightAnswers.get(j).toString());
                }
                JSONArray tags = currentQuestion.optJSONArray("tags");
                ArrayList<String> questionTags = new ArrayList<>();
                if (tags != null)
                    for(int j=0; j<tags.length(); j++)
                        questionTags.add(tags.get(j).toString());
                else
                    questionTags.add("Other");
                Question q = new Question(id, questionText, wrongItems, rightItems , questionTags);
                mQuizData.addQuestion(q);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
