package doit.study.droid.data.source;

import android.arch.persistence.room.TypeConverters;

import java.util.List;

import doit.study.droid.data.source.local.Converters;

public class Tag {
    private final long id;
    private final String text;
    private final int quantity;
    private int learned;

    @TypeConverters(Converters.IdConverter.class)
    private final List<Integer> questionIds;

    public List<Integer> getQuestionIds() {
        return questionIds;
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getLearned() {
        return learned;
    }

    public void setLearned(int learned) {
        this.learned = learned;
    }

    public boolean isCheckedAnyQuestion() {
        return checkedAnyQuestion;
    }

    public void setCheckedAnyQuestion(boolean checkedAnyQuestion) {
        this.checkedAnyQuestion = checkedAnyQuestion;
    }

    private boolean checkedAnyQuestion;

    public Tag(long id, String text, int quantity, int learned, List<Integer> questionIds, boolean checkedAnyQuestion) {
        this.id = id;
        this.text = text;
        this.quantity = quantity;
        this.setLearned(learned);
        this.questionIds = questionIds;
        this.setCheckedAnyQuestion(checkedAnyQuestion);
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + getId() +
                ", text='" + getText() + '\'' +
                ", checkedAnyQuestion=" + isCheckedAnyQuestion() +
                ", quantity=" + getQuantity() +
                ", learned=" + getLearned() +
                //", questionIds=" + questionIds +
                '}';
    }

}
