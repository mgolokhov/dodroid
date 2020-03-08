package doit.study.droid.splash.domain

import doit.study.droid.data.Outcome
import doit.study.droid.data.QuizRepository
import javax.inject.Inject

class SyncWithServerUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    suspend operator fun invoke(): Outcome<Unit> {
        return quizRepository.sync(forceUpdate = false)
    }
}
