package doit.study.droid.topic

data class TopicItem(
    val id: Int,
    val name: String,
    val counterTotal: Int,
    val counterStudied: Int,
    val selected: Boolean
)
