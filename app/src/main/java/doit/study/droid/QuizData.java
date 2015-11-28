package doit.study.droid;

import java.util.HashMap;

public class QuizData {
    private HashMap<Integer, Question> mQuestions = new HashMap<>();
    private int mTotalWrongCounter;
    private int mTotalRightCounter;

    public Question getById(int id){
        return mQuestions.get(id);
    }

    public int size(){
        return mQuestions.size();
    }

    public void addQuestion(Question q){
        mQuestions.put(q.getId(), q);
    }

    public void addAllQuestions(HashMap<Integer, Question> questions){
        mQuestions = questions;
    }

    public void incrementWrongCounter(int id){
        mQuestions.get(id).incrementWrongCounter();
        mTotalWrongCounter++;
    }

    public void incrementRightCounter(int id){
        mQuestions.get(id).incrementRightCounter();
        mTotalRightCounter++;
    }

    public int getTotalWrongCounter(){
        return mTotalWrongCounter;
    }

    public int getTotalRightCounter(){
        return mTotalRightCounter;
    }
}
