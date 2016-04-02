package doit.study.droid.data;

public class RelationTables {

    public static final class QuestionTag {
        public static final String NAME = "question_tag_relation";
        public static final String TAG_ID = "tag_id";
        public static final String QUESTION_ID = "question_id";
        // fully qualified names
        public static final String FQ_TAG_ID = NAME + "." + TAG_ID;
        public static final String FQ_QUESTION_ID = NAME + "." + QUESTION_ID;
    }
}
