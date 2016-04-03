package doit.study.droid.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO: implement iterator
public class JsonParser {
    @SuppressWarnings("unused")
    private static final String TAG = "NSA JsonParser";
    private static final boolean DEBUG = true;

    public static List<ParsedQuestion> getQuestions(InputStream inputStream){
        return parseTests(readFile(inputStream));
    }

    private static String readFile(InputStream inputStream){
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream));
        StringBuilder buffer = new StringBuilder();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            inputStream.close();
        } catch (IOException e) {
            Log.i(TAG, "IOException");
        }
        return buffer.toString();
    }

    // Parse and map json data to the Question object
    // Many questions => List of questions
    private static List<ParsedQuestion> parseTests(String data){
        List<ParsedQuestion> parsedQuestions = null;
        try {
            JSONArray questions = new JSONArray(data);
            final int SIZE = questions.getJSONObject(0).getInt("quiz_size");
            parsedQuestions = new ArrayList<>(SIZE);
            for(int i=1; i < questions.length(); i++) {
                ParsedQuestion parsedQuestion = new ParsedQuestion();
                JSONObject currentQuestion = questions.getJSONObject(i);
                parsedQuestion.mTopicId = 0;//Integer.parseInt(currentQuestion.getString("topic_id"));
                parsedQuestion.mTestSetId = 0;//Integer.parseInt(currentQuestion.getString("test_set_id"));
                parsedQuestion.mQuestionId = 0;//Integer.parseInt(currentQuestion.getString("question_id"));
                parsedQuestion.mText = currentQuestion.getString("question");
                JSONArray wrongAnswers = currentQuestion.getJSONArray("wrong");

                for(int j=0; j<wrongAnswers.length(); j++){
                    parsedQuestion.mWrongItems.add(wrongAnswers.get(j).toString());
                }
                JSONArray rightAnswers = currentQuestion.getJSONArray("right");
                for(int j=0; j<rightAnswers.length(); j++){
                    parsedQuestion.mRightItems.add(rightAnswers.get(j).toString());
                }
                JSONArray tags = currentQuestion.optJSONArray("tags");
                if (tags != null)
                    for(int j=0; j<tags.length(); j++) {
                        String[] splitedTags = (tags.get(j).toString()).split("\n");
                        parsedQuestion.mTags.addAll(Arrays.asList(splitedTags));
                    }
                else
                    parsedQuestion.mTags.add("Other");
                parsedQuestion.mDocRef = currentQuestion.getString("docRef");
                parsedQuestions.add(parsedQuestion);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            //throw new RuntimeException(e);
        }
        if (DEBUG) Log.d(TAG, parsedQuestions != null ? parsedQuestions.toString() : "none");
        return parsedQuestions;
    }


}
