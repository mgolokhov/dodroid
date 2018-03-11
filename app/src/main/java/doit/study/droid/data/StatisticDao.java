package doit.study.droid.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface StatisticDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Statistic> statistics);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Statistic statistic);

    @Query("SELECT * FROM statistics")
    List<Statistic> getAllStatistics();
}
