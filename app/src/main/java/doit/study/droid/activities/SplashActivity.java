package doit.study.droid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.Arrays;

import javax.inject.Inject;

import doit.study.droid.R;
import doit.study.droid.app.App;
import doit.study.droid.data.source.QuestionsRepository;
import doit.study.droid.data.source.local.QuizDatabase;
import doit.study.droid.topic_chooser.TopicsChooserActivity;
import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class SplashActivity extends AppCompatActivity {
    @Inject
    QuestionsRepository questionsRepository;
    @Inject
    QuizDatabase quizDatabase;
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);

        disposable = Maybe.concat(questionsRepository.populateDb().toMaybe(), quizDatabase.getQuizDao().getTagStatistics())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // onNext
                        res -> {
                            Timber.d("result " + Arrays.toString(res.toArray()));
                            navigateToTopicChooser();
                        },
//                        // onComplete
//                        () -> {},
                        // onError
                        this::handleErrCannotLoadQuiz
                )
        ;


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) disposable.dispose();
    }

    private void navigateToTopicChooser(){
        Intent intent = new Intent(SplashActivity.this, TopicsChooserActivity.class);
        startActivity(intent);
        finish();
    }

    private void handleErrCannotLoadQuiz(Throwable t){
        Toast.makeText(SplashActivity.this, R.string.failed_to_load_quiz, Toast.LENGTH_SHORT).show();
        Timber.e(t);
        finish();
    }
}
