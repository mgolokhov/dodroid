package doit.study.droid.domain

import doit.study.droid.quiz.QuizItem
import javax.inject.Inject

class IsQuizAnsweredRightUseCase @Inject constructor() {
    operator fun invoke(quizItem: QuizItem): Boolean {
        val isRightAnswersChecked = quizItem.answerVariants.none { it.isRight && !it.isChecked }
        val isWrongAnswersUnchecked = quizItem.answerVariants.none { !it.isRight && it.isChecked }
        return isRightAnswersChecked && isWrongAnswersUnchecked
    }
}
