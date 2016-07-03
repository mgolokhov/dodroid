package doit.study.droid.data;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

import timber.log.Timber;

public class QuizProvider extends ContentProvider {
    private static final boolean DEBUG = false;
    private boolean mIsBatchMode = false;

    public static final String AUTHORITY = "doit.study.droid";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_TAG = "tag";
    public static final Uri TAG_URI = BASE_URI.buildUpon().appendPath(PATH_TAG).build();
    public static final String TAG_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_TAG;
    public static final String TAG_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_TAG;

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

    private static final SQLiteQueryBuilder sQuizQueryBuilder;

    static {
        sQuizQueryBuilder = new SQLiteQueryBuilder();

        sQuizQueryBuilder.setTables(
                Question.Table.NAME +
                        " INNER JOIN " + RelationTables.QuestionTag.NAME +
                        " ON " + Question.Table.FQ_ID + " = " + RelationTables.QuestionTag.FQ_QUESTION_ID +
                        " INNER JOIN " + Tag.Table.NAME +
                        " ON " + RelationTables.QuestionTag.FQ_TAG_ID + " = " + Tag.Table.FQ_ID
        );
    }


    @Override
    public boolean onCreate() {
        mQuizDBHelper = new QuizDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case RAND_QUESTION_DIR:
                return QUESTION_TYPE;
            case QUESTION_DIR:
                return QUESTION_TYPE;
            case TAG_DIR:
                return TAG_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (DEBUG) Timber.d("query db: %s %s %s", uri, projection, selectionArgs);
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case RAND_QUESTION_DIR: {
                cursor = getRandSelectedQuestions(uri, projection);
                break;
            }
            case TAG_DIR: {
                cursor = getTags();
                break;
            }
            case QUESTION_DIR: {
                cursor = getQuestions(uri, projection);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mQuizDBHelper.getWritableDatabase();
        int mod = 0;
        String tableName = "";
        switch (sUriMatcher.match(uri)) {
            case TAG_DIR: {
                mod = db.update(Tag.Table.NAME, values, selection, selectionArgs);
                tableName = Tag.Table.NAME;
                break;
            }
            case QUESTION_DIR: {
                mod = db.update(Question.Table.NAME, values, selection, selectionArgs);
                tableName = Question.Table.NAME;
                break;
            }
            default: {
                break;
            }
        }
        if (mod != 0) {
            if (DEBUG) Timber.d("update db: %s %s %s", tableName, values, selection);
            if (!mIsBatchMode)
                getContext().getContentResolver().notifyChange(uri, null);
        }
        return mod;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }


    private Cursor getRandSelectedQuestions(Uri uri, String[] projection) {
        String limit = uri.getPathSegments().get(2);
        SQLiteDatabase db = mQuizDBHelper.getWritableDatabase();
        String selection = Tag.Table.NAME + "." + Tag.Table.SELECTED + " = 1";
        String sortOrder = "RANDOM()";
        String[] projection2 = Arrays.copyOf(projection, projection.length + 1);
        projection2[projection.length] = "group_concat( " + Tag.Table.FQ_TEXT + ", '\n' ) as tags2";
        if (DEBUG) Timber.d(sQuizQueryBuilder.buildQuery(projection2, selection, Question.Table.FQ_TEXT, null, sortOrder, limit));
        return sQuizQueryBuilder.query(db, projection2, selection, null, Question.Table.FQ_TEXT, null, sortOrder, limit);
    }


    private Cursor getTags() {
        // authority/tag/
        SQLiteDatabase db = mQuizDBHelper.getWritableDatabase();
        String tagTotalCounter = "COUNT(" + Tag.Table.FQ_TEXT + ") as " + Tag.Table.TOTAL_COUNTER;
        String tagStudiedCounter = "SUM(" + Question.Table.FQ_CONSECUTIVE_RIGHT_ANS_CNT + ">=" + Question.NUM_TO_CONSIDER_STUDIED + ") as " +
                Tag.Table.STUDIED_COUNTER;
        String[] projection = {Tag.Table.FQ_ID, Tag.Table.FQ_TEXT, tagTotalCounter, tagStudiedCounter, Tag.Table.FQ_SELECTION};
        return sQuizQueryBuilder.query(db, projection, null, null, Tag.Table.FQ_TEXT, null, null);
    }

    private Cursor getQuestions(Uri uri, String[] projection) {
        SQLiteDatabase db = mQuizDBHelper.getWritableDatabase();
//        String[] projection2;

//        if (projection != null)
//            projection2 = Arrays.copyOf(projection, projection.length + 1);
//        else
//            projection2 = new String[1];
//        projection2[projection.length] = "group_concat( " + Tag.Table.FQ_TEXT + ", '\n' ) as tags2";
        return sQuizQueryBuilder.query(db, projection, null, null, Question.Table.FQ_TEXT, null, null);
//        return db.query(Question.Table.NAME, projection, null, null, null, null, null);
    }


    @NonNull
    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        SQLiteDatabase db = mQuizDBHelper.getWritableDatabase();
        mIsBatchMode = true;
        db.beginTransaction();
        try {
            final ContentProviderResult[] res = super.applyBatch(operations);
            db.setTransactionSuccessful();
            getContext().getContentResolver().notifyChange(QuizProvider.BASE_URI, null);
            return res;
        } finally {
            mIsBatchMode = false;
            db.endTransaction();
        }
    }
}
