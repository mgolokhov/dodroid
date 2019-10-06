package doit.study.droid.di;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import doit.study.droid.data.source.QuestionsRepository;
import doit.study.droid.data.source.local.QuizDatabase;
import doit.study.droid.data.source.remote.QuizWebService;

@Module
public class AppModule {

    private Context context;

    public AppModule(Application app) {
        context = app;
    }

    @Provides
    @Singleton
    public Context provideAppContext() {
        return context;
    }

    @Singleton
    @Provides
    QuizDatabase provideDatabase(Context context) {
        return Room.databaseBuilder(
                context.getApplicationContext(),
                QuizDatabase.class,
                "quizDatabase.db")
                .build();
    }
    @Singleton
    @Provides
    QuestionsRepository provideRepository(QuizWebService quizWebService, QuizDatabase quizDatabase){
        return new QuestionsRepository(quizWebService, quizDatabase);
    }
}
