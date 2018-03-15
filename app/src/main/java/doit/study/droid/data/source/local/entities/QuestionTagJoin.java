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
                        entity=QuestionEntity.class,
                        parentColumns="id",
                        childColumns="questionId",
                        onDelete=CASCADE),
                @ForeignKey(
                        entity=TagEntity.class,
                        parentColumns="id",
                        childColumns="tagId",
                        onDelete=CASCADE)},
        indices={
                @Index(value="questionId"),
                @Index(value="tagId")
        } )
public class QuestionTagJoin {
    @NonNull
    public final long questionId;
    @NonNull
    public final long tagId;

    public QuestionTagJoin(long questionId, long tagId) {
        this.questionId=questionId;
        this.tagId = tagId;
    }
}
