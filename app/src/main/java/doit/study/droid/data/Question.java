package doit.study.droid.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Question implements Parcelable {

    public static final class Table {

        public static final String NAME ="questions";
        public static final String _ID = "_id";
        public static final String TEXT = "question_text";
        public static final String WRONG_ANSWERS = "wrong_answers";
        public static final String RIGHT_ANSWERS = "right_answers";
        public static final String DOC_LINK = "doc_link";
        public static final String WRONG_ANS_CNT = "wrong_ans_cnt";
        public static final String RIGHT_ANS_CNT = "right_ans_cnt";
        public static final String CONSECUTIVE_RIGHT_ANS_CNT = "consecutive_right_ans_cnt";
        public static final String LAST_VIEWED_AT = "last_viewed_at";
        public static final String STUDIED_AT = "studied_at";
        public static final String QUESTION_TYPE = "question_type";

        // fully qualified names
        public static final String FQ_ID = mkFullyQualified(_ID);
        public static final String FQ_TEXT = mkFullyQualified(TEXT);
        public static final String FQ_WRONG_ANSWERS = mkFullyQualified(WRONG_ANSWERS);
        public static final String FQ_RIGHT_ANSWERS = mkFullyQualified(RIGHT_ANSWERS);
        public static final String FQ_DOC_LINK = mkFullyQualified(DOC_LINK);
        public static final String FQ_WRONG_ANS_CNT = mkFullyQualified(WRONG_ANS_CNT);
        public static final String FQ_RIGHT_ANS_CNT = mkFullyQualified(RIGHT_ANS_CNT);
        public static final String FQ_CONSECUTIVE_RIGHT_ANS_CNT = mkFullyQualified(CONSECUTIVE_RIGHT_ANS_CNT);
        public static final String FQ_LAST_VIEWED_AT = mkFullyQualified(LAST_VIEWED_AT);
        public static final String FQ_STUDIED_AT = mkFullyQualified(STUDIED_AT);
        public static final String FQ_QUESTION_TYPE = mkFullyQualified(QUESTION_TYPE);

        private static String mkFullyQualified(String s){
            return NAME + "." + s;
        }

        private Table() {}
    }


    // consecutive right shots when we consider question is studied
    public static final int NUM_TO_CONSIDER_STUDIED = 3;
    private int mId;
    private int mWrongAnsCnt;
    private int mRightAnsCnt;
    private int mConsecutiveRightCnt;
    private String mText;
    private List<String> mWrongAnswers;
    private List<String> mRightAnswers;
    private List<String> mTags;
    private String mDocRef;
    private int mQuestionType;

    public Question(){
        mRightAnswers = new ArrayList<>();
        mWrongAnswers = new ArrayList<>();
        mTags = new ArrayList<>();
    }

    public Question(int id, String text,
                    List<String> wrongAnswers,
                    List<String> rightAnswers,
                    List<String> tags,
                    String docRef,
                    int wrongAnsCnt,
                    int rightAnsCnt,
                    int consecutiveRightCnt,
                    int questionType
                    ){
        mId = id;
        mText = text;
        mWrongAnswers = wrongAnswers;
        mRightAnswers = rightAnswers;
        mTags = tags;
        mDocRef = docRef;
        mRightAnsCnt = rightAnsCnt;
        mWrongAnsCnt = wrongAnsCnt;
        mConsecutiveRightCnt = consecutiveRightCnt;
        mQuestionType = questionType;
    }

    public static Question newInstance(Cursor c){
        return new Question(c.getInt(c.getColumnIndex(Table._ID)),
                c.getString(c.getColumnIndex(Table.TEXT)),
                splitItems(c.getString(c.getColumnIndex(Table.WRONG_ANSWERS))),
                splitItems(c.getString(c.getColumnIndex(Table.RIGHT_ANSWERS))),
                splitItems(c.getString(c.getColumnIndex("tags2"))),
                c.getString(c.getColumnIndex(Table.DOC_LINK)),
                c.getInt(c.getColumnIndex(Table.WRONG_ANS_CNT)),
                c.getInt(c.getColumnIndex(Table.RIGHT_ANS_CNT)),
                c.getInt(c.getColumnIndex(Table.CONSECUTIVE_RIGHT_ANS_CNT)),
                c.getInt(c.getColumnIndex(Table.QUESTION_TYPE))
        );
    }


    public static ContentValues getContentValues(Question question) {
        ContentValues values = new ContentValues();
        values.put(Table._ID, String.valueOf(question.getId()));
        values.put(Table.CONSECUTIVE_RIGHT_ANS_CNT, question.getConsecutiveRightCnt());
        values.put(Table.RIGHT_ANS_CNT, question.getRightAnsCnt());
        values.put(Table.WRONG_ANS_CNT, question.getWrongAnsCnt());

        return values;
    }

    @Override
    public String toString(){
        String s = String.format("ID: %d, wrong: %s, wright: %s, tags: %s question: %s",
                mId,
                mWrongAnswers,
                mRightAnswers,
                mTags,
                mText);
        return s;
    }

    private static List<String> splitItems(String s){
        if ("".equals(s))
            return new ArrayList<>();
        else
            return Arrays.asList(s.split("\n"));
    }

    public String getDocRef () {
        return mDocRef;
    }

    public int getId() {
        return mId;
    }

    public void setText(String text) {
        mText = text;
    }

    public void setId(int id) {
        mId = id;
    }


    public void setDocRef(String docRef) {
        mDocRef = docRef;
    }

    public String getText() {
        return mText;
    }

    public List<String> getWrongAnswers() {
        return mWrongAnswers;
    }

    public List<String> getRightAnswers() {
        return mRightAnswers;
    }

    public List<String> getTags() {
        return mTags;
    }

    public int getRightAnsCnt() {
        return mRightAnsCnt;
    }

    public int getWrongAnsCnt() {
        return mWrongAnsCnt;
    }

    public int getConsecutiveRightCnt() {
        return mConsecutiveRightCnt;
    }

    public int getQuestionType(){
        return mQuestionType;
    }
    // not yet
    public void setQuestionType(int questionType){
        mQuestionType = questionType;
    }

    public int incWrongCounter(){
        mConsecutiveRightCnt = 0;
        return ++mWrongAnsCnt;
    }

    public void incRightCounter(){
        ++mRightAnsCnt;
        ++mConsecutiveRightCnt;
    }

    public boolean isStudied() {
        return mConsecutiveRightCnt >= NUM_TO_CONSIDER_STUDIED;
    }

    // Serialization interface

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeInt(this.mWrongAnsCnt);
        dest.writeInt(this.mRightAnsCnt);
        dest.writeString(this.mText);
        dest.writeStringList(this.mWrongAnswers);
        dest.writeStringList(this.mRightAnswers);
        dest.writeStringList(this.mTags);
        dest.writeString(this.mDocRef);
        dest.writeInt(this.mConsecutiveRightCnt);
        dest.writeInt(this.mQuestionType);
    }

    protected Question(Parcel in) {
        this.mId = in.readInt();
        this.mWrongAnsCnt = in.readInt();
        this.mRightAnsCnt = in.readInt();
        this.mText = in.readString();
        this.mWrongAnswers = in.createStringArrayList();
        this.mRightAnswers = in.createStringArrayList();
        this.mTags = in.createStringArrayList();
        this.mDocRef = in.readString();
        this.mConsecutiveRightCnt = in.readInt();
        this.mQuestionType = in.readInt();
    }

    public static final Parcelable.Creator<Question> CREATOR = new Parcelable.Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel source) {
            return new Question(source);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
}
