package doit.study.droid;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;


// Entry point for the app.
// Because we set in manifest action=MAIN category=LAUNCHER
public class MainActivity extends AppCompatActivity {
    // Shia LaBeouf - Just Do it! (Auto-tuned)
    private final String URL = "http://www.youtube.com/watch?v=gJscrxxl_Bg";
    @SuppressWarnings("unused")
    private final String TAG = "NSA " + getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void motivationButton(View v){
        Intent motivIntent = new Intent(Intent.ACTION_VIEW);
        motivIntent.setData(Uri.parse(URL));
        startActivity(motivIntent);
    }

    public void setTopicButton(View v){
        Intent intent = new Intent(MainActivity.this, TopicsActivity.class);
        GlobalData globalData = (GlobalData) getApplication();
        QuizData quizData = globalData.getQuizData();
        globalData.save("selectedTagIds", quizData.getSelectedTagIds());
        globalData.save("tags", quizData.getTags());
        globalData.save("tagStats", quizData.getTagStats());
        startActivity(intent);
    }

    public void doitButton(View v){
        Intent intent = new Intent(MainActivity.this, QuestionsActivity.class);
        GlobalData globalData = (GlobalData) getApplication();
        QuizData quizData = globalData.getQuizData();
        globalData.save("questionIds", quizData.getQuestionIdsToWorkWith());
        startActivity(intent);
    }
}
