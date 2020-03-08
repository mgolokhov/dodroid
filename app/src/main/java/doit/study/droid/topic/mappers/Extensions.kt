package doit.study.droid.topic.mappers

import doit.study.droid.data.local.entity.Tag
import doit.study.droid.topic.TopicItem

internal fun TopicItem.toTag(selected: Boolean): Tag {
    return Tag(
            id = this.id,
            name = this.name,
            selected = selected
    )
}

internal fun Tag.toTagItem(studiedQuestionsQuantity: Int, totalQuestionsQuantity: Int): TopicItem {
    return TopicItem(
            id = this.id,
            name = this.name,
            counterTotal = totalQuestionsQuantity,
            counterStudied = studiedQuestionsQuantity,
            selected = this.selected
    )
}
