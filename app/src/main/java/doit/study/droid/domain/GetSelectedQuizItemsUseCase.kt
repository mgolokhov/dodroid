package doit.study.droid.domain

import doit.study.droid.data.local.QuizDatabase
import doit.study.droid.quiz.AnswerVariantItem
import doit.study.droid.quiz.QuizItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class GetSelectedQuizItemsUseCase @Inject constructor(
        private val quizDatabase: QuizDatabase
) {
    suspend operator fun invoke (): HashSet<QuizItem> = withContext(Dispatchers.IO) {
        val tags = quizDatabase.tagDao().getTagBySelection(isSelected = true)
        val allSelectedItems = HashSet<QuizItem>()
        tags.forEach {
            val questions = quizDatabase.questionDao().getQuestionsByTag(it.name)
            allSelectedItems.addAll(
                    questions.map { question ->
                        val answerVariants: MutableList<AnswerVariantItem> = mutableListOf<AnswerVariantItem>()
                        question.wrong.forEach {
                            answerVariants.add(
                                    AnswerVariantItem(
                                            text = it,
                                            isRight = false
                                    )
                            )
                        }
                        question.right.forEach {
                            answerVariants.add(
                                    AnswerVariantItem(
                                            text = it,
                                            isRight = true
                                    )
                            )
                        }
                        QuizItem(
                                questionId = question.id,
                                questionText = question.text,
                                answerVariants = answerVariants,
                                title = it.name,
                                docLink = question.docLink
                        )
                    })
            Timber.d("loadQuizItems ${allSelectedItems.size}")
        }
        return@withContext allSelectedItems
    }
}