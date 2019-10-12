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
        internal var topic: TextView
        internal var checkbox: CheckBox

        init {
            topic = itemView.findViewById<View>(R.id.topic_name) as TextView
            checkbox = itemView.findViewById<View>(R.id.checkbox_tag) as CheckBox
        }

    }

    override fun getItemCount(): Int {
        return if (filteredTags != null)
            filteredTags!!.size
        else
            0
    }

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


    class DividerItemDecoration : RecyclerView.ItemDecoration {

        private val ATTRS = intArrayOf(android.R.attr.listDivider)

        private var divider: Drawable? = null

        /**
         * Default divider will be used
         */
        constructor(context: Context) {
            val styledAttributes = context.obtainStyledAttributes(ATTRS)
            divider = styledAttributes.getDrawable(0)
            styledAttributes.recycle()
        }

        /**
         * Custom divider will be used
         */
        constructor(context: Context, resId: Int) {
            divider = ContextCompat.getDrawable(context, resId)
        }

        override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            val left = parent.paddingLeft
            val right = parent.width - parent.paddingRight

            val childCount = parent.childCount
            for (i in 0 until childCount) {
                val child = parent.getChildAt(i)

                val params = child.layoutParams as RecyclerView.LayoutParams

                val top = child.bottom + params.bottomMargin
                val bottom = top + divider!!.intrinsicHeight

                divider!!.setBounds(left, top, right, bottom)
                divider!!.draw(c)
            }
        }
    }

    companion object {
        private const val DEBUG = false
    }
}
