package doit.study.droid.topic

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

@BindingAdapter("app:items")
fun setItems(listView: RecyclerView, items: List<TopicView>) {
    listView.adapter?.let { it as TopicAdapter
        it.submitList(items)
    }
}