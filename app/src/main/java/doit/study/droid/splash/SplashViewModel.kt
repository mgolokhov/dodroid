package doit.study.droid.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
        private val quizContentVersion: QuizContentVersion,
        private val quizDataClient: QuizDataClient,
        private val quizDatabase: QuizDatabase
): ViewModel() {

    private val cachedTags = mutableMapOf<String, Int>()
    private lateinit var config: Configuration

    private val _navigateToTopics = MutableLiveData<Event<Unit>>()
    val navigateToTopics: LiveData<Event<Unit>> = _navigateToTopics

    private val _showErrorAndExit = MutableLiveData<Event<String>>()
    val showErrorAndExit: LiveData<Event<String>> = _showErrorAndExit


    fun syncWithServer() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                withContext(Dispatchers.IO) {
                    if (isThereNewContent()) {
                        updateData(quizDataClient.quizData())
                    }
                }
            } catch (e: Exception) {
                _showErrorAndExit.value = Event("Error to sync, try again later")
                Timber.e(e)
            } finally {
                _navigateToTopics.value = Event(Unit)
            }
        }
    }

    private suspend fun isThereNewContent(): Boolean {
        config = quizDataClient.configuration()
        return config.contentVersion > quizContentVersion.getVersion()
    }

    private suspend fun updateData(quizData: List<QuizData>) {
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