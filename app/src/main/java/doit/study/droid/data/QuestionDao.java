package doit.study.droid.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface QuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Question> questions);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Question question);

    @Query("SELECT * FROM questions")
    List<Question> getAllQuestions();
}
