package doit.study.droid.data.source;

import java.util.List;

import doit.study.droid.data.Question;
import io.reactivex.Completable;
import io.reactivex.Flowable;

public interface QuestionsDataSource {
    Flowable<List<Question>> getQuestions();
    Completable saveQuestions(List<Question> questions);
    Completable populateDb();
}
