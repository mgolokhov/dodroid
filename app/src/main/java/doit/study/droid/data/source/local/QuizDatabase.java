package doit.study.droid.data.source.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import doit.study.droid.data.source.local.entities.QuestionEntity;
import doit.study.droid.data.source.local.entities.QuestionTagJoin;
import doit.study.droid.data.source.local.entities.StatisticEntity;
import doit.study.droid.data.source.local.entities.TagEntity;


@Database(entities = {QuestionEntity.class, TagEntity.class, StatisticEntity.class, QuestionTagJoin.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class QuizDatabase extends RoomDatabase {
    public abstract QuestionDao getQuestionDao();
    public abstract TagDao getTagDao();
    public abstract StatisticDao getStatisticsDao();
    public abstract QuizDao getQuizDao();
}
