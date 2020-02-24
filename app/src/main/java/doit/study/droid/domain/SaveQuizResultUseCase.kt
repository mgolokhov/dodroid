package doit.study.droid.domain

import doit.study.droid.data.local.QuizDatabase
import doit.study.droid.quiz.QuizItem
import java.util.Date
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SaveQuizResultUseCase @Inject constructor(
    private val quizDatabase: QuizDatabase,
    private val isQuizAnsweredRightUseCase: IsQuizAnsweredRightUseCase
) {
    suspend operator fun invoke(quizItems: List<QuizItem>?) = withContext(Dispatchers.IO) {
        // TODO: dirty stub, replace with real logic
        // yeah, batching
        quizItems?.forEach {
            val isQuizAnsweredRight = isQuizAnsweredRightUseCase(it)
            quizDatabase.questionDao().updateStatistics(
                    id = it.questionId,
                    rightCount = if (isQuizAnsweredRight) 1 else 0,
                    wrongCount = if (isQuizAnsweredRight) 0 else 1,
                    studiedAt = if (isQuizAnsweredRight) Date().time else 0
            )
        }
    }
}
