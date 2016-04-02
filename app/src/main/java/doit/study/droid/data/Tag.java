package doit.study.droid.data;

import android.database.Cursor;
import android.os.Bundle;

public class Tag {

    public static final class Table {

        public static final String NAME = "tags";
        public static final String _ID = "_id";
        public static final String TEXT = "text";
        public static final String SELECTED = "selected";
        // fully qualified names
        public static final String FQ_ID = NAME + "." + _ID;
        public static final String FQ_TEXT = NAME + "." + TEXT;
        public static final String FQ_SELECTION = NAME + "." + SELECTED;
        // helper constants
        public static final String QTY_WHEN_STUDIED = "3";
        public static final String TOTAL_COUNTER = "tagTotalCounter";
        public static final String STUDIED_COUNTER = "tagStudiedCounter";
    }

    public Integer getId() {
        return mId;
    }

    private final Integer mId;
    private final String mName;
    private final Integer mQuestionsCounter;
    private final Integer mQuestionsStudied;
    private boolean mSelected;

    public static Tag newInstance(Cursor c) {
        return new Tag(c.getInt(c.getColumnIndex("_id")),
                c.getString(c.getColumnIndex(Tag.Table.TEXT)),
                c.getInt(c.getColumnIndex(Tag.Table.SELECTED)) == 1,
                c.getInt(c.getColumnIndex(Tag.Table.TOTAL_COUNTER)),
                c.getInt(c.getColumnIndex(Table.STUDIED_COUNTER)));
    }


    public Tag (Integer id, String name, boolean selected, Integer questionsCounter, Integer questionsStudied) {
        mId = id;
        mName = name;
        mSelected = selected;
        mQuestionsCounter = questionsCounter;
        mQuestionsStudied = questionsStudied;
    }

    @Override
    public String toString(){
        return String.format("%d# Name: %s, selection: %s", hashCode(), mName, mSelected);
    }

    public String getName() {
        return mName;
    }

    public Integer getQuestionsStudied() {
        return mQuestionsStudied;
    }

    public Integer getQuestionsCounter() {
        return mQuestionsCounter;
    }

    public void setChecked(boolean checked){
        mSelected = checked;
    }

    public boolean getSelectionStatus(){
        return mSelected;
    }
}
