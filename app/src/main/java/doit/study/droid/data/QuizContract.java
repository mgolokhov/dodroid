package doit.study.droid.data;

import android.net.Uri;

public class QuizContract {

    public final static String CONTENT_AUTHORITY = "doit.study.droid";
    public final static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class TagEntry{

        public static final String NAME = "tags";
        public static final String TEXT = "text";
        public static final String SELECTED = "selected";
    }

    public static final class  QuestionEntry {

        public static final String NAME = "questions";
        public static final String TEXT = "atext";
        public static final String WRONG_ANSWERS = "wrong_answers";
        public static final String RIGHT_ANSWERS = "right_answers";
        public static final String TRUE_OR_FALSE = "true_or_false";
        public static final String DOC_LINK = "doc_link";
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
}
