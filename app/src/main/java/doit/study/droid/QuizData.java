package doit.study.droid;

import java.util.List;

import doit.study.droid.sqlite.helper.DatabaseHelper;

public class QuizData {
    //private HashMap<Integer, Question> mQuestions = new HashMap<>();
    private int mTotalWrongCounter;
    private int mTotalRightCounter;
    private DatabaseHelper mDBHelper;
    private int mSize;
    private List<Integer> mIds;

    public QuizData(DatabaseHelper dbHelper) {
        this.mDBHelper = dbHelper;
        init();
    }

    private void init() {
        this.mSize = this.mDBHelper.countQuestions();
        this.mIds = this.mDBHelper.getQuestionIds();
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
}
