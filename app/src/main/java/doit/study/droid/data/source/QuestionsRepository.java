package doit.study.droid.data.source;

import java.io.IOException;
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
import io.reactivex.Maybe;
import timber.log.Timber;

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
    public Maybe<List<Question>> getQuestions() {
        return quizDatabase.getQuizDao().getQuestionTagStatistics();
    }

    @Override
    public Completable saveQuestions(List<Question> questions) {
        return null;
    }

    @Override
    public Completable populateDb() {
        return Completable.fromAction(this::populateDbImpl);
    }

    private void populateDbImpl(){
        try {
            List<QuestionWeb> questions = quizWebService.getQuestionsSync().execute().body();
            if (questions == null) return;
            try {
                quizDatabase.beginTransaction();
                for (QuestionWeb q : questions) {
                    insertInDb(q);
                }
                quizDatabase.setTransactionSuccessful();
            } catch (Exception e){
                Timber.e(e);
            } finally {
                quizDatabase.endTransaction();
            }
        } catch (IOException e) {
            Timber.e(e);
        }
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

        long inserted = quizDatabase.statisticsDao().insert(new StatisticEntity(
                questionNetwork.id,
                0,
                0,
                0,
                false,
                0,
                0,
                0
        ));
        Timber.d("inserted statistics " + inserted);
    }
}
