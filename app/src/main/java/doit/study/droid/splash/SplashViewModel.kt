package doit.study.droid.splash

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import doit.study.droid.R
import doit.study.droid.data.local.QuizContentVersion
import doit.study.droid.data.local.QuizDatabase
import doit.study.droid.data.local.entity.Question
import doit.study.droid.data.local.entity.QuestionTagJoin
import doit.study.droid.data.local.entity.Tag
import doit.study.droid.data.remote.Configuration
import doit.study.droid.data.remote.QuizData
import doit.study.droid.data.remote.QuizDataClient
import doit.study.droid.utils.Event
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

class SplashViewModel @Inject constructor(
        private val appContext: Application,
        private val quizContentVersion: QuizContentVersion,
        private val quizDataClient: QuizDataClient,
        private val quizDatabase: QuizDatabase
): ViewModel() {

    private val cachedTags = mutableMapOf<String, Int>()
    private lateinit var config: Configuration

    private val _navigateToTopicsEvent = MutableLiveData<Event<Unit>>()
    val navigateToTopicsEvent: LiveData<Event<Unit>> = _navigateToTopicsEvent

    private val _showErrorAndExitEvent = MutableLiveData<Event<String>>()
    val showErrorAndExitEvent: LiveData<Event<String>> = _showErrorAndExitEvent


    fun syncWithServer() {
        viewModelScope.launch {
            try {
                if (isThereNewContent()) {
                    updateData(quizDataClient.quizData())
                }
            } catch (e: Exception) {
                _showErrorAndExitEvent.value = Event(appContext.getString(R.string.error_to_sync_try_again_later))
                Timber.e(e)
            } finally {
                _navigateToTopicsEvent.value = Event(Unit)
            }
        }
    }

    private suspend fun isThereNewContent(): Boolean = withContext(Dispatchers.IO) {
        config = quizDataClient.configuration()
        return@withContext config.contentVersion > quizContentVersion.getVersion()
    }

    private suspend fun updateData(quizData: List<QuizData>) = withContext(Dispatchers.IO) {
        quizDatabase.runInTransaction {
            quizData.forEach{ item ->
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

    private suspend fun populateQuestion(quizItem: QuizData){
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