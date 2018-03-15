package doit.study.droid.data.source.local.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

import doit.study.droid.data.source.local.Converters;

@Entity(tableName = "questions")
public class QuestionEntity {
    @PrimaryKey
    public final int id;
    public final String text;
    public final String wrong;
    public final String right;
    public final String docLink;

    public QuestionEntity(int id, String text, String wrong, String right, String docLink) {
        this.id = id;
        this.text = text;
        this.wrong = wrong;
        this.right = right;
        this.docLink = docLink;
    }

    public QuestionEntity(int id, String text, List<String> wrong, List<String> right, String docLink) {
        this.id = id;
        this.text = text;
        this.wrong = Converters.listToString(wrong);
        this.right = Converters.listToString(right);
        this.docLink = docLink;
    }
}
