package doit.study.droid.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import doit.study.droid.R;
import timber.log.Timber;

public class QuizDBHelper extends SQLiteOpenHelper {
    private static final boolean DEBUG = true;
    // Database Version
    private static final int DATABASE_VERSION = 37;
    //private static final int DB_CONTENT_VERSION = 28;
    private static final String DB_CONTENT_VERSION_KEY = "doit.study.droid.sqlite.db_content_version_key";

    public static final String SQLITE_SHAREDPREF = "doit.study.droid.sqlite.sharedpref";

    // Database Name
    private static final String DATABASE_NAME = "dodroid";

    private Context mContext;

    public QuizDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (DEBUG) Timber.d("create db");
        // Enable foreign key constraints
        db.execSQL("PRAGMA foreign_keys=ON;");

        String CREATE_TABLE_QUESTION = "CREATE TABLE "
                + Question.Table.NAME + "("
                + Question.Table._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Question.Table.TEXT + " TEXT,"
                + Question.Table.TRUE_OR_FALSE + " INTEGER,"
                + Question.Table.RIGHT_ANSWERS + " TEXT,"
                + Question.Table.WRONG_ANSWERS + " TEXT,"
                + Question.Table.DOC_LINK + " TEXT,"
                + Question.Table.RIGHT_ANS_CNT + " INTEGER DEFAULT 0,"
                + Question.Table.WRONG_ANS_CNT + " INTEGER DEFAULT 0,"
                + Question.Table.STATUS + " INTEGER DEFAULT 0,"
                + Question.Table.LAST_VIEWED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + Question.Table.STUDIED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP)";

        String CREATE_TABLE_TAG = "CREATE TABLE "
                + Tag.Table.NAME + "("
                + Tag.Table._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Tag.Table.TEXT + " TEXT,"
                + Tag.Table.SELECTED + " INTEGER DEFAULT 1)";

        String CREATE_TABLE_RELATION_QUESTION_TAG = "CREATE TABLE "
                + RelationTables.QuestionTag.NAME + "("
                + RelationTables.QuestionTag.QUESTION_ID + " INTEGER,"
                + RelationTables.QuestionTag.TAG_ID + " INTEGER)";

        db.execSQL(CREATE_TABLE_QUESTION);
        db.execSQL(CREATE_TABLE_TAG);
        db.execSQL(CREATE_TABLE_RELATION_QUESTION_TAG);

        insertFromFile(JsonParser.getQuestions(mContext.getResources().openRawResource(R.raw.quiz)), db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (DEBUG) Timber.d("update db");
        // Start timing how long it takes to update
        long startTime = System.nanoTime();
        // Drop all tables
        String dropIfExists = "DROP TABLE IF EXISTS ";
        db.execSQL(dropIfExists + Question.Table.NAME);
        db.execSQL(dropIfExists + Tag.Table.NAME);
        //FIXME: all stats will be lost
        db.execSQL(dropIfExists + RelationTables.QuestionTag.NAME);

        // create new tables
        onCreate(db);

        long stopTime = System.nanoTime();
        long elapsedTime = (stopTime - startTime) / 1000000; //ms
        if (DEBUG) Timber.d("update db in %d", elapsedTime);
    }


    // Leave for the future use
    @SuppressWarnings("unused")
//    private void createFromFile(SQLiteDatabase db) {
//        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SQLITE_SHAREDPREF, Context.MODE_PRIVATE);
//        int version = sharedPreferences.getInt(DB_CONTENT_VERSION_KEY, 0);
//        if (version < DB_CONTENT_VERSION) {
//            Log.i(TAG, "populate db from file");
//            insertFromFile(JsonParser.getQuestions(mContext.getResources().openRawResource(R.raw.quiz)), db);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putInt(DB_CONTENT_VERSION_KEY, DB_CONTENT_VERSION);
//            editor.commit();
//        }
//
//    }

    private void insertFromFile(List<JsonParser.ParsedQuestion> parsedQuestions, SQLiteDatabase db) {
        // TODO: do we need replace?
        SQLiteStatement insertQuestion = db.compileStatement("INSERT OR REPLACE INTO "
                        + Question.Table.NAME + "("
                        + TextUtils.join(", ", new String[]{
                        Question.Table.TEXT,
                        Question.Table.DOC_LINK,
                        Question.Table.RIGHT_ANSWERS,
                        Question.Table.WRONG_ANSWERS,
                        Question.Table.TRUE_OR_FALSE
                        })
                        + ") VALUES (?, ?, ?, ?, ?)"
        );

        SQLiteStatement insertTag = db.compileStatement("INSERT OR REPLACE INTO "
                        + Tag.Table.NAME + "("
                        + Tag.Table.TEXT + ") VALUES (?)"
        );

        SQLiteStatement insertRelationQuestionTag = db.compileStatement("INSERT OR REPLACE INTO "
                        + RelationTables.QuestionTag.NAME + "("
                        + RelationTables.QuestionTag.QUESTION_ID + ", "
                        + RelationTables.QuestionTag.TAG_ID + ") VALUES (?, ?)"
        );

        try {
            if (DEBUG) Timber.d("wanna to insert data");
            db.beginTransaction();
            Map<String, Long> tags = new HashMap<>();
            for (JsonParser.ParsedQuestion q : parsedQuestions) {
                insertQuestion.bindAllArgsAsStrings(new String[]{
                        q.mText,
                        q.mDocRef,
                        TextUtils.join("\n", q.mRightItems),
                        TextUtils.join("\n", q.mWrongItems),
                        String.valueOf(q.mTrueOrFalse ? 1 : 0)
                });
                long qid = insertQuestion.executeInsert();

                for (String t : q.mTags) {
                    if (tags.containsKey(t))
                        insertRelationQuestionTag.bindLong(2, tags.get(t));
                    else {
                        insertTag.bindString(1, t);
                        long tid = insertTag.executeInsert();
                        tags.put(t, tid);
                        insertRelationQuestionTag.bindLong(2, tid);
                    }
                    insertRelationQuestionTag.bindLong(1, qid);
                    insertRelationQuestionTag.executeInsert();
                }

            }
            db.setTransactionSuccessful();
            if (DEBUG) Timber.d("data was inserted");
        } finally {
            db.endTransaction();
        }
    }

}