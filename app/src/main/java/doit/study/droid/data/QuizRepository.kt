package doit.study.droid.data

import doit.study.droid.data.local.QuizContentVersion
import doit.study.droid.data.local.QuizDatabase
import doit.study.droid.data.local.entity.Question
import doit.study.droid.data.local.entity.QuestionTagJoin
import doit.study.droid.data.local.entity.Tag
import doit.study.droid.data.remote.Configuration
import doit.study.droid.data.remote.QuizData
import doit.study.droid.data.remote.QuizDataClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class QuizRepository @Inject constructor(
        private val quizDatabase: QuizDatabase,
        private val quizContentVersion: QuizContentVersion,
        private val quizDataClient: QuizDataClient
) {
    private lateinit var config: Configuration
    private val cachedTags = mutableMapOf<String, Int>()

    suspend fun sync(forceUpdate: Boolean): Outcome<Unit> = withContext(Dispatchers.IO) {
        try {
            if (forceUpdate || isThereNewContent()) {
                updateData(quizDataClient.quizData())
            }
        } catch (e: Exception) {
            Timber.e(e)
            return@withContext Outcome.Error(e)
        }
        return@withContext Outcome.Success(Unit)
    }

    suspend fun selectTags(tags: List<Tag>) {
        quizDatabase.tagDao().insertOrReplaceTag(*tags.toTypedArray())
    }

    private suspend fun isThereNewContent(): Boolean = withContext(Dispatchers.IO) {
        config = quizDataClient.configuration()
        return@withContext config.contentVersion > quizContentVersion.getVersion()
    }

    private suspend fun updateData(quizData: List<QuizData>) = withContext(Dispatchers.IO) {
        quizDatabase.runInTransaction {
            quizData.forEach { item ->
                // TODO: what's a proper handling of
                //  "Suspension functions can be called only within coroutine body" ?
                runBlocking {
                    populateQuestion(item)
                    populateTagAndRelationToQuestion(item)
                }
            }
            quizContentVersion.saveVersion(config.contentVersion)
        }
    }

    private suspend fun populateQuestion(quizItem: QuizData) {
        val question = Question(
                id = quizItem.id,
                text = quizItem.text,
                docLink = quizItem.docRef,
                right = quizItem.rightAnswers,
                wrong = quizItem.wrongAnswers
        )
        quizDatabase.questionDao().insertQuestion(question)
    }

    private suspend fun populateTagAndRelationToQuestion(quizItem: QuizData) {
        quizDatabase.tagDao().getTags().forEach {
            cachedTags[it.name] = it.id
        }
        quizItem.tags.forEach { tag ->
            val tagId = cachedTags[tag]
                    ?: quizDatabase.tagDao().insertTag(Tag(name = tag)).toInt()
            cachedTags[tag] = tagId
            quizDatabase.tagDao().insertQuestionTagJoin(
                    QuestionTagJoin(
                            questionId = quizItem.id,
                            tagId = tagId
                    ))
        }
    }
}