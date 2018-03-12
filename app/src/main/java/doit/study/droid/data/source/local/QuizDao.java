package doit.study.droid.data.source.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

import doit.study.droid.data.Question;
import io.reactivex.Flowable;

@Dao
public abstract class QuizDao {
    @Query("SELECT q.id as id, q.text as question, q.wrong, q.right, q.docLink, t.text as tags " +
            "FROM questions q " +
            "INNER JOIN statistics s " +
            "ON s.id = q.id " +
            "INNER JOIN tags t " +
            "ON q.id = t.questionId " +
            ""
    )
    public abstract Flowable<List<Question>> getQuestionTagStatistics();
}

