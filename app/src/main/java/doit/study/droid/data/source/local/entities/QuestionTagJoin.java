package doit.study.droid.data.source.local.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(
        tableName="question_tag_join",
        primaryKeys={"questionId", "tagId"},
        foreignKeys={
                @ForeignKey(
                        entity=QuestionDb.class,
                        parentColumns="id",
                        childColumns="questionId",
                        onDelete=CASCADE),
                @ForeignKey(
                        entity=Tag.class,
                        parentColumns="id",
                        childColumns="tagId",
                        onDelete=CASCADE)},
        indices={
                @Index(value="questionId"),
                @Index(value="tagId")
        } )
public class QuestionTagJoin {
    @NonNull
    public final String questionId;
    @NonNull
    public final String tagId;

    public QuestionTagJoin(String questionId, String tagId) {
        this.questionId=questionId;
        this.tagId = tagId;
    }
}
