package doit.study.droid.model;

public class Tag {

    public static final class Table {
        public static final String NAME = "tags";
        public static final String TEXT = "text";
        public static final String SELECTED = "selected";
    }

    public Integer getId() {
        return mId;
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

    public String getName() {
        return mName;
    }

    public Integer getQuestionsStudied() {
        return mQuestionsStudied;
    }

    public Integer getQuestionsCounter() {
        return mQuestionsCounter;
    }

    public void select(){
        mSelected = true;
    }

    public void unselect(){
        mSelected = false;
    }

    public boolean getSelectionStatus(){
        return mSelected;
    }
}
