package doit.study.droid.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import doit.study.droid.data.local.entity.QuestionTagJoin
import doit.study.droid.data.local.entity.Tag

@Dao
interface TagDao {
    @Query("select * from Tag")
    suspend fun getTags(): List<Tag>

    @Query("select * from Tag where id = :id")
    suspend fun getTag(id: Int): Tag

    @Query("select * from Tag where selected = :isSelected")
    suspend fun getTagBySelection(isSelected: Boolean): List<Tag>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: Tag): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceTag(vararg tags: Tag): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestionTagJoin(questionTagJoin: QuestionTagJoin)
}