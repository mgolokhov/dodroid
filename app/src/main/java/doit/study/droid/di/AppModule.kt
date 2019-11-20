package doit.study.droid.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.Tracker
import dagger.Module
import dagger.Provides
import doit.study.droid.R
import doit.study.droid.data.local.QuizDatabase
import javax.inject.Singleton

@Module
class AppModule(private val appContext: Application) {

    @Provides
    @Singleton
    fun provideApplication(): Application {
        return appContext
    }

    @Provides
    @Singleton
    fun provideAppContext(): Context {
        return appContext
    }

    @Provides
    @Singleton
    fun provideAnalyticsTracker(): Tracker {
        val ga = GoogleAnalytics.getInstance(appContext)
        ga.enableAutoActivityReports(appContext)
        return ga.newTracker(R.xml.track_app)
    }

    @Singleton
    @Provides
    internal fun provideDatabase(context: Context): QuizDatabase {
        return Room.databaseBuilder(
                context.applicationContext,
                QuizDatabase::class.java,
                "quizDatabase.db")
                .fallbackToDestructiveMigration()
                .build()
    }
}