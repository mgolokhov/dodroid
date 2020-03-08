package doit.study.droid.quiz.mappers

import doit.study.droid.data.local.entity.Question
import doit.study.droid.quiz.AnswerVariantItem
import doit.study.droid.quiz.QuizItem

fun Question.toQuizItem(tagName: String): QuizItem {
    val answerVariants =
            right.map { AnswerVariantItem(text = it, isRight = true) } +
            wrong.map { AnswerVariantItem(text = it, isRight = false) }

    return QuizItem(
            questionId = id,
            questionText = text,
            answerVariants = answerVariants.toMutableList(),
            title = tagName,
            docLink = docLink
    )
}
