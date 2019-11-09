package doit.study.droid.quiz

import doit.study.droid.R


data class QuizView (
        val questionId: Int,
        val questionText: String,
        val title: String,
        val rightVariants: List<String>,
        val answerVariants: List<String>,
        val selectedVariants: MutableSet <String> = mutableSetOf(),
        val docLink: String,
        var answered: Boolean = false,
        var questionIsEvaluated: Boolean = false,
        // TODO: convert sealed to classes or enums
        // Possible values: 0 - default, 1 - smile, 2 - sad
        // vs directly use drawable
        var commitButtonState: Int = R.drawable.ic_done_black_48dp,
        // TODO: make configurable, const
        var attemptsLeft: Int = 2
)