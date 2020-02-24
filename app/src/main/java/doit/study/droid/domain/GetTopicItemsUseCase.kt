package doit.study.droid.domain

import doit.study.droid.data.local.QuizDatabase
import doit.study.droid.topic.TopicItem
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetTopicItemsUseCase @Inject constructor(
    private val quizDatabase: QuizDatabase
) {
    suspend operator fun invoke(query: String = ""): List<TopicItem> = withContext(Dispatchers.IO) {
        val tags = quizDatabase.tagDao().getTags()
        val topics = ArrayList<TopicItem>()
        tags.filter { it.name.contains(query, ignoreCase = true) }.forEach { tag ->
            val questions = quizDatabase.questionDao().getQuestionsByTag(tag.name)
            questions.filter { it.studiedAt != 0L }.size
            topics.add(
                    TopicItem(
                            id = tag.id,
                            name = tag.name,
                            counterTotal = questions.size,
                            counterStudied = questions.filter { it.studiedAt != 0L }.size,
                            selected = tag.selected
                    )
            )
        }
        return@withContext topics
    }
}
