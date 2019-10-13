package doit.study.droid.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import doit.study.droid.R
import doit.study.droid.data.Tag

class TopicsAdapter(private val clickListener: (Tag) -> Unit):
        ListAdapter<Tag, TopicsAdapter.ViewHolder>(TopicsDiffCallback()){


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

        fun bind(tag: Tag, clickListener: (Tag) -> Unit) {
            topic.text = "${tag.name} (${tag.questionsCounter}/${tag.questionsStudied})"
            checkbox.isChecked = tag.selectionStatus
            checkbox.setOnClickListener {
                clickListener(tag)
            }

        }
    }

}