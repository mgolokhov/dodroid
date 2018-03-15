package doit.study.droid.data.source.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import doit.study.droid.data.source.local.entities.QuestionTagJoin;
import doit.study.droid.data.source.local.entities.TagEntity;
import io.reactivex.Flowable;

@Dao
public interface TagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<TagEntity> tags);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(TagEntity tag);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(QuestionTagJoin questionTagJoin);

    @Query("SELECT * FROM tags GROUP BY text ORDER BY text")
    Flowable<List<TagEntity>> getAllTags();
}
