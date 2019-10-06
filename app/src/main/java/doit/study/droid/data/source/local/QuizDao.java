package doit.study.droid.data.source.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

import doit.study.droid.data.source.Question;
import doit.study.droid.data.source.Statistic;
import doit.study.droid.data.source.Tag;
import io.reactivex.Maybe;

@Dao
public interface QuizDao {
    @Query("SELECT q.id as id, q.text as question, q.wrong, q.right, q.docLink, t.text as tags " +
            "FROM questions q " +
            "INNER JOIN statistics s ON s.id = q.id " +
            "INNER JOIN question_tag_join as qt ON q.id = qt.questionId " +
            "INNER JOIN tags t ON t.id = qt.tagId"
    )
    Maybe<List<Question>> getQuestionTagStatistics();

    @Query("SELECT t.id, t.text, count(*) as quantity, " +
            "sum(case s.status when 2 then 1 else 0 end) as learned, " +
            "t.checked, " +
            "group_concat(q.id) as questionIds " +
            "FROM questions q " +
            "INNER JOIN statistics s ON s.id = q.id " +
            "INNER JOIN question_tag_join as qt ON q.id = qt.questionId " +
            "INNER JOIN tags t ON t.id = qt.tagId " +
            "GROUP BY t.text "
    )
    Maybe<List<Tag>> getTagStatistics();


    @Query("UPDATE tags SET checked = case when id IN " +
            "(:tagIds) " +
            " then 1 else 0 end"
    )
    int updateCheckedTagsAndQuestions(Long... tagIds);


    @Query("UPDATE tags SET checked = :isChecked WHERE tags.id = :tagId"
    )
    int updateCheckedTag(Long tagId, boolean isChecked);


    @Query("SELECT * FROM statistics"
    )
    Maybe<List<Statistic>> getStatistics();


}

