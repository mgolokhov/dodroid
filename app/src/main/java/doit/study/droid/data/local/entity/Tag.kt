package doit.study.droid.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import doit.study.droid.topic.TopicItem

@Entity
data class Tag(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
        // Read/Write
    val selected: Boolean = false
)


internal fun Tag.toTagItem(): TopicItem {
    TODO("not yet")
}

internal fun TopicItem.toTag(selected: Boolean): Tag {
    return Tag(
            id = this.id,
            name = this.name,
            selected = selected
    )
}