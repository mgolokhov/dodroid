package doit.study.droid.data.source;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;

public interface QuestionsDataSource {
    Completable populateDb();

    Completable saveQuestions(List<Question> questions);
    Completable saveTags(List<Tag> tags);

    Maybe<List<Question>> getQuestions();
    Maybe<List<Tag>> getTagStatistics();
}
