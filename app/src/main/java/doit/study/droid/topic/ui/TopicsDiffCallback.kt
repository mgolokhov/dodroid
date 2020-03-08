package doit.study.droid.topic.ui

import androidx.recyclerview.widget.DiffUtil
import doit.study.droid.topic.TopicItem

class TopicsDiffCallback : DiffUtil.ItemCallback<TopicItem>() {
    override fun areItemsTheSame(oldItem: TopicItem, newItem: TopicItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TopicItem, newItem: TopicItem): Boolean {
        return oldItem == newItem
    }
}
