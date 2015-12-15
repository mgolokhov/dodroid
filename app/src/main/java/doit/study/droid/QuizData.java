package doit.study.droid;

import java.util.ArrayList;
import java.util.List;

import doit.study.droid.sqlite.helper.DatabaseHelper;

public class QuizData {
    //private HashMap<Integer, Question> mQuestions = new HashMap<>();
    private int mTotalWrongCounter;
    private int mTotalRightCounter;
    private DatabaseHelper mDBHelper;
    private int mSize;
    private List<Integer> mIds;
    private int mTagsCount;
    private List<Integer> mTagIds;
    private List<Integer> mSelectedTagIds = new ArrayList<>();

    public QuizData(DatabaseHelper dbHelper) {
        this.mDBHelper = dbHelper;
        init();
    }

    private void init() {
        this.mSize = this.mDBHelper.countQuestions();
        this.mIds = this.mDBHelper.getQuestionIds();
        this.mTagIds = this.mDBHelper.getTagIds();
        this.mTagsCount = this.mTagIds.size();
    }

    public List<Integer> getQuestionIds() {
        return mIds;
    }

    public int idAtPosition(int pos) {
        return mIds.get(pos);
    }

    public int tagsCount() {
        return mTagsCount;
    }
    public int tagIdAtPosition(int pos) {
        return mTagIds.get(pos);
    }

    public Question getById(int id){
        return this.mDBHelper.getQuestionById(id);
    }

    public Tag getTagById(int id) {
        return this.mDBHelper.getTagById(id);
    }
    public int size(){
        return this.mSize;
    }

//    public void addQuestion(Question q){
//        mQuestions.put(q.getId(), q);
//    }
//
//    public void addAllQuestions(HashMap<Integer, Question> questions){
//        mQuestions = questions;
//    }

    public void incrementWrongCounter(int id){
//        mQuestions.get(id).incrementWrongCounter();
        mTotalWrongCounter++;
    }

    public void incrementRightCounter(int id){
//        mQuestions.get(id).incrementRightCounter();
        mTotalRightCounter++;
    }

    public int getTotalWrongCounter(){
        return mTotalWrongCounter;
    }

    public int getTotalRightCounter(){
        return mTotalRightCounter;
    }

    public boolean isSelectedTagId(int tagId) {
        return mSelectedTagIds.contains(tagId);
    }

    public void addSelectedTag(int tagId) {
        mSelectedTagIds.add(tagId);
    }

    public void removeSelectedTag(int tagId) {
        int index = mSelectedTagIds.indexOf(tagId);
        if (index == -1) return;
        mSelectedTagIds.remove(index);
    }

    public List<Integer> getQuestionIdsToWorkWith() {
        if (mSelectedTagIds.isEmpty()) return getQuestionIds();
        return getQuestionIdsByTags();
    }

    private List<Integer> getQuestionIdsByTags() {
        return mDBHelper.getQuestionIdsByTags(mSelectedTagIds);
    }
}
