package doit.study.droid.data.source.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import doit.study.droid.data.source.local.entities.QuestionDb;

@Dao
public interface QuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<QuestionDb> questions);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(QuestionDb question);

    @Query("SELECT * FROM questions")
    List<QuestionDb> getAllQuestions();
}
