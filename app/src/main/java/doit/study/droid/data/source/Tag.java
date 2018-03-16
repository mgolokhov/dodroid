package doit.study.droid.data.source;

import android.arch.persistence.room.TypeConverters;

import java.util.List;

import doit.study.droid.data.source.local.Converters;

public class Tag {
    public final int id;
    public final String text;
    public final int quantity;
    public int learned;

    @TypeConverters(Converters.IdConverter.class)
    public final List<Integer> questionIds;

    public List<Integer> getQuestionIds() {
        return questionIds;
    }

    public int getId() {
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

    public boolean checkedAnyQuestion;

    public Tag(int id, String text, int quantity, int learned, List<Integer> questionIds, boolean checkedAnyQuestion) {
        this.id = id;
        this.text = text;
        this.quantity = quantity;
        this.learned = learned;
        this.questionIds = questionIds;
        this.checkedAnyQuestion = checkedAnyQuestion;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", checkedAnyQuestion=" + checkedAnyQuestion +
                ", quantity=" + quantity +
                ", learned=" + learned +
                //", questionIds=" + questionIds +
                '}';
    }

}
