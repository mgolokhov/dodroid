package doit.study.droid.data;

import java.util.List;

public class QuizData {
    private QuizDBHelper mDBHelper;

    public QuizData(QuizDBHelper dbHelper) {
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
