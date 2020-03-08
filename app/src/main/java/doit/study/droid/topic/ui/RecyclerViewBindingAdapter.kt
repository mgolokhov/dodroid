package doit.study.droid.topic.ui

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import doit.study.droid.topic.TopicItem

@BindingAdapter("items")
fun setItems(listView: RecyclerView, items: List<TopicItem>) {
    listView.adapter?.let { it as TopicAdapter
        it.submitList(items)
    }
}
