package doit.study.droid.sqlite.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import doit.study.droid.R;
import doit.study.droid.model.Question;
import doit.study.droid.model.Statistics;
import doit.study.droid.model.TableRelationships;
import doit.study.droid.model.Tag;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Logcat tag
    @SuppressWarnings("unused")
    private final String TAG = "NSA " + getClass().getName();

    // Database Version
    private static final int DATABASE_VERSION = 18;
    private static final int DB_CONTENT_VERSION = 17;
    private static final String DB_CONTENT_VERSION_KEY = "doit.study.droid.sqlite.db_content_version_key";

    public static final String SQLITE_SHAREDPREF = "doit.study.droid.sqlite.sharedpref";

    // Database Name
    private static final String DATABASE_NAME = "dodroid";

    private Context mContext;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "create db");
        // Enable foreign key constraints
        db.execSQL("PRAGMA foreign_keys=ON;");

        String CREATE_TABLE_QUESTION = "CREATE TABLE "
                + Question.Table.NAME + "("
                + Question.Table.TEXT + " TEXT,"
                + Question.Table.TRUE_OR_FALSE + " INTEGER,"
                + Question.Table.RIGHT_ANSWERS + " TEXT,"
                + Question.Table.WRONG_ANSWERS + " TEXT,"
                + Question.Table.DOC_LINK + " TEXT,"
                + Statistics.Table.RIGHT_ANS_CNT + " INTEGER DEFAULT 0,"
                + Statistics.Table.WRONG_ANS_CNT + " INTEGER DEFAULT 0,"
                + Statistics.Table.STATUS + " INTEGER DEFAULT 0,"
                + Statistics.Table.LAST_VIEWED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + Statistics.Table.STUDIED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP)";

        String CREATE_TABLE_TAG = "CREATE TABLE "
                + Tag.Table.NAME + "("
                + Tag.Table.TEXT + " TEXT,"
                + Tag.Table.SELECTED + " INTEGER DEFAULT 1)";

        String CREATE_TABLE_RELATION_QUESTION_TAG = "CREATE TABLE "
                + TableRelationships.QuestionTag.NAME + "("
                + TableRelationships.QUESTION_ID + " INTEGER,"
                + TableRelationships.QuestionTag.TAG_ID + " INTEGER)";

        db.execSQL(CREATE_TABLE_QUESTION);
        db.execSQL(CREATE_TABLE_TAG);
        db.execSQL(CREATE_TABLE_RELATION_QUESTION_TAG);

        insertFromFile(JsonParser.getQuestions(mContext.getResources().openRawResource(R.raw.quiz)), db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "update db");
        // Drop all tables
        String dropIfExists = "DROP TABLE IF EXISTS ";
        db.execSQL(dropIfExists + Question.Table.NAME);
        db.execSQL(dropIfExists + Tag.Table.NAME);
        //FIXME: all stats will be lost
        db.execSQL(dropIfExists + TableRelationships.QuestionTag.NAME);

        // create new tables
        onCreate(db);
    }


    // Leave for the future use
    @SuppressWarnings("unused")
    private void createFromFile(SQLiteDatabase db) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SQLITE_SHAREDPREF, Context.MODE_PRIVATE);
        int version = sharedPreferences.getInt(DB_CONTENT_VERSION_KEY, 0);
        if (version < DB_CONTENT_VERSION) {
            Log.i(TAG, "populate db from file");
            insertFromFile(JsonParser.getQuestions(mContext.getResources().openRawResource(R.raw.quiz)), db);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(DB_CONTENT_VERSION_KEY, DB_CONTENT_VERSION);
            editor.commit();
        }

    }

    private void insertFromFile(List<ParsedQuestion> parsedQuestions, SQLiteDatabase db) {
        // TODO: do we need replace?
        SQLiteStatement insertQuestion = db.compileStatement("INSERT OR REPLACE INTO "
                        + Question.Table.NAME + "("
                        + TextUtils.join(", ", new String[]{
                        Question.Table.TEXT,
                        Question.Table.DOC_LINK,
                        Question.Table.RIGHT_ANSWERS,
                        Question.Table.WRONG_ANSWERS,
                        Question.Table.TRUE_OR_FALSE})
                        + ") VALUES (?, ?, ?, ?, ?)"
        );

        SQLiteStatement insertTag = db.compileStatement("INSERT OR REPLACE INTO "
                        + Tag.Table.NAME + "("
                        + Tag.Table.TEXT + ") VALUES (?)"
        );

        SQLiteStatement insertRelationQuestionTag = db.compileStatement("INSERT OR REPLACE INTO "
                        + TableRelationships.QuestionTag.NAME + "("
                        + TableRelationships.QUESTION_ID + ", "
                        + TableRelationships.QuestionTag.TAG_ID + ") VALUES (?, ?)"
        );

        try {
            Log.i(TAG, "wanna to insert data");
            db.beginTransaction();
            Map<String, Long> tags = new HashMap<>();
            for (ParsedQuestion q : parsedQuestions) {
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
            Log.i(TAG, "data is inserted");
        } finally {
            db.endTransaction();
        }
    }

    public List<Integer> getQuestionIds() {
        return getIds(Question.Table.NAME);
    }

    public List<Integer> getTagIds() {
        return getIds(Tag.Table.NAME);
    }

    private List<Integer> getIds(String tableName) {
        SQLiteDatabase db = getReadableDatabase();
        List<Integer> ids = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT ROWID as _id FROM " + tableName, null);
        while (c.moveToNext()) {
            Long id = c.getLong(c.getColumnIndex("_id"));
            ids.add(Integer.valueOf(id.intValue()));
        }
        return ids;
    }

    public List<Tag> getTagByIds(List<Integer> tagIds) {
        List<Tag> tags = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT ROWID, * FROM " + Tag.Table.NAME + " WHERE ROWID = ";
        for (Integer t : tagIds) {
            Cursor c = db.rawQuery(query + t, null);
            while (c.moveToNext()) {
                tags.add(mkTag(c));
            }
            c.close();
        }
        return tags;
    }

    public Tag getTagById(Integer tagId) {
        Tag tag = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT ROWID as _id, * FROM " + Tag.Table.NAME + " WHERE ROWID = " + tagId, null);
        if (c.moveToNext())
            tag = mkTag(c);
        c.close();
        return tag;
    }

    private Tag mkTag(Cursor c) {
        return new Tag(c.getInt(c.getColumnIndex("_id")),
                c.getString(c.getColumnIndex(Tag.Table.TEXT)),
                c.getInt(c.getColumnIndex(Tag.Table.SELECTED)) == 1);
    }
}