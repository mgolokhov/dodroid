package doit.study.droid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import doit.study.droid.R;
import doit.study.droid.app.App;
import doit.study.droid.data.Question;
import doit.study.droid.data.QuestionNetwork;
import doit.study.droid.data.QuestionTagStatistics;
import doit.study.droid.data.QuizDatabase;
import doit.study.droid.data.Statistic;
import doit.study.droid.data.Tag;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ((App)getApplication()).getQuizService().getQuestions().enqueue(new Callback<List<QuestionNetwork>>() {
            @Override
            public void onResponse(Call<List<QuestionNetwork>> call, Response<List<QuestionNetwork>> response) {
                try {
                    Timber.d("ok "+response);
                    QuizDatabase quizDatabase = QuizDatabase.getInstance(getApplicationContext());
                    for (QuestionNetwork questionNetwork: response.body()) {
                        quizDatabase.questionDao().insert(new Question(
                                questionNetwork.mId,
                                questionNetwork.mText,
                                questionNetwork.mWrongAnswers,
                                questionNetwork.mRightAnswers,
                                questionNetwork.mDocRef)
                        );
                        quizDatabase.tagDao().insert(new Tag(
                                        questionNetwork.mTags,
                                        questionNetwork.mId
                                )
                        );


                        quizDatabase.statisticsDao().insert(new Statistic(
                                questionNetwork.mId,
                                "0",
                                "0",
                                "0",
                                "0",
                                "0"
                        ));
                    }

                    List<QuestionTagStatistics> q = quizDatabase.questionTagStatisticsDao().getQuestionTagStatistics();
                    Timber.d("done");


//                    Intent intent = new Intent(SplashActivity.this, TopicsChooserActivity.class);
//                    startActivity(intent);
//                    finish();
                } catch (Exception e){
                    handleErrCannotLoadQuiz(e);
                }
            }

            @Override
            public void onFailure(Call<List<QuestionNetwork>> call, Throwable t) {
                handleErrCannotLoadQuiz(t);
            }


            private void handleErrCannotLoadQuiz(Throwable t){
                Toast.makeText(SplashActivity.this, R.string.failed_to_load_quiz, Toast.LENGTH_SHORT).show();
                Timber.e(t);
                finish();
            }

            private String listToString(List<String> data){
                StringBuilder sb = new StringBuilder();
                for(String d: data)
                    sb.append(d).append("\n");
                return sb.toString();
            }
        });



    }
}
