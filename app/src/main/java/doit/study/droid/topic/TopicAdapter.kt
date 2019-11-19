package doit.study.droid.topic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import doit.study.droid.R

class TopicAdapter(private val clickListener: (TopicView, Boolean) -> Unit):
        ListAdapter<TopicView, TopicAdapter.ViewHolder>(TopicsDiffCallback()){


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_topic_item, parent, false)
        return ViewHolder(v)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var topic: TextView = itemView.findViewById(R.id.topic_name)
        private var checkbox: CheckBox = itemView.findViewById(R.id.checkbox_tag)

        fun bind(tag: TopicView, clickListener: (TopicView, Boolean) -> Unit) {
            topic.text = "${tag.name} (${tag.counterTotal}/${tag.counterStudied})"
            checkbox.isChecked = tag.selected
            checkbox.setOnClickListener {
                clickListener(tag, (it as CheckBox).isChecked)
            }

        }
    }

}