package doit.study.droid.topic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import doit.study.droid.data.local.QuizDatabase
import doit.study.droid.data.local.entity.Tag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class TopicModelView @Inject constructor(
        private val quizDatabase: QuizDatabase
) : ViewModel() {
    private val _items = MutableLiveData<List<TopicView>>().apply { value = emptyList() }
    val items: LiveData<List<TopicView>> = _items

    init {
        loadTopics()
    }

    fun loadTopics(query: String = "") {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                quizDatabase.questionDao().getQuestions()
                val tags = quizDatabase.tagDao().getTags()
                val topics = ArrayList<TopicView>()
                tags.filter { it.name.contains(query, ignoreCase = true) }.forEach{tag ->
                    val questions = quizDatabase.questionDao().getQuestionsByTag(tag.name)
                    questions.filter { it.studiedAt != 0L }.size
                    topics.add(TopicView(
                            id = tag.id,
                            name = tag.name,
                            counterTotal = questions.size,
                            counterStudied = questions.filter { it.studiedAt != 0L }.size,
                            selected = tag.selected
                            )
                    )

                }
                _items.postValue(topics)
                Timber.d("post values ${topics.size}")
            }

        }
    }

    private fun saveSelectedTags(vararg topics: TopicView, isSelected: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val tags = topics.map {
                    Tag(
                            id = it.id,
                            name = it.name,
                            selected = isSelected
                    )
                }
                val res = quizDatabase.tagDao().insertOrReplaceTag(*tags.toTypedArray())
                Timber.d("insertOrReplaceTag $res")
            }
        }
        // TODO: optimize
        loadTopics()
    }


    fun selectTopic(topic: TopicView, isSelected: Boolean) {
        saveSelectedTags(topic, isSelected = isSelected)
    }

    fun selectAllTopics() = allTopics(select = true)

    fun deselectAllTopics() = allTopics(select = false)

    private fun allTopics(select: Boolean) {
        _items.value?.let { topicView ->
            val topics = topicView.map {
                it.copy (
                        selected = select
                )
            }
            saveSelectedTags(*topics.toTypedArray(), isSelected = select)
            _items.value = topics
        }

    }
}