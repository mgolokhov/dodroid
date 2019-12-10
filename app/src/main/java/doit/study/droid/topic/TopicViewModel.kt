package doit.study.droid.topic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import doit.study.droid.domain.GetTopicItemsUseCase
import doit.study.droid.domain.SaveTopicItemsUseCase
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class TopicViewModel @Inject constructor(
        private val getTopicItemsUseCase: GetTopicItemsUseCase,
        private val saveTopicItemsUseCase: SaveTopicItemsUseCase
) : ViewModel() {
    private val _items = MutableLiveData<List<TopicItem>>().apply { value = emptyList() }
    val items: LiveData<List<TopicItem>> = _items

    init {
        loadTopics()
    }

    fun loadTopics(
            query: String = ""
    ) = viewModelScope.launch {
        _items.value = getTopicItemsUseCase(query)
        Timber.d("post values ${_items.value?.size}")
    }

    fun selectTopic(
            topicItem: TopicItem,
            selected: Boolean
    ) = viewModelScope.launch {
        saveTopicItemsUseCase(topicItem, selected = selected)
        loadTopics()
    }

    fun selectAllTopics() = allTopics(selected = true)

    fun deselectAllTopics() = allTopics(selected = false)

    private fun allTopics(
            selected: Boolean
    ) = viewModelScope.launch {
        _items.value?.let { topicView ->
            val topics = topicView.map {
                it.copy(selected = selected)
            }
            saveTopicItemsUseCase(*topics.toTypedArray(), selected = selected)
            loadTopics()
        }
    }
}