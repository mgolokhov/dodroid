package doit.study.droid;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import doit.study.droid.model.GlobalData;
import doit.study.droid.model.QuizData;
import doit.study.droid.model.Tag;


public class TopicsActivity extends AppCompatActivity{
    @SuppressWarnings("unused")
    private final String TAG = "NSA " + getClass().getName();
    private QuizData mQuizData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topics_layout);

        GlobalData gd = (GlobalData) getApplication();
        mQuizData = gd.getQuizData();
        setTitle("Total questions: " + mQuizData.getQuestionIds().size());
        List<Integer> tagIds = gd.getQuizData().getTagIds();

        RecyclerView rv = (RecyclerView) findViewById(R.id.topics_view);
        rv.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        rv.addItemDecoration(new DividerItemDecoration(this, R.drawable.divider));
        rv.setAdapter(new TopicAdapter(tagIds, mQuizData));
    }


    private static class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder>{

        List<Integer> mTagIds;
        QuizData mQuizData;

        public TopicAdapter(List<Integer> tagIds, QuizData quizData){
            mTagIds = tagIds;
            mQuizData = quizData;
        }

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
                Toast.makeText(v.getContext(), topic.getText(), Toast.LENGTH_SHORT);
            }
        }

        @Override
        public int getItemCount() {
            return mTagIds.size();
        }

        @Override
        public TopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_item, parent, false);
            return new TopicViewHolder(v);
        }

        @Override
        public void onBindViewHolder(TopicViewHolder holder, int position) {
            final Tag tag = mQuizData.getTagById(mTagIds.get(position));
            String text = String.format("%s (%d/%d)", tag.getName(), tag.getQuestionsCounter(), tag.getQuestionsStudied());
            holder.topic.setText(text);
            holder.checkbox.setChecked(tag.getSelectionStatus());
            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tag.select();
                    mQuizData.setTagSelection(tag);
                } else {
                    tag.unselect();
                    mQuizData.setTagSelection(tag);
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

