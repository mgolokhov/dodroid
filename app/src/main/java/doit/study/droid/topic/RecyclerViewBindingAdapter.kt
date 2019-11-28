package doit.study.droid.topic

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView

@BindingAdapter("app:items")
fun setItems(listView: RecyclerView, items: List<TopicItem>) {
    listView.adapter?.let { it as TopicAdapter
        it.submitList(items)
    }
}