package doit.study.droid.data.source;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import doit.study.droid.data.Question;
import doit.study.droid.data.source.local.QuizDatabase;
import doit.study.droid.data.source.local.entities.QuestionDb;
import doit.study.droid.data.source.local.entities.Statistic;
import doit.study.droid.data.source.local.entities.Tag;
import doit.study.droid.data.source.remote.QuestionNet;
import doit.study.droid.data.source.remote.QuizWebService;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class QuestionsRepository implements QuestionsDataSource {
    private final QuizWebService quizWebService;
    private final QuizDatabase quizDatabase;

    @Inject
    public QuestionsRepository(QuizWebService quizWebService, QuizDatabase quizDatabase){
        this.quizWebService = quizWebService;
        this.quizDatabase = quizDatabase;
    }

    @Override
    public Flowable<List<Question>> getQuestions() {
        return quizDatabase.questionTagStatisticsDao().getQuestionTagStatistics();
    }

    @Override
    public Completable saveQuestions(List<Question> questions) {
        return null;
    }

    @Override
    public Completable populateDb() {
        return quizWebService.getQuestions()
                // https://stackoverflow.com/questions/49058715/transactions-in-android-room-w-rxjava2
                .observeOn(Schedulers.single()) // off UI thread
                .doOnNext(__ -> quizDatabase.beginTransaction())
                .doOnComplete(quizDatabase::setTransactionSuccessful)
                .doFinally(quizDatabase::endTransaction)
                .flatMap(Flowable::fromIterable)
                .doOnNext(this::insertInDb)
                .ignoreElements()
        ;
    }

    private void insertInDb(QuestionNet questionNetwork){
        quizDatabase.questionDao().insert(new QuestionDb(
                questionNetwork.mId,
                questionNetwork.mText,
                questionNetwork.mWrongAnswers,
                questionNetwork.mRightAnswers,
                questionNetwork.mDocRef)
        );

        quizDatabase.tagDao().insert(new Tag(
                        questionNetwork.mTags,
                        questionNetwork.mId
                )
        );


        quizDatabase.statisticsDao().insert(new Statistic(
                questionNetwork.mId,
                0,
                0,
                0,
                false,
                0,
                0
        ));
    }
}
