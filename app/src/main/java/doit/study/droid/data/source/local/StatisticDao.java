package doit.study.droid.data.source.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import doit.study.droid.data.source.local.entities.StatisticEntity;

@Dao
public interface StatisticDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<StatisticEntity> statistics);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(StatisticEntity statistic);

    @Query("SELECT * FROM statistics")
    List<StatisticEntity> getAllStatistics();
}
