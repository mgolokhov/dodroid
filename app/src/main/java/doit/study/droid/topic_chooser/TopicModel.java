package doit.study.droid.topic_chooser;

import android.arch.persistence.room.TypeConverters;

import java.util.List;

import doit.study.droid.data.source.local.Converters;

public class TopicModel {
    private final long id;
    private final String text;
    private final int quantity;
    private int learned;
    private boolean checked;

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

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public TopicModel(long id, String text, int quantity, int learned, List<Integer> questionIds, boolean checked) {
        this.id = id;
        this.text = text;
        this.quantity = quantity;
        this.setLearned(learned);
        this.questionIds = questionIds;
        this.setChecked(checked);
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + getId() +
                ", text='" + getText() + '\'' +
                ", checked=" + isChecked() +
                ", quantity=" + getQuantity() +
                ", learned=" + getLearned() +
                //", questionIds=" + questionIds +
                '}';
    }

}
