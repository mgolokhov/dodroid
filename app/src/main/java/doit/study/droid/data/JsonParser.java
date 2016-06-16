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
            Timber.e(e, "wtf");
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
                        String[] splitTags = (tags.get(j).toString()).split("\n");
                        parsedQuestion.mTags.addAll(Arrays.asList(splitTags));
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
        if (DEBUG) Timber.d(parsedQuestions != null ? parsedQuestions.toString() : "none");
        return parsedQuestions;
    }


    public static class ParsedQuestion {
        private int mTopicId;
        private int mTestSetId;
        private int mQuestionId;
        private String mText;
        private List<String> mWrongItems = new ArrayList<>();
        private List<String> mRightItems = new ArrayList<>();
        private List<String> mTags = new ArrayList<>();
        private String mDocRef;
        private boolean mTrueOrFalse;

        public int getmTopicId() {
            return mTopicId;
        }

        public void setmTopicId(int mTopicId) {
            this.mTopicId = mTopicId;
        }

        public int getmTestSetId() {
            return mTestSetId;
        }

        public void setmTestSetId(int mTestSetId) {
            this.mTestSetId = mTestSetId;
        }

        public int getmQuestionId() {
            return mQuestionId;
        }

        public void setmQuestionId(int mQuestionId) {
            this.mQuestionId = mQuestionId;
        }

        public String getmText() {
            return mText;
        }

        public void setmText(String mText) {
            this.mText = mText;
        }

        public List<String> getmWrongItems() {
            return mWrongItems;
        }

        public void setmWrongItems(List<String> mWrongItems) {
            this.mWrongItems = mWrongItems;
        }

        public List<String> getmRightItems() {
            return mRightItems;
        }

        public void setmRightItems(List<String> mRightItems) {
            this.mRightItems = mRightItems;
        }

        public List<String> getmTags() {
            return mTags;
        }

        public void setmTags(List<String> mTags) {
            this.mTags = mTags;
        }

        public String getmDocRef() {
            return mDocRef;
        }

        public void setmDocRef(String mDocRef) {
            this.mDocRef = mDocRef;
        }

        public boolean ismTrueOrFalse() {
            return mTrueOrFalse;
        }

        public void setmTrueOrFalse(boolean mTrueOrFalse) {
            this.mTrueOrFalse = mTrueOrFalse;
        }

        @Override
        public String toString() {
            return mText;
        }
    }
}
