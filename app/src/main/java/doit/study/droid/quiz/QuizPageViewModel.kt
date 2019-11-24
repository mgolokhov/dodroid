package doit.study.droid.quiz

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import doit.study.droid.BuildConfig
import doit.study.droid.R
import doit.study.droid.data.local.QuizDatabase
import doit.study.droid.utils.AnalyticsData
import doit.study.droid.utils.Event
import timber.log.Timber
import javax.inject.Inject


class QuizPageViewModel @Inject constructor(
private val quizDatabase: QuizDatabase,
private val analyticsTracker: Tracker
) : ViewModel() {
    private val _item = MutableLiveData<QuizView>()
    val item: LiveData<QuizView> = _item

    private val _showToastForAnswer = MutableLiveData<Event<Int>>()
    val showToastForAnswer: LiveData<Event<Int>> = _showToastForAnswer

    private val _playSound = MutableLiveData<Event<Boolean>>()
    val playSound: LiveData<Event<Boolean>> = _playSound

    private val _commitButtonState = MutableLiveData<Int>()
    val commitButtonState: LiveData<Int> = _commitButtonState

    private val _lockInteraction = MutableLiveData<Unit>()
    val lockInteraction: LiveData<Unit> = _lockInteraction

    private val _showToastForEvaluation = MutableLiveData<Event<Int>>()
    val showToastForEvaluation: LiveData<Event<Int>> = _showToastForEvaluation

    init {
        Timber.d("init $this")
    }

    // from master viewmodel
    fun setItem(quizView: QuizView){
        Timber.d("check ref: ${quizView.hashCode()}; id ${quizView.questionId}")
        _item.value = quizView
    }


    fun saveCheckState(text: String, isChecked: Boolean) {
        _item.value?.selectedVariants?.let {
            if (isChecked) it.add(text)
            else it.remove(text)
        }
    }

    fun checkAnswer() {
        _item.value?.let {
            val isRightAnswer = (it.selectedVariants == it.rightVariants.toSet())
            if (isRightAnswer) {
                _showToastForAnswer.value = Event(R.array.feedback_right_answer)
                it.commitButtonState = R.drawable.ic_sentiment_satisfied_black_24dp
                _commitButtonState.value = it.commitButtonState
                _playSound.value = Event(true)
            } else {
                _showToastForAnswer.value = Event(R.array.feedback_wrong_answer)
                it.commitButtonState = R.drawable.ic_sentiment_dissatisfied_black_24dp
                _commitButtonState.value = it.commitButtonState
                _playSound.value = Event(false)
            }
            it.answered = true
            _lockInteraction.value = Unit
            Timber.d("checkAnswer: $it")
        }
    }

    fun handleThumpUpButton(analyticsData: AnalyticsData) {
        _item.value?.let {
            if (!it.questionIsEvaluated) {
                _showToastForEvaluation.value = Event(R.string.thank_upvote)
                sendAnalyticsEvent(analyticsData)
                it.questionIsEvaluated = true
            } else {
                _showToastForEvaluation.value = Event(R.string.already_voted)
            }
        }
    }

    fun handleThumpDownButton(analyticsData: AnalyticsData) {
        _item.value?.let {
            if (!it.questionIsEvaluated) {
                _showToastForEvaluation.value = Event(R.string.report_was_sent)
                sendAnalyticsEvent(analyticsData)
                it.questionIsEvaluated = true
            } else {
                _showToastForEvaluation.value = Event(R.string.already_voted)
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