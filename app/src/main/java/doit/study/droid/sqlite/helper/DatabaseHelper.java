package doit.study.droid.sqlite.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import doit.study.droid.Question;
import doit.study.droid.R;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String SQLITE_SHAREDPREF = "doit.study.droid.sqlite.sharedpref";
    // Logcat tag
    @SuppressWarnings("unused")
    private final String TAG = "NSA " + getClass().getName();

    // Database Version
    private static final int DATABASE_VERSION = 6;
    private static final int DB_CONTENT_VERSION = 3;
    private static final String DB_CONTENT_VERSION_KEY = "doit.study.droid.sqlite.db_content_version_key";

    // Database Name
    private static final String DATABASE_NAME = "dodroid";

    // Table Names
    private static final String TABLE_QUESTION = "questions";
    private static final String TABLE_ANSWER = "answers";
    private static final String TABLE_TAG = "tags";
    private static final String TABLE_QUESTION_TAG = "questions_tags";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";

    // QUESTION Table - column names
    private static final String KEY_QUESTION = "question";
    private static final String KEY_DOC_REF = "doc_ref";
    private static final String KEY_IS_BINARY = "is_binary";
    private static final String KEY_ANSWER_IS_TRUE = "is_binary_true";

    // ANSWER Table - column names
    private static final String KEY_ANSWER = "answer";
    private static final String KEY_IS_RIGHT = "is_right";

    // TAGS Table - column names
    private static final String KEY_TAG_NAME = "tag_name";

    // QUESTION_TAG Table - column names
    private static final String KEY_QUESTION_ID = "question_id";
    private static final String KEY_TAG_ID = "tag_id";

    // Table Create Statements
    // Todo table create statement
    private static final String CREATE_TABLE_QUESTION = "CREATE TABLE "
            + TABLE_QUESTION + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_QUESTION + " TEXT,"
            + KEY_DOC_REF + " TEXT,"
            + KEY_IS_BINARY + " INTEGER DEFAULT 0,"
            + KEY_ANSWER_IS_TRUE + " INTEGER DEFAULT 0,"
            + KEY_CREATED_AT
            + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";

    // Tag table create statement
    private static final String CREATE_TABLE_TAG = "CREATE TABLE "
            + TABLE_TAG + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_TAG_NAME + " TEXT,"
            + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";

    // todo_tag table create statement
    private static final String CREATE_TABLE_QUESTION_TAG = "CREATE TABLE "
            + TABLE_QUESTION_TAG + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_QUESTION_ID + " INTEGER,"
            + KEY_TAG_ID + " INTEGER,"
            + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";

    private static final String CREATE_TABLE_ANSWER = "CREATE TABLE "
            + TABLE_ANSWER + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_ANSWER + " TEXT,"
            + KEY_IS_RIGHT + " INTEGER DEFAULT 0,"
            + KEY_QUESTION_ID + " INTEGER,"
            + KEY_CREATED_AT
            + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";

    private Context mContext;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_QUESTION);
        db.execSQL(CREATE_TABLE_ANSWER);
        db.execSQL(CREATE_TABLE_TAG);
        db.execSQL(CREATE_TABLE_QUESTION_TAG);
        // In our case schema and initial db content are tied
        insertFromFile(readFile(), db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANSWER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION_TAG);

        // create new tables
        onCreate(db);
    }


    // Leave for the future use
    @SuppressWarnings("unused")
    public void importFromFile(SQLiteDatabase db) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SQLITE_SHAREDPREF, Context.MODE_PRIVATE);
        int version = sharedPreferences.getInt(DB_CONTENT_VERSION_KEY, 0);
        if (version < DB_CONTENT_VERSION) {
            Log.i(TAG, "populate db from file");
            insertFromFile(readFile(), db);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(DB_CONTENT_VERSION_KEY, DB_CONTENT_VERSION);
            editor.commit();
        }

    }

    // Read raw data from resource file
    // Yeah, just read file, return contents
    private String readFile(){
        InputStream inputStream = mContext.getResources().openRawResource(
                R.raw.quiz);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream));

        StringBuffer buffer = new StringBuffer("");
        try {
            String line;
            while ((line = reader.readLine()) != null) {

                buffer.append(line);
            }

        } catch (IOException e) {
            Log.i(TAG, "IOException");
        }
        return buffer.toString();
    }

    // Parse and map json data to the Question object
    // Many questions => List of questions
    private List<QuestionInfo> parseTests(String data){
        List<QuestionInfo> questionInfos = new ArrayList<>();
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
                String docRef = currentQuestion.getString("docRef");
                QuestionInfo q = new QuestionInfo(id, questionText, wrongItems, rightItems , questionTags, docRef);
                questionInfos.add(q);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return questionInfos;
    }

    public int countQuestions() {
        return (int)DatabaseUtils.queryNumEntries(getReadableDatabase(), TABLE_QUESTION);
    }

    public List<Integer> getQuestionIds() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + KEY_ID + " FROM " + TABLE_QUESTION;
        Cursor c = db.rawQuery(query, null);
        List<Integer> result = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                result.add(c.getInt(0));
            } while (c.moveToNext());
        }
        return result;
    }

    public Question getQuestionById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String queryQuestion = "SELECT * FROM " + TABLE_QUESTION + " WHERE " + KEY_ID + " = " + id;
        Cursor c = db.rawQuery(queryQuestion, null);
        if (!c.moveToFirst()) throw new RuntimeException("no movetofirst!!");
        int qid = c.getInt(c.getColumnIndex(KEY_ID));
        String text = c.getString(c.getColumnIndex(KEY_QUESTION));
        String docRef = c.getString(c.getColumnIndex(KEY_DOC_REF));

        boolean isBinary = c.getInt(c.getColumnIndex(KEY_IS_BINARY)) == 1;

        String queryTag = "SELECT * FROM " + TABLE_QUESTION_TAG + " qt " +
                " JOIN " + TABLE_TAG + " t on qt." + KEY_TAG_ID + " = t." + KEY_ID +
                " WHERE qt." + KEY_QUESTION_ID + " = " + id;

        c = db.rawQuery(queryTag, null);
        ArrayList<String> tags = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                String tag = c.getString(c.getColumnIndex(KEY_TAG_NAME));
                tags.add(tag);
            } while (c.moveToNext());
        }

        ArrayList<String> wrongItems = new ArrayList<>();
        ArrayList<String> rightItems = new ArrayList<>();
        if (isBinary) {
            boolean isTrue = c.getInt(c.getColumnIndex(KEY_ANSWER_IS_TRUE)) == 1;
            rightItems.add(isTrue?"true":"false");
            wrongItems.add(isTrue?"false":"true");
        } else {
            String queryAnswer = "SELECT * FROM " + TABLE_ANSWER +
                    " WHERE " + KEY_QUESTION_ID + " = " + id;
            c = db.rawQuery(queryAnswer, null);
            if (c.moveToFirst()) {
                do {
                    String answer = c.getString(c.getColumnIndex(KEY_ANSWER));
                    boolean isRight = c.getInt(c.getColumnIndex(KEY_IS_RIGHT)) == 1;

                    if (isRight) rightItems.add(answer);
                    else wrongItems.add(answer);
                } while (c.moveToNext());
            }
        }
        Question q = new Question(qid, text, wrongItems, rightItems, tags, docRef);
        return q;
    }

    private static class QuestionInfo {
        private int mId;
        private String mText;
        private ArrayList<String> mWrongItems = new ArrayList<>();
        private ArrayList<String> mRightItems = new ArrayList<>();
        private ArrayList<String> mTags = new ArrayList<>();
        private String mDocRef;
        private boolean isBinary;
        private boolean answerIsTrue;

        public QuestionInfo(int mId, String mText, ArrayList<String> mWrongItems, ArrayList<String> mRightItems, ArrayList<String> mTags, String mDocRef) {
            this.mId = mId;
            this.mText = mText;
            this.mWrongItems = mWrongItems;
            this.mRightItems = mRightItems;
            this.mTags = mTags;
            this.mDocRef = mDocRef;
            Boolean binaryAnswer = binaryAnswer(mWrongItems, mRightItems);
            if (binaryAnswer != null) {
                this.isBinary = true;
                this.answerIsTrue = binaryAnswer;
            }
        }

        private static Boolean binaryAnswer(ArrayList<String> wrongItems, ArrayList<String> rightItems) {
            if (wrongItems.size() != 1 || rightItems.size() != 1) return null;
            String right = rightItems.get(0);
            String wrong = wrongItems.get(0);
            if ("true".equals(right) && "false".equals(wrong)) return true;
            if ("false".equals(right) && "true".equals(wrong)) return false;
            return null;
        }
    }

    private void insertFromFile(String fileContents, SQLiteDatabase db) {
        List<QuestionInfo> parsedQuestions = parseTests(fileContents);
        Map<String, Long> tag2id = insertTags(db, parsedQuestions);
        insertQuestions(db, parsedQuestions, tag2id);
    }

    private void insertQuestions(SQLiteDatabase db, List<QuestionInfo> parsedQuestions, Map<String, Long> tag2id) {
        try {
            SQLiteStatement insertQuestion = db.compileStatement(
                    "INSERT OR REPLACE INTO " + TABLE_QUESTION + " (" +
                            KEY_QUESTION + ", " +
                            KEY_DOC_REF + ", " +
                            KEY_IS_BINARY + ", " +
                            KEY_ANSWER_IS_TRUE + ") VALUES (?, ?, ?, ?)");
            SQLiteStatement insertAnswer = db.compileStatement(
                    "INSERT OR REPLACE INTO " + TABLE_ANSWER + " (" +
                            KEY_ANSWER + ", " +
                            KEY_QUESTION_ID + ", " +
                            KEY_IS_RIGHT + ") VALUES (?, ?, ?)");
            SQLiteStatement insertQuestionTag = db.compileStatement(
                    "INSERT OR REPLACE INTO " + TABLE_QUESTION_TAG + " (" +
                            KEY_QUESTION_ID + ", " +
                            KEY_TAG_ID + ") VALUES (?, ?)");
            db.beginTransaction();
            for (QuestionInfo q : parsedQuestions) {
                insertQuestion.bindString(1, q.mText);
                insertQuestion.bindString(2, q.mDocRef);
                if (q.isBinary) {
                    insertQuestion.bindLong(3, 1);
                    insertQuestion.bindLong(4, q.answerIsTrue ? 1 : 0);
                }
                long questionId = insertQuestion.executeInsert();

                if (!q.isBinary) {
                    for (String right: q.mRightItems) {
                        insertAnswer.bindString(1, right);
                        insertAnswer.bindLong(2, questionId);
                        insertAnswer.bindLong(3, 1);
                        insertAnswer.executeInsert();
                    }
                    for (String wrong: q.mWrongItems) {
                        insertAnswer.bindString(1, wrong);
                        insertAnswer.bindLong(2, questionId);
                        insertAnswer.bindLong(3, 0);
                        insertAnswer.executeInsert();
                    }
                }

                for (String tag : q.mTags) {
                    insertQuestionTag.bindLong(1, questionId);
                    long tagId = tag2id.get(tag);
                    insertQuestionTag.bindLong(2, tagId);
                    insertQuestionTag.executeInsert();
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private Map<String, Long> insertTags(SQLiteDatabase db, List<QuestionInfo> parsedQuestions) {
        Set<String> tags = new HashSet<>();
        for (QuestionInfo q : parsedQuestions) {
            tags.addAll(q.mTags);
        }
        Map<String, Long> tag2id = new HashMap<>();
        try {
            SQLiteStatement insert = db.compileStatement("INSERT OR REPLACE INTO " + TABLE_TAG + " (" + KEY_TAG_NAME + ") VALUES (?)");
            db.beginTransaction();
            for (String tag : tags) {
                insert.bindString(1, tag);
                long id = insert.executeInsert();
                tag2id.put(tag, id);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return tag2id;
    }
}
