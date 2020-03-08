package doit.study.droid.topic.domain

import doit.study.droid.data.QuizRepository
import doit.study.droid.topic.TopicItem
import doit.study.droid.topic.mappers.toTagItem
import javax.inject.Inject

class GetTopicItemsUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    suspend operator fun invoke(query: String = ""): List<TopicItem> {
        val tags = quizRepository.getTags()
        val topics = ArrayList<TopicItem>()
        val queriedTags = tags.filter { it.name.contains(query, ignoreCase = true) }
        queriedTags.forEach { tag ->
            val questions = quizRepository.getQuestionsByTag(tag.name)
            val studiedQuestionsQuantity = questions.filter { it.studiedAt != 0L }.size
            topics.add(
                    tag.toTagItem(
                            studiedQuestionsQuantity = studiedQuestionsQuantity,
                            totalQuestionsQuantity = questions.size
                    )
            )
        }
        return topics
    }
}
