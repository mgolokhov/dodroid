package doit.study.droid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.List;

import doit.study.droid.R;
import doit.study.droid.app.App;
import doit.study.droid.data.Question;
import doit.study.droid.data.QuizDBHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ((App)getApplication()).getQuizService().getQuestions().enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                try {
                    QuizDBHelper helper = new QuizDBHelper(getBaseContext());
                    helper.insertFromFile(response.body(), helper.getWritableDatabase());
                    helper.close();

                    Intent intent = new Intent(SplashActivity.this, TopicsChooserActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e){
                    handleErrCannotLoadQuiz(e);
                }
            }

            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                handleErrCannotLoadQuiz(t);
            }


            private void handleErrCannotLoadQuiz(Throwable t){
                Toast.makeText(SplashActivity.this, R.string.failed_to_load_quiz, Toast.LENGTH_SHORT).show();
                Timber.e(t);
                finish();
            }
        });



    }
}
