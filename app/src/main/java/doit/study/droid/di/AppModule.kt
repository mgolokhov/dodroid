package doit.study.droid.di

import android.app.Application
import androidx.room.Room
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.Tracker
import dagger.Module
import dagger.Provides
import doit.study.droid.R
import doit.study.droid.data.local.QuizDatabase
import javax.inject.Singleton

@Module
object AppModule {
    @Provides
    @Singleton
    fun provideAnalyticsTracker(appContext: Application): Tracker {
        val ga = GoogleAnalytics.getInstance(appContext)
        ga.enableAutoActivityReports(appContext)
        return ga.newTracker(R.xml.track_app)
    }

    @Singleton
    @Provides
    internal fun provideDatabase(appContext: Application): QuizDatabase {
        return Room.databaseBuilder(
                appContext.applicationContext,
                QuizDatabase::class.java,
                "quizDatabase.db")
                .fallbackToDestructiveMigration()
                .build()
    }
}