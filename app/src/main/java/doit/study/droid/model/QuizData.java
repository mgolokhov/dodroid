package doit.study.droid.model;

import java.util.List;

import doit.study.droid.model.Question;
import doit.study.droid.model.Tag;
import doit.study.droid.sqlite.helper.DatabaseHelper;

public class QuizData {
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
