package doit.study.droid.data.source.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import doit.study.droid.data.source.local.entities.StatisticEntity;




@Dao
public interface StatisticDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(List<StatisticEntity> statistics);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(StatisticEntity statistic);

    @Query("SELECT * FROM statistics")
    List<StatisticEntity> getAllStatistics();

    @Query("UPDATE statistics SET checked = case when id IN " +
            "(SELECT s.id " +
            "FROM statistics s " +
            "INNER JOIN question_tag_join as qt ON s.id = qt.questionId " +
            "INNER JOIN tags t ON t.id = qt.tagId " +
            "WHERE t.text IN (:questionSelectedIds) " +
            ") then 1 else 0 end"
    )
    int updateCheckedQuestions(List<Integer> questionSelectedIds);

}

//    UPDATE statistics
//    SET checked = 1 WHERE id IN
//        (SELECT s.id
//                FROM statistics s
//                INNER JOIN question_tag_join as qt ON s.id = qt.questionId
//                INNER JOIN tags t ON t.id = qt.tagId
//                WHERE t.text IN ("Activity Class", "Animation", "AsyncTasks", "Broadcast Receivers")
//)
