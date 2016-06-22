package doit.study.droid.data;

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

import timber.log.Timber;

// TODO: implement iterator
public class JsonParser {
    private static final boolean DEBUG = true;

    private JsonParser() {}

    public static List<Question> getQuestions(InputStream inputStream){
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
            Timber.e(e, "wtf");
        }
        return buffer.toString();
    }

    // Parse and map json data to the Question object
    // Many questions => List of questions
    private static List<Question> parseTests(String data){
        List<Question> parsedQuestions = null;
        try {
            JSONArray questions = new JSONArray(data);
            // first item indicates total number of questions
            final int SIZE = questions.getJSONObject(0).getInt("quiz_size");
            parsedQuestions = new ArrayList<>(SIZE);
            for(int i=1; i < questions.length(); i++) {
                Question question = new Question();
                JSONObject currentQuestion = questions.getJSONObject(i);
                question.setText(currentQuestion.getString("question"));

                JSONArray wrongAnswers = currentQuestion.getJSONArray("wrong");
                for(int j=0; j<wrongAnswers.length(); j++){
                    question.getWrongAnswers().add(wrongAnswers.get(j).toString());
                }

                JSONArray rightAnswers = currentQuestion.getJSONArray("right");
                for(int j=0; j<rightAnswers.length(); j++){
                    question.getRightAnswers().add(rightAnswers.get(j).toString());
                }

                JSONArray tags = currentQuestion.optJSONArray("tags");
                if (tags != null)
                    for(int j=0; j<tags.length(); j++) {
                        String[] splitTags = (tags.get(j).toString()).split("\n");
                        question.getTags().addAll(Arrays.asList(splitTags));
                    }
                else
                    question.getTags().add("Other");

                question.setDocRef(currentQuestion.getString("docRef"));
                question.setQuestionType(Integer.decode(currentQuestion.getString("questionType")));
                if (DEBUG) Timber.d(question.toString());
                parsedQuestions.add(question);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (DEBUG) Timber.d(parsedQuestions != null ? parsedQuestions.toString() : "none");
        return parsedQuestions;
    }
}
