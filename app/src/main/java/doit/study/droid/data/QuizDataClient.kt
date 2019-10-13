package doit.study.droid.data

import retrofit2.Call
import retrofit2.http.GET

interface QuizDataClient {
    @GET("quiz.json?cache_stub=drop")
    suspend fun questions(): List<Question>
    @GET("config?cache_stub=drop")
    suspend fun configuration(): Configuration
}
