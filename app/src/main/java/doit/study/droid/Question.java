package doit.study.droid;


import java.util.ArrayList;

public class Question{
    // Database constants
    public static class Table {
        /** for primary key check
         *  @see doit.study.droid.QuizData.Table
         */
        public static final String NAME = "questions";
        public static final String QUESTION_TEXT = "question_text";
        public static final String WRONG_ITEMS = "wrong_items";
        public static final String RIGHT_ITEMS = "right_items";
        public static final String TAGS = "tags";
        public static final String DOC_REFERENCE = "doc_reference";
    }

    // Compound key, ready for serialization, check below
    private QuizData.Id mId;
    private String mText;
    private ArrayList<String> mWrongItems;
    private ArrayList<String> mRightItems;
    private String mTags;
    private String mDocRef;

    public Question(QuizData.Id id, String text,
                    ArrayList<String> wrongItems,
                    ArrayList<String> rightItems,
                    String tags,
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

    public QuizData.Id getId() {
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

    public String getTags() {
        return mTags;
    }

}
