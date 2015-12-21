//package doit.study.droid;
//
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.graphics.Canvas;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.StaggeredGridLayoutManager;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//
//public class TopicsActivity extends AppCompatActivity {
//    @SuppressWarnings("unused")
//    private final String TAG = "NSA " + getClass().getName();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.topics_layout);
//        RecyclerView rv = (RecyclerView) findViewById(R.id.topics_view);
//        rv.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
//        rv.addItemDecoration(new DividerItemDecoration(this, R.drawable.divider));
//        QuizData quizData = (QuizData) getApplication();
//
//        rv.setAdapter(new TopicAdapter(quizData);
//    }
//
//    private static class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder>{
//        QuizData mQuizData;
//        public TopicAdapter(QuizData quizData){
//            mQuizData = quizData;
//        }
//
//        public static class TopicViewHolder extends RecyclerView.ViewHolder{
//            TextView topic;
//            public TopicViewHolder(View itemView) {
//                super(itemView);
//                topic = (TextView)itemView.findViewById(R.id.topic_name);
//            }
//        }
//
//        @Override
//        public int getItemCount() {
//            return mQuizData.size();
//        }
//
//        @Override
//        public TopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_item, parent, false);
//            return new TopicViewHolder(v);
//        }
//
//        @Override
//        public void onBindViewHolder(TopicViewHolder holder, int position) {
//            holder.topic.setText(mQuizData.getById(mQuizData.idAtPosition(position)).getText());
//        }
//    }
//
//    private static class DividerItemDecoration extends RecyclerView.ItemDecoration {
//
//        private final int[] ATTRS = new int[]{android.R.attr.listDivider};
//
//        private Drawable mDivider;
//
//        /**
//         * Default divider will be used
//         */
//        public DividerItemDecoration(Context context) {
//            final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
//            mDivider = styledAttributes.getDrawable(0);
//            styledAttributes.recycle();
//        }
//
//        /**
//         * Custom divider will be used
//         */
//        public DividerItemDecoration(Context context, int resId) {
//            mDivider = ContextCompat.getDrawable(context, resId);
//        }
//
//        @Override
//        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
//            int left = parent.getPaddingLeft();
//            int right = parent.getWidth() - parent.getPaddingRight();
//
//            int childCount = parent.getChildCount();
//            for (int i = 0; i < childCount; i++) {
//                View child = parent.getChildAt(i);
//
//                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
//
//                int top = child.getBottom() + params.bottomMargin;
//                int bottom = top + mDivider.getIntrinsicHeight();
//
//                mDivider.setBounds(left, top, right, bottom);
//                mDivider.draw(c);
//            }
//        }
//    }
//}
