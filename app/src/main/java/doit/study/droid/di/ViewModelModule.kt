package doit.study.droid.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import doit.study.droid.common.ViewModelFactory
import doit.study.droid.quiz.QuizMainViewModel
import doit.study.droid.quiz.QuizPageViewModel
import doit.study.droid.splash.SplashViewModel
import doit.study.droid.topic.TopicModelView
import javax.inject.Singleton


@Module
abstract class ViewModelModule {
    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    abstract fun bindSplashViewModel(splashViewModel: SplashViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TopicModelView::class)
    abstract fun bindTopicModelView(topicModelView: TopicModelView): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(QuizPageViewModel::class)
    abstract fun bindQuizViewModel(quizPageViewModel: QuizPageViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(QuizMainViewModel::class)
    abstract fun bindQuizMainViewModel(quizMainViewModel: QuizMainViewModel): ViewModel
}