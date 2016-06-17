package doit.study.droid.data;

import android.database.Cursor;

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

        private Table() {}
    }

    private final Integer mId;
    private final String mName;
    private final Integer mQuestionsCounter;
    private final Integer mQuestionsStudied;
    private boolean mSelected;

    public Tag (Integer id, String name, boolean selected, Integer questionsCounter, Integer questionsStudied) {
        mId = id;
        mName = name;
        mSelected = selected;
        mQuestionsCounter = questionsCounter;
        mQuestionsStudied = questionsStudied;
    }

    public static Tag newInstance(Cursor c) {
        return new Tag(c.getInt(c.getColumnIndex("_id")),
                c.getString(c.getColumnIndex(Tag.Table.TEXT)),
                c.getInt(c.getColumnIndex(Tag.Table.SELECTED)) == 1,
                c.getInt(c.getColumnIndex(Tag.Table.TOTAL_COUNTER)),
                c.getInt(c.getColumnIndex(Table.STUDIED_COUNTER)));
    }

    public Integer getId() {
        return mId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tag tag = (Tag) o;

        if (mSelected != tag.mSelected) return false;
        if (mId != null ? !mId.equals(tag.mId) : tag.mId != null) return false;
        if (mName != null ? !mName.equals(tag.mName) : tag.mName != null) return false;
        if (mQuestionsCounter != null ? !mQuestionsCounter.equals(tag.mQuestionsCounter) : tag.mQuestionsCounter != null)
            return false;
        return mQuestionsStudied != null ? mQuestionsStudied.equals(tag.mQuestionsStudied) : tag.mQuestionsStudied == null;

    }

    @Override
    public int hashCode() {
        int result = mId != null ? mId.hashCode() : 0;
        result = 31 * result + (mName != null ? mName.hashCode() : 0);
        result = 31 * result + (mQuestionsCounter != null ? mQuestionsCounter.hashCode() : 0);
        result = 31 * result + (mQuestionsStudied != null ? mQuestionsStudied.hashCode() : 0);
        result = 31 * result + (mSelected ? 1 : 0);
        return result;
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
