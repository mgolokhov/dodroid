package doit.study.droid.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

public class QuizProvider extends ContentProvider {
    public static final String AUTHORITY = "doit.study.droid.data";
    public static final Uri BASE_URI =  Uri.parse("content://" + AUTHORITY);

    public static final String PATH_TAG = "tag";
    public static final Uri TAG_URI = BASE_URI.buildUpon().appendPath(PATH_TAG).build();
    public static final String TAG_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_TAG;
    public static final String TAG_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE+ "/" + AUTHORITY + "/" + PATH_TAG;

    public static final String PATH_QUESTION = "question";
    public static final Uri QUESTION_URI = BASE_URI.buildUpon().appendPath(PATH_QUESTION).build();
    public static final String QUESTION_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_QUESTION;
    public static final String QUESTION_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_QUESTION;

    public static final String PATH_STATISTICS = "statistics";
    public static final Uri STATISTICS_URI = BASE_URI.buildUpon().appendPath(PATH_STATISTICS).build();
    public static final String STATISTICS_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_STATISTICS;
    public static final String STATISTICS_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_STATISTICS;

    private QuizDBHelper mQuizDBHelper;

    static final int QUESTION_ITEM = 100;
    static final int QUESTION_DIR = 101;
    static final int RAND_QUESTION_DIR = 150;
    static final int TAG_ITEM = 200;
    static final int TAG_DIR = 201;
    static final int STATISTICS_ITEM = 300;
    static final int STATISTICS_DIR = 301;

    private static final UriMatcher sUriMatcher;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sUriMatcher.addURI(AUTHORITY, PATH_QUESTION, QUESTION_DIR);
        sUriMatcher.addURI(AUTHORITY, PATH_QUESTION + "/#", QUESTION_ITEM);
        sUriMatcher.addURI(AUTHORITY, PATH_QUESTION + "/rand/#", RAND_QUESTION_DIR);

        sUriMatcher.addURI(AUTHORITY, PATH_TAG, TAG_DIR);
        sUriMatcher.addURI(AUTHORITY, PATH_TAG + "/#", TAG_ITEM);

        sUriMatcher.addURI(AUTHORITY, PATH_STATISTICS, STATISTICS_DIR);
        sUriMatcher.addURI(AUTHORITY, PATH_STATISTICS + "/#", STATISTICS_ITEM);
    }

    @Override
    public boolean onCreate() {
        mQuizDBHelper = new QuizDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch(sUriMatcher.match(uri)) {
            case (QUESTION_ITEM):
                return QUESTION_ITEM_TYPE;
            case (QUESTION_DIR):
                return QUESTION_TYPE;
            case (TAG_ITEM):
                return TAG_ITEM_TYPE;
            case (TAG_DIR):
                return TAG_TYPE;
            case (STATISTICS_DIR):
                return STATISTICS_ITEM_TYPE;
            case (STATISTICS_ITEM):
                return STATISTICS_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch(sUriMatcher.match(uri)) {
            case (RAND_QUESTION_DIR):{
                cursor = getRandSelectedQuestions(uri);
                break;
            }
            case (QUESTION_ITEM):{
                break;
            }
            case (QUESTION_DIR):{
                break;
            }
            case (TAG_ITEM):{
                break;
            }
            case (TAG_DIR):{
                break;
            }
            case (STATISTICS_ITEM):{
                break;
            }
            case (STATISTICS_DIR):{
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return cursor;
    }


    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }


    private Cursor getRandSelectedQuestions(Uri uri) {
        int limit = getRandQuestionLimitFromUri(uri);
        SQLiteDatabase db = mQuizDBHelper.getReadableDatabase();
        String query = "SELECT q.ROWID as _id, * FROM " + Question.Table.NAME + " AS q " +
                " JOIN " + RelationTables.QuestionTag.NAME + " qtr ON q.ROWID = qtr." + RelationTables.QUESTION_ID +
                " JOIN " + Tag.Table.NAME + " AS t ON t.ROWID = qtr." + RelationTables.QuestionTag.TAG_ID +
                " WHERE t." + Tag.Table.SELECTED + " = 1" +
                " ORDER BY RANDOM() LIMIT " + limit;
        return db.rawQuery(query, null);
    }

    private int getRandQuestionLimitFromUri(Uri uri){
        // authority/question/limit/#
        return Integer.parseInt(uri.getPathSegments().get(3));
    }
}
