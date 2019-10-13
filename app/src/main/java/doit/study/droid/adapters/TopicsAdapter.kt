package doit.study.droid.adapters

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

import java.util.ArrayList

import doit.study.droid.R
import doit.study.droid.data.Tag
import timber.log.Timber

class TopicsAdapter : RecyclerView.Adapter<TopicsAdapter.TopicViewHolder>() {
    private var filteredTags: MutableList<Tag>? = null
    var tags: List<Tag>? = null
        set(tags) {
            field = tags
            filteredTags = ArrayList(tags)
            notifyDataSetChanged()
        }


    fun animateTo(models: List<Tag>) {
        if (DEBUG) Timber.d("Anim Filtered %d, current: %d", models.size, filteredTags!!.size)
        applyAndAnimateRemovals(models)
        if (DEBUG) Timber.d("Rem Filtered %d, current: %d", models.size, filteredTags!!.size)
        applyAndAnimateAdditions(models)
        if (DEBUG) Timber.d("Add Filtered %d, current: %d", models.size, filteredTags!!.size)
        applyAndAnimateMovedItems(models)
        if (DEBUG) Timber.d("Move Filtered %d, current: %d", models.size, filteredTags!!.size)
    }

    private fun applyAndAnimateRemovals(newTags: List<Tag>) {
        for (i in filteredTags!!.indices.reversed()) {
            val tag = filteredTags!![i]
            if (!newTags.contains(tag)) {
                removeItem(i)
            }
        }
    }

    private fun applyAndAnimateAdditions(newModels: List<Tag>) {
        var i = 0
        val count = newModels.size
        while (i < count) {
            val model = newModels[i]
            if (!filteredTags!!.contains(model)) {
                addItem(i, model)
            }
            i++
        }
    }

    private fun applyAndAnimateMovedItems(newModels: List<Tag>) {
        for (toPosition in newModels.indices.reversed()) {
            val model = newModels[toPosition]
            val fromPosition = filteredTags!!.indexOf(model)
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition)
            }
        }
    }

    fun removeItem(position: Int): Tag {
        val model = filteredTags!!.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
        return model
    }

    fun addItem(position: Int, model: Tag) {
        filteredTags!!.add(position, model)
        notifyItemInserted(position)
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        val model = filteredTags!!.removeAt(fromPosition)
        filteredTags!!.add(toPosition, model)
        notifyItemMoved(fromPosition, toPosition)
    }

    class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var topic: TextView = itemView.findViewById(R.id.topic_name)
        internal var checkbox: CheckBox = itemView.findViewById(R.id.checkbox_tag)
    }

    override fun getItemCount(): Int = filteredTags?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_topic_item, parent, false)
        return TopicViewHolder(v)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        val tag = filteredTags!![position]
        if (DEBUG) Timber.d("%s %d", tag, position)
        val text = String.format("%s (%d/%d)", tag.name, tag.questionsCounter, tag.questionsStudied)
        holder.topic.text = text
        holder.checkbox.isChecked = tag.selectionStatus
        holder.checkbox.setOnClickListener { v ->
            val isChecked = (v as CheckBox).isChecked
            tag.setChecked(isChecked)
            // synchronize with all tags
            for (t in tags!!) {
                if (t.id == tag.id) {
                    t.setChecked(isChecked)
                }
            }
            if (DEBUG) Timber.d("change %s", tag)
        }
    }


    companion object {
        private const val DEBUG = false
    }
}
