package doit.study.droid.quiz

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import doit.study.droid.BuildConfig
import doit.study.droid.R
import doit.study.droid.domain.IsQuizAnsweredRightUseCase
import doit.study.droid.utils.AnalyticsData
import doit.study.droid.utils.Event
import timber.log.Timber
import javax.inject.Inject
import kotlin.random.Random


class QuizPageViewModel @Inject constructor(
        private val appContext: Application,
        private val analyticsTracker: Tracker,
        private val isQuizAnsweredRightUseCase: IsQuizAnsweredRightUseCase
) : ViewModel() {
    private val _item = MutableLiveData<QuizItem>()
    val item: LiveData<QuizItem> = _item

    private val _showToastSuccessEvent = MutableLiveData<Event<String>>()
    val showToastSuccessEvent: LiveData<Event<String>> = _showToastSuccessEvent

    private val _showToastFailureEvent = MutableLiveData<Event<String>>()
    val showToastFailureEvent: LiveData<Event<String>> = _showToastFailureEvent

    private val _playSoundEvent = MutableLiveData<Event<Boolean>>()
    val playSoundEvent: LiveData<Event<Boolean>> = _playSoundEvent

    private val _commitButtonState = MutableLiveData<Int>()
    val commitButtonState: LiveData<Int> = _commitButtonState

    private val _lockInteraction = MutableLiveData<Unit>()
    val lockInteraction: LiveData<Unit> = _lockInteraction

    private val _showToastForEvaluationEvent = MutableLiveData<Event<Int>>()
    val showToastForEvaluationEvent: LiveData<Event<Int>> = _showToastForEvaluationEvent

    init {
        Timber.d("init $this")
    }

    // from master viewmodel
    fun setItem(quizItem: QuizItem){
        Timber.d("check ref: ${quizItem.hashCode()}; id ${quizItem.questionId}")
        _item.value = quizItem
    }

    fun selectAnswer(answerVariantItem: AnswerVariantItem) {
        answerVariantItem.isChecked = !answerVariantItem.isChecked
        _item.value?.answerVariants?.let {
            it.find { it == answerVariantItem}?.isChecked = answerVariantItem.isChecked
        }
        _item.value = _item.value

        Timber.d("selectAnswer ${_item.value}\n $answerVariantItem")

    }

    fun checkAnswer() {
        _item.value?.let {
            val isRightAnswer = isQuizAnsweredRightUseCase(it)
            if (isRightAnswer) {
                _showToastSuccessEvent.value = Event(
                        getRandomMessageFromResources(
                                R.array.feedback_right_answer
                        )
                )
                it.commitButtonState = R.drawable.ic_sentiment_satisfied_black_24dp
                _commitButtonState.value = it.commitButtonState
                _playSoundEvent.value = Event(true)
            } else {
                _showToastFailureEvent.value = Event(
                        getRandomMessageFromResources(
                                R.array.feedback_wrong_answer
                        )
                )
                it.commitButtonState = R.drawable.ic_sentiment_dissatisfied_black_24dp
                _commitButtonState.value = it.commitButtonState
                _playSoundEvent.value = Event(false)
            }
            it.answered = true
            _lockInteraction.value = Unit
            Timber.d("checkAnswer: $it")
        }
    }

    private fun getRandomMessageFromResources(resourceId: Int): String {
        val variants = appContext.resources.getStringArray(resourceId)
        val pos = Random.nextInt(variants.size)
        return variants[pos]
    }

    fun handleThumpUpButton(analyticsData: AnalyticsData) {
        _item.value?.let {
            if (!it.questionIsEvaluated) {
                _showToastForEvaluationEvent.value = Event(R.string.thank_upvote)
                sendAnalyticsEvent(analyticsData)
                it.questionIsEvaluated = true
            } else {
                _showToastForEvaluationEvent.value = Event(R.string.already_voted)
            }
        }
    }

    fun handleThumpDownButton(analyticsData: AnalyticsData) {
        _item.value?.let {
            if (!it.questionIsEvaluated) {
                _showToastForEvaluationEvent.value = Event(R.string.report_was_sent)
                sendAnalyticsEvent(analyticsData)
                it.questionIsEvaluated = true
            } else {
                _showToastForEvaluationEvent.value = Event(R.string.already_voted)
            }
        }
    }

    fun isEvaluated(): Boolean =
            _item.value?.questionIsEvaluated ?: false

    private fun sendAnalyticsEvent(analyticsData: AnalyticsData) {
        if (!BuildConfig.DEBUG) {
            analyticsTracker.send(HitBuilders.EventBuilder()
                    .setCategory(analyticsData.category)
                    .setAction(analyticsData.action)
                    .setLabel(analyticsData.label)
                    .build())
        }
    }

    fun getDocRef(): String {
        return _item.value?.docLink ?: ""
    }

}