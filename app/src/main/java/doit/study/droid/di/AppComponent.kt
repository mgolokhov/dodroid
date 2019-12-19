package doit.study.droid.di

import android.app.Application
import com.google.android.gms.analytics.Tracker
import dagger.BindsInstance
import dagger.Component
import doit.study.droid.quiz.QuizMainFragment
import doit.study.droid.quiz.QuizPageFragment
import doit.study.droid.splash.SplashActivity
import doit.study.droid.topic.TopicFragment
import javax.inject.Singleton

@Singleton
@Component(modules = [ViewModelModule::class, NetworkModule::class, AppModule::class])
interface AppComponent {
    fun inject(activity: SplashActivity)
    fun inject(fragment: TopicFragment)
    fun inject(fragment: QuizPageFragment)
    fun inject(fragment: QuizMainFragment)
    val analyticsTracker: Tracker

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance appContext: Application): AppComponent
    }
}