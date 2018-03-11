package doit.study.droid.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

import static doit.study.droid.data.Converters.listToString;

@Entity(tableName = "questions")
public class Question {
    @PrimaryKey
    public final int id;
    public final String text;
    public final String wrong;
    public final String right;
    public final String docLink;

    public Question(int id, String text, String wrong, String right, String docLink) {
        this.id = id;
        this.text = text;
        this.wrong = wrong;
        this.right = right;
        this.docLink = docLink;
    }

    public Question(int id, String text, List<String> wrong, List<String> right, String docLink) {
        this.id = id;
        this.text = text;
        this.wrong = listToString(wrong);
        this.right = listToString(right);
        this.docLink = docLink;
    }
}
