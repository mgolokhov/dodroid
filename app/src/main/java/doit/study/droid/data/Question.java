package doit.study.droid.data;

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
        public static final String TEXT = "atext";
        public static final String WRONG_ANSWERS = "wrong_answers";
        public static final String RIGHT_ANSWERS = "right_answers";
        public static final String TRUE_OR_FALSE = "true_or_false";
        public static final String DOC_LINK = "doc_link";
        public static final String WRONG_ANS_CNT = "wrong_ans_cnt";
        public static final String RIGHT_ANS_CNT = "right_ans_cnt";
        public static final String LAST_VIEWED_AT = "last_viewed_at";
        public static final String STUDIED_AT = "studied_at";
        // status
        // 0 - a new
        // 1 - added for learning
        // 2 - in progress (one or two right hits)
        // 3 - studied (three or more right hits)
        public static final String STATUS = "status";

        // fully qualified names
        public static final String FQ_ID = NAME + "." + _ID;
        public static final String FQ_TEXT = NAME + "." + TEXT;
        public static final String FQ_WRONG_ANSWERS = NAME + "." + WRONG_ANSWERS;
        public static final String FQ_RIGHT_ANSWERS = NAME + "." + RIGHT_ANSWERS;
        public static final String FQ_TRUE_OR_FALSE = NAME + "." + TRUE_OR_FALSE;
        public static final String FQ_DOC_LINK = NAME + "." + DOC_LINK;
        public static final String FQ_WRONG_ANS_CNT = NAME + "." + WRONG_ANS_CNT;
        public static final String FQ_RIGHT_ANS_CNT = NAME + "." + RIGHT_ANS_CNT;
        public static final String FQ_LAST_VIEWED_AT = NAME + "." + LAST_VIEWED_AT;
        public static final String FQ_STUDIED_AT = NAME + "." + STUDIED_AT;
        public static final String FQ_STATUS = NAME + "." + STATUS;
    }

    public enum Status {NEW, ADDED, IN_PROGRESS, STUDIED}

    private int mId;
    private int mWrongCounter;
    private int mRightCounter;
    private String mText;
    private List<String> mWrongAnswers;
    private List<String> mRightAnswers;
    private List<String> mTags;
    private String mDocRef;
    private Status mStatus;

    public Question(int id, String text,
                    List<String> wrongAnswers,
                    List<String> rightAnswers,
                    List<String> tags,
                    String docRef,
                    int wrongCounter,
                    int rightCounter,
                    int status
                    ){
        mId = id;
        mText = text;
        mWrongAnswers = wrongAnswers;
        mRightAnswers = rightAnswers;
        mTags = tags;
        mDocRef = docRef;
        mRightCounter = rightCounter;
        mWrongCounter = wrongCounter;
        mStatus = initStatus(status);
    }

    public static Question newInstance(Cursor c){
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

    @Override
    public String toString(){
        return "tags: " + mTags.toString();
    }

    private static List<String> splitItems(String s){
        if (s.equals(""))
            return new ArrayList<>();
        else
            return Arrays.asList(s.split("\n"));
    }

    private Status initStatus(int status){
        for (Status s: Status.values()) {
            if (s.ordinal() == status)
                return s;
        }
        return Status.NEW;
    }

    public String getDocRef () {
        return mDocRef;
    }

    public int getId() {
        return mId;
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

    public int getRightCounter() {
        return mRightCounter;
    }

    public int getWrongCounter() {
        return mWrongCounter;
    }

    public int incWrongCounter(){
        mStatus = Status.ADDED;
        mRightCounter = 0;
        return ++mWrongCounter;
    }

    public void incRightCounter(){
        if(++mRightCounter >= 3)
            mStatus = Status.STUDIED;
        else
            mStatus = Status.IN_PROGRESS;
    }

    public Status getStatus() {
        return mStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeInt(this.mWrongCounter);
        dest.writeInt(this.mRightCounter);
        dest.writeString(this.mText);
        dest.writeStringList(this.mWrongAnswers);
        dest.writeStringList(this.mRightAnswers);
        dest.writeStringList(this.mTags);
        dest.writeString(this.mDocRef);
        dest.writeInt(this.mStatus == null ? -1 : this.mStatus.ordinal());
    }

    protected Question(Parcel in) {
        this.mId = in.readInt();
        this.mWrongCounter = in.readInt();
        this.mRightCounter = in.readInt();
        this.mText = in.readString();
        this.mWrongAnswers = in.createStringArrayList();
        this.mRightAnswers = in.createStringArrayList();
        this.mTags = in.createStringArrayList();
        this.mDocRef = in.readString();
        int tmpMStatus = in.readInt();
        this.mStatus = tmpMStatus == -1 ? null : Status.values()[tmpMStatus];
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
