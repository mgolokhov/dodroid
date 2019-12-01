package doit.study.droid.data.remote

import retrofit2.http.GET


interface QuizDataClient {
    @GET("quiz.json?cache_stub=drop")
    suspend fun quizData(): List<QuizData>

    @GET("config.json?cache_stub=drop")
    suspend fun configuration(): Configuration
}
