package doit.study.droid;

import java.util.List;

import doit.study.droid.model.Question;
import doit.study.droid.model.Tag;
import doit.study.droid.sqlite.helper.DatabaseHelper;

public class QuizData {
    public final static String COUNTERS = "wrong_right_counters";
    public final static String WRONG_COUNTER = "wrong_counter";
    public final static String RIGHT_COUNTER = "right_counter";

    private DatabaseHelper mDBHelper;

    public QuizData(DatabaseHelper dbHelper) {
        mDBHelper = dbHelper;
    }

    public List<Integer> getQuestionIds() {
        return mDBHelper.getQuestionIds();
    }

    public List<Integer> getTagIds() {
        return mDBHelper.getTagIds();
    }

    public Question getQuestionById(int questionId) {
        return mDBHelper.getQuestionById(questionId);
    }

    public Tag getTagById(int tagId){
        return mDBHelper.getTagById(tagId);
    }

    public void setTagSelection(Tag tag){
        mDBHelper.setTagSelection(tag);
    }

    public List<Integer> getRandSelectedQuestionIds(Integer limit){
        return mDBHelper.getRandSelectedQuestionIds(limit);
    }

    public void setQuestion(Question q){
        mDBHelper.setQuestion(q);
    }
}
