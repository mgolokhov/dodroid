package doit.study.droid.domain

import doit.study.droid.quiz.QuizItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ShuffleQuizContentUseCase @Inject constructor() {
    suspend operator fun  invoke(
            items: HashSet<QuizItem>,
            itemsInQuiz: Int = MAX_ITEMS_IN_ONE_QUIZ
    ): List<QuizItem> = withContext(Dispatchers.Default) {
        val res = items.shuffled().take(itemsInQuiz)
        res.forEach {
            it.answerVariants.shuffle()
        }
        return@withContext res
    }

    companion object {
        private const val MAX_ITEMS_IN_ONE_QUIZ = 10
    }
}