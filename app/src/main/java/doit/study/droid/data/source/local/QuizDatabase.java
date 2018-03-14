package doit.study.droid.data.source.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import doit.study.droid.data.source.local.entities.QuestionDb;
import doit.study.droid.data.source.local.entities.QuestionTagJoin;
import doit.study.droid.data.source.local.entities.Statistic;
import doit.study.droid.data.source.local.entities.Tag;


@Database(entities = {QuestionDb.class, Tag.class, Statistic.class, QuestionTagJoin.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class QuizDatabase extends RoomDatabase {
    public abstract QuestionDao questionDao();
    public abstract TagDao tagDao();
    public abstract StatisticDao statisticsDao();
    public abstract QuizDao questionTagStatisticsDao();
}
