package doit.study.droid.topic

import androidx.recyclerview.widget.DiffUtil

class TopicsDiffCallback : DiffUtil.ItemCallback<TopicItem>() {
    override fun areItemsTheSame(oldItem: TopicItem, newItem: TopicItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TopicItem, newItem: TopicItem): Boolean {
        return oldItem == newItem
    }
}
