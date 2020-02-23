package doit.study.droid.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import doit.study.droid.data.local.entity.Question

@Dao
interface QuestionDao {
    @Query("select * from Question")
    suspend fun getQuestions(): List<Question>

    @Query("select * from Question where id = :id")
    suspend fun getQuestionById(id: Int): Question?

    @Query("select distinct Q.* from Question as Q inner join QuestionTagJoin as QTJ on Q.id = QTJ.questionId inner join Tag as T on T.id = QTJ.tagId where T.name = :tag")
    suspend fun getQuestionsByTag(tag: String): List<Question>

    // TODO: migrate to ABORT strategy
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: Question)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(vararg questions: Question)

    @Query("update Question set wrongCounter = :wrongCount, rightCounter = :rightCount, studiedAt = :studiedAt where id = :id")
    suspend fun updateStatistics(id: Int, wrongCount: Int, rightCount: Int, studiedAt: Long)
}
