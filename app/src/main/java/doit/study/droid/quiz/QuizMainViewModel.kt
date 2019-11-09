package doit.study.droid.quiz

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import doit.study.droid.data.local.QuizDatabase
import doit.study.droid.utils.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashSet


class QuizMainViewModel @Inject constructor(
        private val quizDatabase: QuizDatabase
): ViewModel() {
    private val _items = MutableLiveData<List<QuizView>>()
    val items: LiveData<List<QuizView>> = _items

    private val _updateTitle = MutableLiveData<Int>()
    val updateTitle: LiveData<Int> = _updateTitle

    private val _swipe = MutableLiveData<Event<Unit>>()
    val addResultPageAndSwipeOnce: LiveData<Event<Unit>> = _swipe

    init {
        Timber.d("init viewmodel $this")
        loadQuizItems()
    }

    private fun loadQuizItems() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val tags = quizDatabase.tagDao().getTagBySelection(isSelected = true)
                val allSelectedItems = HashSet<QuizView>()
                tags.forEach{
                    val questions = quizDatabase.questionDao().getQuestionsByTag(it.name)
                    allSelectedItems.addAll(
                            questions.map { question ->
                                QuizView(
                                        questionId = question.id,
                                        questionText = question.text,
                                        title = it.name,
                                        rightVariants = question.right,
                                        answerVariants = (question.right + question.wrong).shuffled(),
                                        docLink = question.docLink
                                )
                            })
                }
                Timber.d("loadQuizItems ${allSelectedItems.size}")
                _items.postValue(allSelectedItems.shuffled().take(MAX_ITEMS_IN_ONE_QUIZ))
                withContext(Dispatchers.Main) {
                    updateQuestionsLeft()
                }
            }
        }
    }

    fun updateQuestionsLeft() {
        val questionsLeft = _items.value?.filter { !it.answered }?.size ?: 0
        Timber.d("updateQuestionsLeft questionsLeft $questionsLeft")
        _updateTitle.value = questionsLeft

        if (questionsLeft == 0 && _swipe.value == null) {
            _swipe.value = Event(Unit)
            storeQuizResults()
        }
    }

    private fun storeQuizResults() {
        GlobalScope.launch(Dispatchers.IO) {
            // TODO: dirty stub, replace with real logic
            // yeah, batching
            _items.value?.forEach {
                val wasAnsweredRight = it.rightVariants.toSet() == it.selectedVariants
                quizDatabase.questionDao().updateStatistics(
                        id = it.questionId,
                        rightCnt = if (wasAnsweredRight) 1 else 0,
                        wrongCnt = if (wasAnsweredRight) 0 else 1,
                        studiedAt = if (wasAnsweredRight) Date().time else 0
                )
            }
        }
    }

    companion object {
        private const val MAX_ITEMS_IN_ONE_QUIZ = 10
    }
}