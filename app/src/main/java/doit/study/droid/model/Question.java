package doit.study.droid.model;

import java.util.List;


public class Question{

    public static final class Table {
        public static final String NAME ="questions";
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

    public void incWrongCounter(){
        mStatus = Status.ADDED;
        mRightCounter = 0;
        mWrongCounter++;
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
}
