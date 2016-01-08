package doit.study.droid.model;

import java.util.ArrayList;


public class Question{

    public static final class Table {
        public static final String NAME ="questions";
        public static final String TEXT = "text";
        public static final String WRONG_ANSWERS = "wrong_answers";
        public static final String RIGHT_ANSWERS = "right_answers";
        public static final String TRUE_OR_FALSE = "true_or_false";
        public static final String DOC_LINK = "doc_link";
    }

    private int mId;
    private String mText;
    private ArrayList<String> mWrongItems;
    private ArrayList<String> mRightItems;
    private ArrayList<String> mTags;
    private String mDocRef;

    public Question(int id, String text,
                    ArrayList<String> wrongItems,
                    ArrayList<String> rightItems,
                    ArrayList<String> tags,
                    String docRef
                    ){
        mId = id;
        mText = text;
        mWrongItems = wrongItems;
        mRightItems = rightItems;
        mTags = tags;
        mDocRef = docRef;
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

    public ArrayList<String> getWrongItems() {
        return mWrongItems;
    }

    public ArrayList<String> getRightItems() {
        return mRightItems;
    }

    public ArrayList<String> getTags() {
        return mTags;
    }

}
