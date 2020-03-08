package doit.study.droid.quiz.ui

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import doit.study.droid.BuildConfig
import doit.study.droid.R
import doit.study.droid.data.Outcome
import doit.study.droid.data.local.preferences.Sound
import doit.study.droid.quiz.AnswerVariantItem
import doit.study.droid.quiz.QuizItem
import doit.study.droid.quiz.domain.GetSoundFileForFailureUseCase
import doit.study.droid.quiz.domain.GetSoundFileForSuccessUseCase
import doit.study.droid.quiz.domain.IsQuizAnsweredRightUseCase
import doit.study.droid.utils.AnalyticsData
import doit.study.droid.utils.Event
import javax.inject.Inject
import kotlin.random.Random
import timber.log.Timber

class QuizPageViewModel @Inject constructor(
    private val appContext: Application,
    private val analyticsTracker: Tracker,
    private val soundPreferences: Sound,
    private val getSoundFileForFailureUseCase: GetSoundFileForFailureUseCase,
    private val getSoundFileForSuccessUseCase: GetSoundFileForSuccessUseCase,
    private val isQuizAnsweredRightUseCase: IsQuizAnsweredRightUseCase
) : ViewModel() {
    private val _item = MutableLiveData<QuizItem>()
    val item: LiveData<QuizItem> = _item

    private val _showToastSuccessEvent = MutableLiveData<Event<String>>()
    val showToastSuccessEvent: LiveData<Event<String>> = _showToastSuccessEvent

    private val _showToastFailureEvent = MutableLiveData<Event<String>>()
    val showToastFailureEvent: LiveData<Event<String>> = _showToastFailureEvent

    private val _playSoundEvent = MutableLiveData<Event<String>>()
    val playSoundEvent: LiveData<Event<String>> = _playSoundEvent

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
    fun setItem(quizItem: QuizItem) {
        Timber.d("check ref: ${quizItem.hashCode()}; id ${quizItem.questionId}")
        _item.value = quizItem
    }

    fun selectAnswer(answerVariantItem: AnswerVariantItem) {
        answerVariantItem.isChecked = !answerVariantItem.isChecked
        _item.value?.answerVariants?.let {
            it.find { it == answerVariantItem }?.isChecked = answerVariantItem.isChecked
        }
        _item.value = _item.value

        Timber.d("selectAnswer ${_item.value}\n $answerVariantItem")
    }

    fun checkAnswer() {
        _item.value?.let {
            if (isQuizAnsweredRightUseCase(it)) {
                handleRightAnswer(it)
            } else {
                handleWrongAnswer(it)
            }
            it.answered = true
            _lockInteraction.value = Unit
            Timber.d("checkAnswer: $it")
        }
    }

    private fun handleRightAnswer(quizItem: QuizItem) {
        _showToastSuccessEvent.value = Event(
                getRandomMessageFromResources(
                        R.array.feedback_right_answer
                )
        )
        quizItem.commitButtonState = R.drawable.ic_sentiment_satisfied_black_24dp
        _commitButtonState.value = quizItem.commitButtonState
        if (soundPreferences.isEnabled()) {
            val res = getSoundFileForSuccessUseCase()
            if (res is Outcome.Success) {
                _playSoundEvent.value = Event(res.data)
            }
        }
    }

    private fun handleWrongAnswer(quizItem: QuizItem) {
        _showToastFailureEvent.value = Event(
                getRandomMessageFromResources(
                        R.array.feedback_wrong_answer
                )
        )
        quizItem.commitButtonState = R.drawable.ic_sentiment_dissatisfied_black_24dp
        _commitButtonState.value = quizItem.commitButtonState
        if (soundPreferences.isEnabled()) {
            val res = getSoundFileForFailureUseCase()
            if (res is Outcome.Success) {
                _playSoundEvent.value = Event(res.data)
            }
        }
    }

    private fun getRandomMessageFromResources(resourceId: Int): String {
        val variants = appContext.resources.getStringArray(resourceId)
        val pos = Random.nextInt(variants.size)
        return variants[pos]
    }

    fun handleThumbUpButton(analyticsData: AnalyticsData) {
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

    fun handleThumbDownButton(analyticsData: AnalyticsData) {
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
