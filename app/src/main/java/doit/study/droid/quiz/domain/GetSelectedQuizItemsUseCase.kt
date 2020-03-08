package doit.study.droid.quiz.domain

import doit.study.droid.data.QuizRepository
import doit.study.droid.quiz.QuizItem
import doit.study.droid.quiz.mappers.toQuizItem
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetSelectedQuizItemsUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    suspend operator fun invoke(): HashSet<QuizItem> = withContext(Dispatchers.IO) {
        val selectedTopics = quizRepository.getTagsBySelection(isSelected = true)
        val selectedUniqueQuestionItems = selectedTopics
                .map { tag ->
                    val questions = quizRepository.getQuestionsByTag(tag.name)
                    questions.map { question -> question.toQuizItem(tag.name) }
                }
                .flatten()
                .toHashSet()
        return@withContext selectedUniqueQuestionItems
    }
}
