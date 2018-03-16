package doit.study.droid.data.source.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import doit.study.droid.data.source.local.entities.QuestionEntity;
import io.reactivex.Maybe;

@Dao
public interface QuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insert(List<QuestionEntity> questions);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(QuestionEntity question);

    @Query("SELECT * FROM questions")
    Maybe<List<QuestionEntity>> getAllQuestions();
}
