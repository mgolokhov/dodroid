package doit.study.droid.domain

import doit.study.droid.data.local.QuizDatabase
import doit.study.droid.data.local.entity.Tag
import doit.study.droid.topic.TopicItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class SaveTopicItemsUseCase @Inject constructor(
        private val quizDatabase: QuizDatabase
) {
    suspend operator fun invoke(
            vararg topicItems: TopicItem,
            selected: Boolean
    ) = withContext(Dispatchers.IO) {
        val tags = topicItems.map {
            Tag(
                    id = it.id,
                    name = it.name,
                    selected = selected
            )
        }
        val res = quizDatabase.tagDao().insertOrReplaceTag(*tags.toTypedArray())
        Timber.d("insertOrReplaceTag $res")
    }
}