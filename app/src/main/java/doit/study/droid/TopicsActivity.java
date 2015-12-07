package doit.study.droid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class TopicsActivity extends AppCompatActivity {
    @SuppressWarnings("unused")
    private final String LOG_TAG = "NSA " + getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topics_layout);
        RecyclerView rv = (RecyclerView) findViewById(R.id.topics_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        GlobalData gd = (GlobalData) getApplication();
        rv.setAdapter(new TopicAdapter(gd.getQuizData()));
    }

    private static class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder>{
        QuizData mQuizData;
        public TopicAdapter(QuizData quizData){
            mQuizData = quizData;
        }

        public static class TopicViewHolder extends RecyclerView.ViewHolder{
            TextView topic;
            public TopicViewHolder(View itemView) {
                super(itemView);
                topic = (TextView)itemView.findViewById(R.id.topic_name);
            }
        }

        @Override
        public int getItemCount() {
            return mQuizData.size();
        }

        @Override
        public TopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_item, parent, false);
            return new TopicViewHolder(v);
        }

        @Override
        public void onBindViewHolder(TopicViewHolder holder, int position) {
            holder.topic.setText(mQuizData.getById(position).getText());
        }
    }
}
