package doit.study.droid.splash

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import doit.study.droid.R
import doit.study.droid.data.Outcome
import doit.study.droid.data.local.QuizContentVersion
import doit.study.droid.data.local.QuizDatabase
import doit.study.droid.data.local.entity.Question
import doit.study.droid.data.local.entity.QuestionTagJoin
import doit.study.droid.data.local.entity.Tag
import doit.study.droid.data.remote.Configuration
import doit.study.droid.data.remote.QuizData
import doit.study.droid.data.remote.QuizDataClient
import doit.study.droid.domain.SyncWithServerUseCase
import doit.study.droid.utils.Event
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

class SplashViewModel @Inject constructor(
        private val appContext: Application,
        private val syncWithServerUseCase: SyncWithServerUseCase
) : ViewModel() {

    private val _navigateToTopicsEvent = MutableLiveData<Event<Unit>>()
    val navigateToTopicsEvent: LiveData<Event<Unit>> = _navigateToTopicsEvent

    private val _showErrorAndExitEvent = MutableLiveData<Event<String>>()
    val showErrorAndExitEvent: LiveData<Event<String>> = _showErrorAndExitEvent

    fun syncWithServer() = viewModelScope.launch {
        when (syncWithServerUseCase()) {
            is Outcome.Success -> {
                _navigateToTopicsEvent.value = Event(Unit)
            }
            is Outcome.Error -> {
                _showErrorAndExitEvent.value = Event(appContext.getString(R.string.error_to_sync_try_again_later))
            }
        }
    }
}