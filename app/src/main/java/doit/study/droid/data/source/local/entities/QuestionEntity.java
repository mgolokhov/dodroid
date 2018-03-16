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

    @Override
    public String toString() {
        return "QuestionEntity{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", wrong='" + wrong + '\'' +
                ", right='" + right + '\'' +
                ", docLink='" + docLink + '\'' +
                '}';
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QuestionEntity that = (QuestionEntity) o;

        if (id != that.id) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;
        if (wrong != null ? !wrong.equals(that.wrong) : that.wrong != null) return false;
        if (right != null ? !right.equals(that.right) : that.right != null) return false;
        return docLink != null ? docLink.equals(that.docLink) : that.docLink == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (wrong != null ? wrong.hashCode() : 0);
        result = 31 * result + (right != null ? right.hashCode() : 0);
        result = 31 * result + (docLink != null ? docLink.hashCode() : 0);
        return result;
    }
}
