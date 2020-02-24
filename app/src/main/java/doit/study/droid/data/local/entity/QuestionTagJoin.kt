package doit.study.droid.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
        foreignKeys = [
            ForeignKey(
                    entity = Question::class,
                    parentColumns = ["id"],
                    childColumns = ["questionId"]
            ),
            ForeignKey(
                    entity = Tag::class,
                    parentColumns = ["id"],
                    childColumns = ["tagId"]
            )
        ],
        indices = [
            Index("questionId"),
            Index("tagId")
        ]
)
data class QuestionTagJoin(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val questionId: Int,
    val tagId: Int
)
