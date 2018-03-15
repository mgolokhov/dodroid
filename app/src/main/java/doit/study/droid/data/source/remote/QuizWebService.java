package doit.study.droid.data.source.remote;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.GET;

public interface QuizWebService {
    @GET("quiz.json?cache_stub=drop")
    Flowable<List<QuestionWeb>> getQuestions();
}
