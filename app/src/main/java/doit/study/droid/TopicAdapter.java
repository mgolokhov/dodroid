package doit.study.droid;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import doit.study.droid.data.Tag;
import timber.log.Timber;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder>{
    private final static boolean DEBUG = false;
    private List<Tag> mTags;

    public static class TopicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView topic;
        CheckBox checkbox;
        public TopicViewHolder(View itemView) {
            super(itemView);
            topic = (TextView)itemView.findViewById(R.id.topic_name);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox_tag);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//                Toast.makeText(v.getContext(), topic.getText(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        if (mTags != null)
            return mTags.size();
        else
            return 0;
    }


    public List<Tag> getTags(){
        return mTags;
    }

    public void setTags(List<Tag> tags){
        mTags = tags;
        notifyDataSetChanged();
    }

    @Override
    public TopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_item, parent, false);
        return new TopicViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TopicViewHolder holder, int position) {
        final Tag tag = mTags.get(position);
        if (DEBUG) Timber.d("%s %d", tag, position);
        String text = String.format("%s (%d/%d)", tag.getName(), tag.getQuestionsCounter(), tag.getQuestionsStudied());
        holder.topic.setText(text);
        holder.checkbox.setChecked(tag.getSelectionStatus());
        holder.checkbox.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            tag.setChecked(((CheckBox)v).isChecked());
            if (DEBUG) Timber.d("change %s", tag);
        }
    });
    }


    public static class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private final int[] ATTRS = new int[]{android.R.attr.listDivider};

        private Drawable mDivider;

        /**
         * Default divider will be used
         */
        public DividerItemDecoration(Context context) {
            final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
            mDivider = styledAttributes.getDrawable(0);
            styledAttributes.recycle();
        }

        /**
         * Custom divider will be used
         */
        public DividerItemDecoration(Context context, int resId) {
            mDivider = ContextCompat.getDrawable(context, resId);
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}
