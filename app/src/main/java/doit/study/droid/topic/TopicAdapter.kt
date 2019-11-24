package doit.study.droid.topic

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import doit.study.droid.databinding.TopicItemBinding

class TopicAdapter(private val viewModel: TopicModelView):
        ListAdapter<TopicView, TopicAdapter.ViewHolder>(TopicsDiffCallback()){

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel, getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: TopicItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind (viewModel: TopicModelView, item: TopicView) {
            binding.viewmodel = viewModel
            binding.topic = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TopicItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }


}

