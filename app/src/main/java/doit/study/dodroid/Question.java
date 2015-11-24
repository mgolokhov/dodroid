package doit.study.dodroid;

import java.util.ArrayList;


public class Question{
    private int mId;
    private String mText;
    private ArrayList<String> mWrongItems = new ArrayList<>();
    private ArrayList<String> mRightItems = new ArrayList<>();
    private ArrayList<String> mTags = new ArrayList<>();
    private int mRightCounter = 0;
    private int mWrongCounter = 0;

    public Question(int id, String text,
                    ArrayList<String> wrongItems,
                    ArrayList<String> rightItems,
                    ArrayList<String> tags){
        mId = id;
        mText = text;
        mWrongItems = wrongItems;
        mRightItems = rightItems;
        mTags = tags;
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

    public int getRightCounter() {
        return mRightCounter;
    }

    public int incrementRightCounter(){
        return ++mRightCounter;
    }

    public int getWrongCounter() {
        return mWrongCounter;
    }

    public int incrementWrongCounter(){
        return ++mWrongCounter;
    }
}
