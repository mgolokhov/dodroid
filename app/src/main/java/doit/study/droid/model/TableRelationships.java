package doit.study.droid.model;

public class TableRelationships {

    public static final String QUESTION_ID = "question_id";

    public static final class QuestionTag {
        public static final String NAME = "question_tag_relation";
        public static final String TAG_ID = "tag_id";
    }

    public static final class QuestionStatistics {
        public static final String NAME = "question_statistics_relation";
        public static final String STATISTICS_ID = "statistics_id";
    }

}
