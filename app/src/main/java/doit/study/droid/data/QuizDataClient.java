package doit.study.droid.data;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface QuizDataClient {
    @GET("quiz.json?cache_stub=drop")
    Call<List<Question>> getQuestions();
    @GET("config?cache_stub=drop")
    Call<Configuration> getConfiguration();
}
