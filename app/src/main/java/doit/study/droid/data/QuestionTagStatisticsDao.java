package doit.study.droid.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface QuestionTagStatisticsDao {
    @Query("SELECT q.id as id, q.text as question, q.wrong, q.right, q.docLink " +
            "FROM questions q " +
            "INNER JOIN statistics s " +
            "ON s.id = q.id " +
            "INNER JOIN tags t " +
            "ON q.id = t.questionId " +
            ""
    )
    List<QuestionTagStatistics> getQuestionTagStatistics();
}

