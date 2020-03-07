package doit.study.droid.domain

import doit.study.droid.data.QuizRepository
import doit.study.droid.data.local.QuizDatabase
import doit.study.droid.data.local.entity.Tag
import doit.study.droid.data.local.entity.toTag
import doit.study.droid.topic.TopicItem
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class SaveTopicItemsUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    suspend operator fun invoke(
        vararg topicItems: TopicItem,
        selected: Boolean
    ) = withContext(Dispatchers.IO) {
        val tags = topicItems.map {
            it.toTag(selected)
        }
        val res = quizRepository.selectTags(tags)
        Timber.d("insertOrReplaceTag $res")
    }
}
