package doit.study.droid;

public class Statistics {
    // Database constants
    public static class Table {
        /** for primary key check
         *  @see doit.study.droid.QuizData.Table
         */
        public static final String NAME = "statistics";
        public static final String ON_LEARNING = "on_learning";
        public static final String WRONG_COUNTER = "wrong_counter";
        public static final String RIGHT_COUNTER = "right_counter";
    }

    private QuizData.Id mId;
    private int mWrongCounter;
    private int mRightCounter;
    private int mIsOnLearning;
    // TODO: thread safety
    private static int mTotalRightCnt;
    private static int mTotalWrongCnt;

    public Statistics(QuizData.Id id, int wrongCounter, int rightCounter, int isOnLearning){
        mId = id;
        mWrongCounter = wrongCounter;
        mRightCounter = rightCounter;
        mIsOnLearning = isOnLearning;
    }

    public QuizData.Id getId() {
        return mId;
    }

    public void incWrongCnt(){
        mWrongCounter++;
        mTotalWrongCnt++;
    }

    public void incRightCnt(){
        mRightCounter++;
        mTotalRightCnt++;
    }

    public int getRightCounter() {
        return mRightCounter;
    }

    public int getWrongCounter() {
        return mWrongCounter;
    }

    public int getIsOnLearning() {
        return mIsOnLearning;
    }

    public static int getTotalRightCnt() {
        return mTotalRightCnt;
    }
    public static int getTotalWrongCnt() {
        return mTotalWrongCnt;
    }
}
