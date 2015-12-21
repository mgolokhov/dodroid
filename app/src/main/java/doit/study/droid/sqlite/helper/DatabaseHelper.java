package doit.study.droid.sqlite.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import doit.study.droid.Question;
import doit.study.droid.QuizData;
import doit.study.droid.Statistics;

public class DatabaseHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "quiz.db";
    private static final int DATABASE_VERSION = 4;
    @SuppressWarnings("unused")
    private final String TAG = "NSA " + getClass().getName();
    private HashMap<QuizData.Id, Question> mQuestions = new HashMap<>();
    private HashMap<QuizData.Id, Statistics> mStatistics = new HashMap<>();
    Cursor mCursor;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }

    public Question getQuestionById(QuizData.Id id){
        if (mQuestions != null && mQuestions.containsKey(id))
            return mQuestions.get(id);
        else
            getQuestionsById(id);
        return mQuestions.get(id);
    }

    // FIXME: naming sucks
    public Statistics getStatById(QuizData.Id id){
        if (mStatistics != null && mStatistics.containsKey(id))
            return mStatistics.get(id);
        else
            getStatisticsById(id);
        return mStatistics.get(id);
    }

    public HashMap<QuizData.Id, Question> getQuestionsById (QuizData.Id id){
        getById(Question.Table.NAME, id);
        cursorToQuestions();
        mCursor.close();
        return mQuestions;
    }

    public HashMap<QuizData.Id, Statistics> getStatisticsById(QuizData.Id id){
        getById(Statistics.Table.NAME, id);
        cursorToStatistics();
        mCursor.close();
        return mStatistics;
    }

    private void getById(String tableName, QuizData.Id id){
        SQLiteDatabase db = getReadableDatabase();
        if (id.topic > 0 && id.test > 0 && id.question > 0)
            mCursor = db.rawQuery("SELECT * FROM "+ tableName + " WHERE TOPIC_NUM_ID=? AND TEST_NUM_ID=? AND QUESTION_NUM_ID=?",
                    new String[] {String.valueOf(id.topic), String.valueOf(id.test), String.valueOf(id.question)});

        else if (id.topic > 0 && id.test > 0 && id.question == 0)
            mCursor = db.rawQuery("SELECT * FROM "+ tableName + " WHERE TOPIC_NUM_ID=? AND TEST_NUM_ID=? ",
                    new String[] {String.valueOf(id.topic), String.valueOf(id.test)});

        else if (id.topic > 0 && id.test == 0 && id.question == 0)
            mCursor = db.rawQuery("SELECT * FROM "+ tableName + " WHERE TOPIC_NUM_ID=?",
                    new String[] {String.valueOf(id.topic)});

        else if (id.topic == 0 && id.test == 0 && id.question == 0)
            mCursor = db.rawQuery("SELECT * FROM "+ tableName, null);
        else {
            Log.e(TAG, "Wrong id="+id);
            throw new IllegalArgumentException();
        }
    }

    public HashMap<QuizData.Id, Question> getQuestionsByLearningStatus (boolean onLearningFlag){
        SQLiteDatabase db = getReadableDatabase();
        String onLearning = String.valueOf(onLearningFlag ? 1 : 0);
        // FIXME: rawQuery(String sql, String[] selectionArgs) doesn't work
        String q = "SELECT q.topic_num_id, q.test_num_id, q.question_num_id," +
                " q.question_text, q.right_items, q.wrong_items, q.tags, q.doc_reference"
                + " FROM questions as q, statistics as s"
                + " where q.topic_num_id = s.topic_num_id"
                + " and q.test_num_id = s.test_num_id"
                + " and q.question_num_id = s.question_num_id"
                + " and on_learning =" + onLearning;
        mCursor = db.rawQuery(q, null);
        cursorToQuestions();
        mCursor.close();
        return mQuestions;
    }

    private void cursorToQuestions(){
        while(mCursor.moveToNext()){
            String questionText = mCursor.getString(mCursor.getColumnIndex(Question.Table.QUESTION_TEXT));
            String tags = mCursor.getString(mCursor.getColumnIndex(Question.Table.TAGS));
            String docRefs = mCursor.getString(mCursor.getColumnIndex(Question.Table.DOC_REFERENCE));
            String wrongStr = mCursor.getString(mCursor.getColumnIndex(Question.Table.WRONG_ITEMS));
            ArrayList<String> wrong;
            if (wrongStr.equals(""))
                wrong = new ArrayList<>();
            else
                wrong = new ArrayList<>(Arrays.asList(wrongStr.split("\n")));
            String rightStr = mCursor.getString(mCursor.getColumnIndex(Question.Table.RIGHT_ITEMS));
            ArrayList<String> right;
            if (rightStr.equals(""))
                right = new ArrayList<>();
            else
                right = new ArrayList<>(Arrays.asList(rightStr.split("\n")));

            QuizData.Id id = cursorToId();
            Question question = new Question(id, questionText, wrong, right, tags, docRefs);
            mQuestions.put(id, question);
        }
    }

    private void cursorToStatistics(){
        while(mCursor.moveToNext()){
            int wrongCounter = mCursor.getInt(mCursor.getColumnIndex(Statistics.Table.WRONG_COUNTER));
            int rightCounter = mCursor.getInt(mCursor.getColumnIndex(Statistics.Table.RIGHT_COUNTER));
            int onLearning = mCursor.getInt(mCursor.getColumnIndex(Statistics.Table.ON_LEARNING));
            QuizData.Id id = cursorToId();
            Statistics stat = new Statistics(id, wrongCounter, rightCounter, onLearning);
            mStatistics.put(id, stat);
        }
    }

    private QuizData.Id cursorToId(){
        int topic = mCursor.getInt(mCursor.getColumnIndex(QuizData.Table.TOPIC_NUM_ID));
        int test_num = mCursor.getInt(mCursor.getColumnIndex(QuizData.Table.TEST_NUM_ID));
        int question_num =  mCursor.getInt(mCursor.getColumnIndex(QuizData.Table.QUESTION_NUM_ID));
        return new QuizData.Id(topic, test_num, question_num);
    }
}