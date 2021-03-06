package doit.study.droid.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import doit.study.droid.BuildConfig
import doit.study.droid.data.local.preferences.SslPinning
import doit.study.droid.data.remote.QuizDataClient
import java.net.URI
import javax.inject.Singleton
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        return gsonBuilder.create()
    }

    @Provides
    @Singleton
    fun provideCertPinning(sslPinning: SslPinning): CertificatePinner {
        val certBuilder = CertificatePinner.Builder()
        if (sslPinning.isEnabled()) {
            certBuilder.add(URI(BASE_URL).host, SUBJECT_PUBLIC_KEY_INFO)
        }
        return certBuilder.build()
    }

    private fun createLogInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        return logging.apply { logging.level = HttpLoggingInterceptor.Level.BODY }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(certificatePinner: CertificatePinner): OkHttpClient {
        val client = OkHttpClient.Builder()
                .certificatePinner(certificatePinner)
        if (BuildConfig.DEBUG) {
            client.addInterceptor(createLogInterceptor())
        }
        return client.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
    }

    @Provides
    @Singleton
    fun provideServerApi(retrofit: Retrofit): QuizDataClient {
        return retrofit.create(QuizDataClient::class.java)
    }

    private const val BASE_URL = "https://dodroid-6f241.web.app/"
    // note: that Certificate key is valid until Mon, 26 Oct 2020
    private const val SUBJECT_PUBLIC_KEY_INFO = "sha256/fm0SEuAdUu/JvjeuKT5rUTGp5XibsNski/y43V5JSY8="
}
