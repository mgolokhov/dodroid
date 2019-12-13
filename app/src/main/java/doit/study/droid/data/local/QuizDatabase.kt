package doit.study.droid.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import doit.study.droid.data.local.dao.QuestionDao
import doit.study.droid.data.local.dao.TagDao
import doit.study.droid.data.local.entity.Question
import doit.study.droid.data.local.entity.QuestionTagJoin
import doit.study.droid.data.local.entity.Tag

@Database(entities = [Question::class, Tag::class, QuestionTagJoin::class], version = 2)
@TypeConverters(Converters::class)
abstract class QuizDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao
    abstract fun tagDao(): TagDao
}
