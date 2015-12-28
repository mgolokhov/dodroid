package doit.study.droid;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import doit.study.droid.sqlite.helper.DatabaseHelper;


public class TopicsActivity extends AppCompatActivity implements TagSelectionEventListener {
    @SuppressWarnings("unused")
    private final String TAG = "NSA " + getClass().getName();

    private List<Integer> mSelectedTagIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topics_layout);
        GlobalData gd = (GlobalData) getApplication();
        setTitle("Total questions: " + gd.getQuizData().getQuestionIds().size());
        List<Tag> tags = gd.getQuizData().getTags();
        Map<Integer, Tag.Stats> tagStats = gd.getQuizData().getTagStats();
        mSelectedTagIds = gd.getQuizData().getSelectedTagIds();

        RecyclerView rv = (RecyclerView) findViewById(R.id.topics_view);
        rv.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        rv.addItemDecoration(new DividerItemDecoration(this, R.drawable.divider));
        rv.setAdapter(new TopicAdapter(getApplicationContext(), this, tags, tagStats, mSelectedTagIds));
    }

    @Override
    public void onTagSelected(int tagId) {
        mSelectedTagIds.add(tagId);
    }

    @Override
    public void onTagUnselected(int tagId) {
        int index = mSelectedTagIds.indexOf(tagId);
        mSelectedTagIds.remove(index);
    }

    @Override
    public void onBackPressed() {
        GlobalData gd = (GlobalData) getApplication();
        gd.getQuizData().setSelectedTagIds(mSelectedTagIds);
        super.onBackPressed();
    }

    private static class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder>{

        DatabaseHelper mDBHelper;
        List<Tag> mTags;
        List<Integer> mInitialSelectedTagIds;
        TagSelectionEventListener mTagListener;
        Map<Integer, Tag.Stats> mTagStats;

        public TopicAdapter(Context context, TagSelectionEventListener listener, List<Tag> tags, Map<Integer, Tag.Stats> tagStats, List<Integer> selectedTagIds){
            mTagStats = tagStats;
            mDBHelper = new DatabaseHelper(context);
            mTags = tags;
            mInitialSelectedTagIds = selectedTagIds;
            mTagListener = listener;
        }

        public static class TopicViewHolder extends RecyclerView.ViewHolder{
            TextView topic;
            CheckBox checkbox;
            public TopicViewHolder(View itemView) {
                super(itemView);
                topic = (TextView)itemView.findViewById(R.id.topic_name);
                checkbox = (CheckBox) itemView.findViewById(R.id.checkbox_tag);
            }
        }

        @Override
        public int getItemCount() {
            return mTags.size();
        }

        @Override
        public TopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_item, parent, false);
            return new TopicViewHolder(v);
        }

        @Override
        public void onBindViewHolder(TopicViewHolder holder, int position) {
            final Tag tag = mTags.get(position);
            Tag.Stats stats = mTagStats.get(tag.getId());
            holder.topic.setText(tag.getName() + "(" + stats.getLearned() + "/" + stats.getQuestionsCount() + ")");
            holder.checkbox.setChecked(mInitialSelectedTagIds.contains(tag.getId()));
            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mTagListener.onTagSelected(tag.getId());
                    } else {
                        mTagListener.onTagUnselected(tag.getId());
                    }
                }
            });
        }
    }

    private static class DividerItemDecoration extends RecyclerView.ItemDecoration {

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

interface TagSelectionEventListener {
    void onTagSelected(int tagId);
    void onTagUnselected(int tagId);
}
