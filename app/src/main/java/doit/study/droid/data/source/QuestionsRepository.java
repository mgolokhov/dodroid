package doit.study.droid.data.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import doit.study.droid.data.source.local.QuizDatabase;
import doit.study.droid.data.source.local.entities.QuestionEntity;
import doit.study.droid.data.source.local.entities.QuestionTagJoin;
import doit.study.droid.data.source.local.entities.StatisticEntity;
import doit.study.droid.data.source.local.entities.TagEntity;
import doit.study.droid.data.source.remote.QuestionWeb;
import doit.study.droid.data.source.remote.QuizWebService;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class QuestionsRepository implements QuestionsDataSource {
    private final QuizWebService quizWebService;
    private final QuizDatabase quizDatabase;
    private final Map<String, Long> tagsCache = new HashMap<>();


    @Inject
    public QuestionsRepository(QuizWebService quizWebService, QuizDatabase quizDatabase) {
        this.quizWebService = quizWebService;
        this.quizDatabase = quizDatabase;
    }

    @Override
    public Flowable<List<Question>> getQuestions() {
        return quizDatabase.getQuizDao().getQuestionTagStatistics();
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
                .doOnNext(__ -> {
                    quizDatabase.beginTransaction();
                    tagsCache.clear();
                })
                .doOnComplete(quizDatabase::setTransactionSuccessful)
                .doFinally(quizDatabase::endTransaction)
                .flatMap(Flowable::fromIterable)
                .doOnNext(this::insertInDb)
                .ignoreElements()
                ;
    }

    private void insertInDb(QuestionWeb questionNetwork) {
        quizDatabase.questionDao().insert(new QuestionEntity(
                questionNetwork.id,
                questionNetwork.text,
                questionNetwork.wrongAnswers,
                questionNetwork.rightAnswers,
                questionNetwork.docRef)
        );

        long tagId;
        for (String tag : questionNetwork.tags) {
            if (tagsCache.containsKey(tag)) {
                tagId = tagsCache.get(tag);
            } else {
                tagId = quizDatabase.tagDao().insert(new TagEntity(tag));
                tagsCache.put(tag, tagId);
            }
            quizDatabase.tagDao().insert(new QuestionTagJoin(questionNetwork.id, tagId));
        }

        quizDatabase.statisticsDao().insert(new StatisticEntity(
                questionNetwork.id,
                0,
                0,
                0,
                false,
                0,
                0,
                0
        ));
    }
}
