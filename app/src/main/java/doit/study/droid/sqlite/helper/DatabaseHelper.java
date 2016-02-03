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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import doit.study.droid.R;
import doit.study.droid.model.Question;
import doit.study.droid.model.RelationTables;
import doit.study.droid.model.Tag;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final boolean DEBUG = true;
    // Logcat tag
    @SuppressWarnings("unused")
    private final String TAG = "NSA " + getClass().getName();

    // Database Version
    private static final int DATABASE_VERSION = 23;
    private static final int DB_CONTENT_VERSION = 23;
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
                + Question.Table.RIGHT_ANS_CNT + " INTEGER DEFAULT 0,"
                + Question.Table.WRONG_ANS_CNT + " INTEGER DEFAULT 0,"
                + Question.Table.STATUS + " INTEGER DEFAULT 0,"
                + Question.Table.LAST_VIEWED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + Question.Table.STUDIED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP)";

        String CREATE_TABLE_TAG = "CREATE TABLE "
                + Tag.Table.NAME + "("
                + Tag.Table.TEXT + " TEXT,"
                + Tag.Table.SELECTED + " INTEGER DEFAULT 1)";

        String CREATE_TABLE_RELATION_QUESTION_TAG = "CREATE TABLE "
                + RelationTables.QuestionTag.NAME + "("
                + RelationTables.QUESTION_ID + " INTEGER,"
                + RelationTables.QuestionTag.TAG_ID + " INTEGER)";

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
        db.execSQL(dropIfExists + RelationTables.QuestionTag.NAME);

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
                        + RelationTables.QuestionTag.NAME + "("
                        + RelationTables.QUESTION_ID + ", "
                        + RelationTables.QuestionTag.TAG_ID + ") VALUES (?, ?)"
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
        db.beginTransaction();
        for (Integer t : tagIds) {
            Cursor c = db.rawQuery(mkTagQuery(t), null);
            while (c.moveToNext()) {
                tags.add(mkTag(c));
            }
            c.close();
        }
        db.endTransaction();
        return tags;
    }

    public List<Tag> getTags(){
        List<Tag> tags = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query = mkTagQuery(null);
        Cursor c = db.rawQuery(query, null);
        while(c.moveToNext()){
            tags.add(mkTag(c));
        }
        c.close();
        return tags;
    }

    public Tag getTagById(Integer tagId) {
        Tag tag = null;
        SQLiteDatabase db = getReadableDatabase();
        String query = mkTagQuery(tagId);
        Cursor c = db.rawQuery(query, null);
        if (c.moveToNext())
            tag = mkTag(c);
        c.close();
        return tag;
    }

    public void setTagSelection(Tag tag){
        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE " + Tag.Table.NAME +
                " SET " + Tag.Table.SELECTED + " = ? " +
                " WHERE ROWID = ? ";
        Log.i(TAG, " "+tag.getName()+ " " +tag.getSelectionStatus());
        db.execSQL(query, new String[]{(tag.getSelectionStatus() == true) ? "1" : "0", tag.getId().toString()});
    }

    private String mkTagQuery(Integer id){
        String query = String.format(
                "SELECT t.ROWID as _id, t.%s, COUNT(t.%s) AS counter, SUM(q.%s = 3) as studied, t.%s",
                Tag.Table.TEXT, Tag.Table.TEXT, Question.Table.STATUS, Tag.Table.SELECTED) +
                " FROM " + Question.Table.NAME + " AS q " +
                " JOIN " + RelationTables.QuestionTag.NAME + " AS qtr ON q.ROWID = qtr." + RelationTables.QUESTION_ID +
                " JOIN " + Tag.Table.NAME + " AS t on t.rowid = qtr." + RelationTables.QuestionTag.TAG_ID +
                // do we need filter by id?
                ((id != null) ? (" WHERE t.ROWID = " + id) : "" ) +
                " GROUP BY t." + Tag.Table.TEXT;
        return query;
    }

    private Tag mkTag(Cursor c) {
        return new Tag(c.getInt(c.getColumnIndex("_id")),
                c.getString(c.getColumnIndex(Tag.Table.TEXT)),
                c.getInt(c.getColumnIndex(Tag.Table.SELECTED)) == 1,
                c.getInt(c.getColumnIndex("counter")),
                c.getInt(c.getColumnIndex("studied")));
    }

    public List<Integer> getRandSelectedQuestionIds(Integer limit){
        Log.i(TAG, "getRandSelectedQuestionIds with limit "+limit);
        List<Integer> ids = new ArrayList<>();
        if (limit == null)
            limit = 10;
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT q.ROWID from " + Question.Table.NAME + " AS q " +
                " JOIN " + RelationTables.QuestionTag.NAME + " qtr ON q.ROWID = qtr." + RelationTables.QUESTION_ID +
                " JOIN " + Tag.Table.NAME + " AS t ON t.ROWID = qtr." + RelationTables.QuestionTag.TAG_ID +
                " WHERE t." + Tag.Table.SELECTED + " = 1" +
                " ORDER BY RANDOM() LIMIT " + limit;
        Cursor c = db.rawQuery(query, null);
        while(c.moveToNext()){
            ids.add(c.getInt(0));
        }
        c.close();
        return ids;
    }

    public Question getQuestionById(int questionId) {
        Log.i(TAG, "get question by id "+questionId);
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT q.ROWID as _id, * FROM " + Question.Table.NAME + " AS q " +
                " JOIN " + RelationTables.QuestionTag.NAME + " AS qtr ON q.ROWID = qtr." + RelationTables.QUESTION_ID +
                " JOIN " + Tag.Table.NAME + " AS t on t.rowid = qtr." + RelationTables.QuestionTag.TAG_ID +
                " WHERE q.ROWID = " + questionId;

        Cursor c = db.rawQuery(query, null);
        Question q = null;
        if (c.moveToNext()){
            q =  mkQuestion(c);
        }
        c.close();
        return q;
    }

    private Question mkQuestion(Cursor c) {
        return new Question(c.getInt(c.getColumnIndex("_id")),
                c.getString(c.getColumnIndex(Question.Table.TEXT)),
                splitItems(c.getString(c.getColumnIndex(Question.Table.WRONG_ANSWERS))),
                splitItems(c.getString(c.getColumnIndex(Question.Table.RIGHT_ANSWERS))),
                splitItems(c.getString(c.getColumnIndex(Tag.Table.TEXT))),
                c.getString(c.getColumnIndex(Question.Table.DOC_LINK)),
                c.getInt(c.getColumnIndex(Question.Table.WRONG_ANS_CNT)),
                c.getInt(c.getColumnIndex(Question.Table.RIGHT_ANS_CNT)),
                c.getInt(c.getColumnIndex(Question.Table.STATUS))
                );
    }

    private List<String> splitItems(String s){
        if (s.equals(""))
            return new ArrayList<>();
        else
            return Arrays.asList(s.split("\n"));
    }

    public void setQuestion(Question q) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE "+ Question.Table.NAME +
                " SET " + Question.Table.WRONG_ANS_CNT + " = " + q.getWrongCounter() +
                ", " + Question.Table.RIGHT_ANS_CNT + " = " + q.getRightCounter() +
                ", " + Question.Table.STATUS + " = " + q.getStatus().ordinal() +
                " WHERE ROWID = " + q.getId();
        db.execSQL(query);
    }
}