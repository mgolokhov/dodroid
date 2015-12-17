package doit.study.droid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import doit.study.droid.sqlite.helper.DatabaseHelper;

public class QuizData {
    private int mTotalWrongCounter;
    private int mTotalRightCounter;
    private DatabaseHelper mDBHelper;
    private int mSize;
    private ArrayList<Integer> mIds;
    private int mTagsCount;
    private List<Integer> mTagIds;
    private List<Integer> mSelectedTagIds = new ArrayList<>();

    public QuizData(DatabaseHelper dbHelper) {
        this.mDBHelper = dbHelper;
        init();
    }

    private void init() {
        this.mIds = this.mDBHelper.getQuestionIds();
        this.mSize = this.mIds.size();
        this.mTagIds = this.mDBHelper.getTagIds();
        this.mTagsCount = this.mTagIds.size();
    }

    public ArrayList<Integer> getQuestionIds() {
        return mIds;
    }

    public int idAtPosition(int pos) {
        return mIds.get(pos);
    }

    public Question getById(int id){
        return this.mDBHelper.getQuestionById(id);
    }

    public int size(){
        return this.mSize;
    }

    public List<Integer> getTagIds() {
        return mTagIds;
    }

    public int tagIdAtPosition(int pos) {
        return mTagIds.get(pos);
    }

    public Tag getTagById(int id){
        return this.mDBHelper.getTagById(id);
    }

    public int tagsCount(){
        return this.mTagsCount;
    }

    public void setSelectedTagIds(List<Integer> selectedTagIds) {
        this.mSelectedTagIds = selectedTagIds;
    }

    public List<Integer> getSelectedTagIds() {
        return this.mSelectedTagIds;
    }

    public void incrementWrongCounter(int id){
        mTotalWrongCounter++;
    }

    public void incrementRightCounter(int id){
        mTotalRightCounter++;
    }

    public int getTotalWrongCounter(){
        return mTotalWrongCounter;
    }

    public int getTotalRightCounter(){
        return mTotalRightCounter;
    }

    public void addSelectedTag(int tagId) {
        mSelectedTagIds.add(tagId);
    }

    public void removeSelectedTag(int tagId) {
        int index = mSelectedTagIds.indexOf(tagId);
        if (index == -1) return;
        mSelectedTagIds.remove(index);
    }

    public ArrayList<Integer> getQuestionIdsToWorkWith() {
        if (mSelectedTagIds.isEmpty()) return getQuestionIds();
        return getQuestionIdsByTags();
    }

    private ArrayList<Integer> getQuestionIdsByTags() {
        return mDBHelper.getQuestionIdsByTags(mSelectedTagIds);
    }

    public List<Tag> getTags() {
        return mDBHelper.getTags();
    }

    public Map<Integer, Tag.Stats> getTagStats() {
        return mDBHelper.getTagStats();
    }
}
