package doit.study.droid.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;


@Database(entities = {Question.class, Tag.class, Statistic.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class QuizDatabase extends RoomDatabase {

    private static final String DB_NAME = "quizDatabase.db";
    private static QuizDatabase instance;

    public static synchronized QuizDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static QuizDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                QuizDatabase.class,
                DB_NAME)
                .allowMainThreadQueries()
                .build();
    }


    public abstract QuestionDao questionDao();
    public abstract TagDao tagDao();
    public abstract StatisticDao statisticsDao();
    public abstract QuestionTagStatisticsDao questionTagStatisticsDao();
}
