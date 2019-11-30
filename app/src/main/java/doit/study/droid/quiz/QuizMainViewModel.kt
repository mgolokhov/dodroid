package doit.study.droid.quiz

import android.app.Application
import androidx.lifecycle.*
import doit.study.droid.R
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
        private val quizDatabase: QuizDatabase,
        private val appContext: Application
): ViewModel() {
    private val _items = MutableLiveData<List<QuizItem>>()
    val items: LiveData<List<QuizItem>> = _items

    private val _actionBarTitle = MutableLiveData<String>()
    val actionBarTitle: LiveData<String> = _actionBarTitle

    private val _swipeToResultPageEvent = MutableLiveData<Event<Int>>()
    val swipeToResultPageEvent: LiveData<Event<Int>> = _swipeToResultPageEvent

    private val _addResultPageEvent = MutableLiveData<Event<Unit>>()
    val addResultPageEvent: LiveData<Event<Unit>> = _addResultPageEvent

    private val questionsLeft: Int?
        get() = _items.value?.filter { !it.answered }?.size



    init {
        Timber.d("init viewmodel $this")
        loadQuizItems()
    }

    private fun loadQuizItems() {
        viewModelScope.launch(Dispatchers.IO) {
                val tags = quizDatabase.tagDao().getTagBySelection(isSelected = true)
                val allSelectedItems = HashSet<QuizItem>()
                tags.forEach{
                    val questions = quizDatabase.questionDao().getQuestionsByTag(it.name)
                    allSelectedItems.addAll(
                            questions.map { question ->
                                QuizItem(
                                        questionId = question.id,
                                        questionText = question.text,
                                        title = it.name,
                                        rightVariants = question.right,
                                        answerVariants = (question.right + question.wrong).shuffled(),
                                        docLink = question.docLink
                                )
                            })
                Timber.d("loadQuizItems ${allSelectedItems.size}")
                _items.postValue(allSelectedItems.shuffled().take(MAX_ITEMS_IN_ONE_QUIZ))
                withContext(Dispatchers.Main) {
                    refreshUi()
                }
            }
        }
    }

    fun refreshUi() {
        updateQuizProgress()
        if (isQuizCompleted() && !isPageResultAdded()) {
            addResultPage()
            swipeToResultPage()
            saveResult()
        }
    }

    private fun isPageResultAdded(): Boolean {
        return _addResultPageEvent.value != null
    }

    private fun isQuizCompleted(): Boolean {
        return questionsLeft == 0
    }

    private fun addResultPage() {
        _addResultPageEvent.value = Event(Unit)
    }

    private fun swipeToResultPage() {
        _swipeToResultPageEvent.value = Event(getCountForPager())
    }

    private fun updateQuizProgress() {
        questionsLeft?.let { quantity ->
            _actionBarTitle.value = (
                    if (quantity == 0)
                        appContext.getString(R.string.test_completed)
                    else
                        appContext.resources.getQuantityString(
                                R.plurals.numberOfQuestionsInTest,
                                quantity,
                                quantity
                        )
                    )
        }
    }

    private fun saveResult() {
        GlobalScope.launch(Dispatchers.IO) {
            // TODO: dirty stub, replace with real logic
            // yeah, batching
            _items.value?.forEach {
                val wasAnsweredRight = it.rightVariants.toSet() == it.selectedVariants
                quizDatabase.questionDao().updateStatistics(
                        id = it.questionId,
                        rightCount = if (wasAnsweredRight) 1 else 0,
                        wrongCount = if (wasAnsweredRight) 0 else 1,
                        studiedAt = if (wasAnsweredRight) Date().time else 0
                )
            }
        }
    }

    fun getTabTitle(position: Int): String {
        return if (isPageResultAdded() && position == items.value?.size)
            appContext.resources.getString(R.string.test_result_title)
        else {
            items.value?.let {
                "${it[position].title} ${position + 1}/${it.size}"
            } ?: ""
        }

    }

    fun getCountForPager(): Int {
        return items.value?.let {
            if (isPageResultAdded())
                it.size + 1
            else
                it.size
        } ?: 0
    }

    fun getItemType(position: Int): String {
        val size = items.value?.size ?: 0
        return when(position) {
            in 0 until size -> { QUIZ_QUESTION_ITEM_TYPE }
            size -> { ONE_TEST_SUMMARY_TYPE }
            else -> {"oh, shit"}
        }
    }

    fun getResultCounters(): Pair<Int, Int> {
        items.value!!.let { all ->
            val rightAnswers = all.filter { it.answered && it.selectedVariants == it.rightVariants.toSet()}.size
            val wrongAnswers = all.size - rightAnswers
            return Pair(rightAnswers, wrongAnswers)
        }
    }

    companion object {
        private const val MAX_ITEMS_IN_ONE_QUIZ = 10
    }
}