package doit.study.droid.data

import androidx.room.withTransaction
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
import javax.inject.Singleton
import kotlinx.coroutines.withContext
import timber.log.Timber

@Singleton
class QuizRepository @Inject constructor(
    private val quizDatabase: QuizDatabase,
    private val quizContentVersion: QuizContentVersion,
    private val quizDataClient: QuizDataClient,
    private val coroutineDispatchers: CoroutineDispatchers
) {
    private lateinit var config: Configuration
    private val cachedTags = mutableMapOf<String, Int>()

    @SuppressWarnings("TooGenericExceptionCaught")
    suspend fun sync(forceUpdate: Boolean): Outcome<Unit> = withContext(coroutineDispatchers.io) {
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

    suspend fun selectTags(tags: List<Tag>) = withContext(coroutineDispatchers.io) {
        quizDatabase.tagDao().insertOrReplaceTag(tags)
    }

    suspend fun getTagsBySelection(isSelected: Boolean): List<Tag> = withContext(coroutineDispatchers.io) {
        return@withContext quizDatabase.tagDao().getTagBySelection(isSelected = isSelected)
    }

    suspend fun getTags(): List<Tag> = withContext(coroutineDispatchers.io) {
        return@withContext quizDatabase.tagDao().getTags()
    }

    suspend fun getQuestionsByTag(tagName: String) = withContext(coroutineDispatchers.io) {
        return@withContext quizDatabase.questionDao().getQuestionsByTag(tagName)
    }

    suspend fun saveStatistics(
        questionId: Int,
        rightCount: Int,
        wrongCount: Int,
        studiedAt: Long
    ) = withContext(coroutineDispatchers.io) {
        quizDatabase.questionDao().updateStatistics(
                id = questionId,
                rightCount = rightCount,
                wrongCount = wrongCount,
                studiedAt = studiedAt
        )
    }

    private suspend fun isThereNewContent(): Boolean = withContext(coroutineDispatchers.io) {
        config = quizDataClient.configuration()
        return@withContext config.contentVersion > quizContentVersion.getVersion()
    }

    private suspend fun preCacheTagsFromDb() {
        quizDatabase.tagDao().getTags().forEach { tag ->
            cachedTags[tag.name] = tag.id
        }
    }

    private suspend fun updateData(quizData: List<QuizData>) = withContext(coroutineDispatchers.io) {
        preCacheTagsFromDb()
        quizDatabase.withTransaction {
            quizData.forEach { item ->
                    populateQuestion(item)
                    populateTagAndRelationToQuestion(item)
            }
            quizContentVersion.saveVersion(config.contentVersion)
        }
    }

    private suspend fun populateQuestion(quizItem: QuizData) = withContext(coroutineDispatchers.io) {
        val question = Question(
                id = quizItem.id,
                text = quizItem.text,
                docLink = quizItem.docRef,
                right = quizItem.rightAnswers,
                wrong = quizItem.wrongAnswers
        )
        quizDatabase.questionDao().insertQuestion(question)
    }

    private suspend fun insertTagIfNecessary(tagName: String): Int {
        val tagId = cachedTags[tagName] ?: quizDatabase
                .tagDao()
                .insertTag(Tag(name = tagName))
                .toInt()
        cachedTags[tagName] = tagId
        return tagId
    }

    private suspend fun populateTagAndRelationToQuestion(quizItem: QuizData) {
        quizItem.tags.forEach { tag ->
            val tagId = insertTagIfNecessary(tag)
            quizDatabase.tagDao().insertQuestionTagJoin(
                    QuestionTagJoin(
                            questionId = quizItem.id,
                            tagId = tagId
                    ))
        }
    }
}
