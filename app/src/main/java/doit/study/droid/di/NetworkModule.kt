package doit.study.droid.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import doit.study.droid.BuildConfig
import doit.study.droid.data.remote.QuizDataClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    internal fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        return gsonBuilder.create()
    }

    private fun createLogInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        val level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        return logging.apply { logging.level = level }
    }

    @Provides
    @Singleton
    internal fun provideOkHttpClient(): OkHttpClient {
        val client = OkHttpClient.Builder()
        client.addInterceptor(createLogInterceptor());
        return client.build()
    }

    @Provides
    @Singleton
    internal fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
    }

    @Provides
    @Singleton
    internal fun provideServerApi(retrofit: Retrofit): QuizDataClient {
        return retrofit.create(QuizDataClient::class.java)
    }

    companion object {
        private const val BASE_URL = "https://raw.githubusercontent.com/mgolokhov/dodroid_questions/master/"
    }


}