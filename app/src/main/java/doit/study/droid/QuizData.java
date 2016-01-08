package doit.study.droid;

import java.util.List;
import java.util.Map;

import doit.study.droid.model.Question;
import doit.study.droid.model.Statistics;
import doit.study.droid.model.Tag;
import doit.study.droid.sqlite.helper.DatabaseHelper;

public class QuizData {
    private DatabaseHelper mDBHelper;
    private Map<Integer, Question> mQuestions;
    private Map<Integer, Tag> mTags;
    private Map<Integer, Statistics.UserStatistics> mStats;

    public QuizData(DatabaseHelper dbHelper) {
        mDBHelper = dbHelper;
    }

    public int getTotalWrongCounter(){
        return Statistics.getTotalWrongCounter();
    }

    public int getTotalRightCounter(){
        return Statistics.getTotalRightCounter();
    }

    public Statistics.UserStatistics getStatByQuestionId(int questionId){
        return null;
    }

    public List<Statistics.UserStatistics> getStatByQuestionIds(int [] questionIds){
        return null;
    }


    public List<Integer> getQuestionIds() {
        return mDBHelper.getQuestionIds();
    }

    public List<Integer> getTagIds() {
        return mDBHelper.getTagIds();
    }

    public Question getQuestionById(int questionId) {
        return null;
    }

    public Tag getTagById(int tagId){
        return mDBHelper.getTagById(tagId);
    }

    public List<Tag> getTagByIds(List<Integer> tagIds){
        return mDBHelper.getTagByIds(tagIds);
    }

    public List<Question> getQuestionsByIds(List<Integer> questionIds){
        return null;
    }

    public List<Question> getQuestionsByTagIds(List<Integer> tagIds){
        return null;
    }

    public List<Question> getSelectedTags(){
        return null;
    }

    public void setSelectedTags(List<Integer> tagIds){

    }
}
