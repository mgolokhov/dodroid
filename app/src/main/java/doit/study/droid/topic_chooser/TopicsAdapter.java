package doit.study.droid.topic_chooser;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import doit.study.droid.R;
import timber.log.Timber;

public class TopicsAdapter extends RecyclerView.Adapter<TopicsAdapter.TopicViewHolder>{
    public interface OnItemClickListener {
        void onItemClick(TopicModel tag, boolean isChecked);
    }
    private final static boolean DEBUG = false;
    private List<TopicModel> mFilteredTags = new ArrayList<>();
    private List<TopicModel> mMasterCopyTags = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public TopicsAdapter(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public void animateTo(List<TopicModel> models) {
        if (DEBUG) Timber.d("Anim Filtered %d, current: %d", models.size(), mFilteredTags.size());
        applyAndAnimateRemovals(models);
        if (DEBUG) Timber.d("Rem Filtered %d, current: %d", models.size(), mFilteredTags.size());
        applyAndAnimateAdditions(models);
        if (DEBUG) Timber.d("Add Filtered %d, current: %d", models.size(), mFilteredTags.size());
        applyAndAnimateMovedItems(models);
        if (DEBUG) Timber.d("Move Filtered %d, current: %d", models.size(), mFilteredTags.size());
    }

    private void applyAndAnimateRemovals(List<TopicModel> newTags) {
        for (int i = mFilteredTags.size() - 1; i >= 0; i--){
            final TopicModel tag = mFilteredTags.get(i);
            if (!newTags.contains(tag)){
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<TopicModel> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final TopicModel model = newModels.get(i);
            if (!mFilteredTags.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<TopicModel> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final TopicModel model = newModels.get(toPosition);
            final int fromPosition = mFilteredTags.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public TopicModel removeItem(int position) {
        final TopicModel model = mFilteredTags.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
        return model;
    }

    public void addItem(int position, TopicModel model) {
        mFilteredTags.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final TopicModel model = mFilteredTags.remove(fromPosition);
        mFilteredTags.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    public static class TopicViewHolder extends RecyclerView.ViewHolder{
        TextView topic;
        CheckBox checkbox;
        public TopicViewHolder(View itemView) {
            super(itemView);
            topic = itemView.findViewById(R.id.topic_name);
            checkbox = itemView.findViewById(R.id.checkbox_tag);
        }

    }

    @Override
    public int getItemCount() {
        if (mFilteredTags != null)
            return mFilteredTags.size();
        else
            return 0;
    }


    public List<TopicModel> getTags(){
        return mMasterCopyTags;
    }


    public void setTags(List<TopicModel> tags){
        mMasterCopyTags = tags;
        mFilteredTags = new ArrayList<>(tags);
        notifyDataSetChanged();
    }

    @Override
    public TopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_topic_item, parent, false);
        return new TopicViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TopicViewHolder holder, int position) {
        final TopicModel tag = mFilteredTags.get(position);
        if (DEBUG) Timber.d("%s %d", tag, position);
        String text = String.format("%s (%d/%d)", tag.getText(), tag.getQuantity(), tag.getLearned());
        holder.topic.setText(text);
        holder.checkbox.setChecked(tag.isChecked());
        holder.checkbox.setOnClickListener(v -> {
            boolean isChecked = ((CheckBox)v).isChecked();
            onItemClickListener.onItemClick(tag, isChecked);
            if (DEBUG) Timber.d("change %s", tag);
        });
    }

}
