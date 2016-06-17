package doit.study.droid.data;

public class RelationTables {

    public static final class QuestionTag {
        public static final String NAME = "question_tag_relation";
        public static final String TAG_ID = "tag_id";
        public static final String QUESTION_ID = "question_id";
        // fully qualified names
        public static final String FQ_TAG_ID = NAME + "." + TAG_ID;
        public static final String FQ_QUESTION_ID = NAME + "." + QUESTION_ID;

        private QuestionTag() {}
    }


    public static final String [] JoinedQuestionTagProjection = {
        Question.Table.FQ_ID,
        Question.Table.FQ_TEXT,
        Question.Table.FQ_DOC_LINK,
        Question.Table.FQ_RIGHT_ANSWERS,
        Question.Table.FQ_WRONG_ANSWERS,
        Tag.Table.FQ_TEXT,
        Question.Table.FQ_RIGHT_ANS_CNT,
        Question.Table.FQ_WRONG_ANS_CNT,
        Question.Table.FQ_STATUS
    };

    private RelationTables() {}
}
