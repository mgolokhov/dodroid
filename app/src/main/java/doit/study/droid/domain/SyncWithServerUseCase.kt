package doit.study.droid.domain

import doit.study.droid.data.Outcome
import doit.study.droid.data.QuizRepository
import doit.study.droid.data.local.QuizContentVersion
import doit.study.droid.data.local.QuizDatabase
import doit.study.droid.data.local.entity.Question
import doit.study.droid.data.local.entity.QuestionTagJoin
import doit.study.droid.data.local.entity.Tag
import doit.study.droid.data.remote.Configuration
import doit.study.droid.data.remote.QuizData
import doit.study.droid.data.remote.QuizDataClient
import java.lang.Exception
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber

class SyncWithServerUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    suspend operator fun invoke(): Outcome<Unit> {
        return quizRepository.sync(forceUpdate = false)
    }
}
