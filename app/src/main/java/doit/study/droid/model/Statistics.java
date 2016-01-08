package doit.study.droid.model;

import java.util.Date;
import java.util.HashMap;

public class Statistics {

    public static final String STAT_SHAREDPREF = "doit.study.droid.model.stat_sharedpref";
    public static final String TOTAL_WRONG_CNT_KEY = "total_wrong_cnt_key";
    public static final String TOTAL_RIGHT_CNT_KEY = "total_right_cnt_key";

    public static final class Table {
        public static final String NAME = "statistics";
        public static final String WRONG_ANS_CNT = "wrong_ans_cnt";
        public static final String RIGHT_ANS_CNT = "right_ans_cnt";
        public static final String LAST_VIEWED_AT = "last_viewed_at";
        public static final String STUDIED_AT = "studied_at";
        // status
        // 0 - a new
        // 1 - added for learning
        // 2 - in progress (one or two right hits)
        // 3 - studied (three or more right hits)
        public static final String STATUS = "status";
    }

    public enum STATUS {NEW, ADDED, IN_PROGRESS, STUDIED}


    private static int sTotalWrongCounter;
    private static int sTotalRightCounter;

    public static int getTotalWrongCounter() {
        return sTotalWrongCounter;
    }

    public static int getTotalRightCounter() {
        return sTotalRightCounter;
    }


    public static class UserStatistics{
        private int mWrongCounter;
        private int mRightCounter;
        private Date mStudiedAt;
        private Date mLastViewedAt;

        public int getWrongCounter() {
            return mWrongCounter;
        }

        public int getRightCounter() {
            return mRightCounter;
        }

        public void incWrongCounter(){
            mWrongCounter++;
            sTotalWrongCounter++;
        }
        public void incRightCounter(){
            mRightCounter++;
            sTotalRightCounter++;
        }
    }
}
