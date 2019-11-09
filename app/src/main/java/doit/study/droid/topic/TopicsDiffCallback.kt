package doit.study.droid.adapters

import androidx.recyclerview.widget.DiffUtil
import doit.study.droid.topic.TopicView


class TopicsDiffCallback : DiffUtil.ItemCallback<TopicView>() {
    override fun areItemsTheSame(oldItem: TopicView, newItem: TopicView): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TopicView, newItem: TopicView): Boolean {
        return oldItem == newItem
    }
}