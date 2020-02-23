import android.database.sqlite.SQLiteConstraintException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import doit.study.droid.data.local.QuizDatabase
import doit.study.droid.data.local.dao.DbTest
import doit.study.droid.data.local.dao.util.MainCoroutineRule
import doit.study.droid.data.local.entity.Question
import doit.study.droid.data.local.entity.QuestionTagJoin
import doit.study.droid.data.local.entity.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.core.IsEqual
import org.junit.*
import org.junit.runner.RunWith
import java.util.*


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class QuestionDaoTest: DbTest() {
    @Test
    fun insertQuestionAndGetById() = runBlockingTest {
        // GIVEN - insert a question
        val lastViewedAt = Date().time
        val question = Question(
                id = 33,
                text = "question",
                wrong = listOf("wrong1", "wrong2"),
                right = listOf("right1", "right2"),
                docLink = "link",
                lastViewedAt = lastViewedAt
        )
        db.questionDao().insertQuestion(question)

        // WHEN - get question by id from db
        val loaded = db.questionDao().getQuestionById(question.id)

        // THEN question from DB contains expected values
        assertThat(loaded?.id, `is`(question.id))
        assertThat(loaded?.text, `is`(question.text))
        assertThat(loaded?.wrong, containsInAnyOrder("wrong2", "wrong1"))
        assertThat(loaded?.right, containsInAnyOrder("right1", "right2"))
        assertThat(loaded?.docLink, `is`("link"))
        assertThat(loaded?.wrongCounter, `is`(0))
        assertThat(loaded?.rightCounter, `is`(0))
        assertThat(loaded?.consecutiveRightCounter, `is`(0))
        assertThat(loaded?.lastViewedAt, `is`(lastViewedAt))
        assertThat(loaded?.studiedAt, `is`(0L))
    }

    @Ignore("Wait for migration to Room's insert with ABORT strategy")
    @Test(expected = SQLiteConstraintException::class)
    fun insertQuestionOnConflict() = runBlockingTest {
        // GIVEN that a question inserted
        val id = 33
        val question = Question(
                id = id,
                text = "question",
                wrong = listOf("wrong1", "wrong2"),
                right = listOf("right1", "right2"),
                docLink = "link"
        )
        db.questionDao().insertQuestion(question)

        // WHEN insert with duplicate id
        val questionWithIdDuplicate = Question(
                id = id,
                text = "questionWithIdDuplicate",
                wrong = listOf("wrong11", "wrong22"),
                right = listOf("right11", "right22"),
                docLink = "link1"
        )
        try {
            db.questionDao().insertQuestion(questionWithIdDuplicate)
            // THEN throw SQLiteConstraintException exception
        } catch (expectedException: SQLiteConstraintException) {
            assertThat(expectedException.localizedMessage, containsString("UNIQUE constraint failed"))
            throw expectedException
        }
    }

    @Test
    fun insertQuestionOnConflictReplace() = runBlockingTest {
        // GIVEN that a question inserted
        val id = 33
        val question = Question(
                id = id,
                text = "question",
                wrong = listOf("wrong1", "wrong2"),
                right = listOf("right1", "right2"),
                docLink = "link"
        )
        db.questionDao().insertQuestion(question)

        // WHEN insert with duplicate id
        val questionWithIdDuplicate = Question(
                id = id,
                text = "questionWithIdDuplicate",
                wrong = listOf("wrong11", "wrong22"),
                right = listOf("right11", "right22"),
                docLink = "link1"
        )
        db.questionDao().insertQuestion(questionWithIdDuplicate)
        val actualQuestion = db.questionDao().getQuestionById(id)

        // THEN
        assertThat(actualQuestion, `is`(questionWithIdDuplicate))
    }

    @Test
    fun insertQuestionsAndGetAll() = runBlockingTest {
        // GIVEN two tasks in DB
        val question1 = Question(
                id = 1,
                text = "question",
                wrong = listOf("wrong1", "wrong2"),
                right = listOf("right1", "right2"),
                docLink = "link"
        )
        val question2 = Question(
                id = 2,
                text = "question2",
                wrong = listOf("wrong11", "wrong21"),
                right = listOf("right11", "right21"),
                docLink = "link1"
        )
        db.questionDao().insertQuestions(question1, question2)

        // WHEN
        val res = db.questionDao().getQuestions()

        // THEN
        assertThat(res.size, `is`(2))
        assertThat(res, contains(question1, question2))
    }

    @Test
    fun updateQuestionStatisticsAndGetById() = runBlockingTest {
        // GIVEN
        val originalQuestion = Question(
                id = 1,
                text = "question",
                wrong = listOf("wrong1", "wrong2"),
                right = listOf("right1", "right2"),
                docLink = "link"
        )
        db.questionDao().insertQuestion(originalQuestion)
        // WHEN
        val studiedAt = Date().time
        val updatedQuestion = originalQuestion.copy(
                wrongCounter = 11,
                rightCounter = 15,
                studiedAt = studiedAt
        )
        db.questionDao().updateStatistics(
                id = updatedQuestion.id,
                wrongCount = updatedQuestion.wrongCounter,
                rightCount = updatedQuestion.rightCounter,
                studiedAt = updatedQuestion.studiedAt
        )
        val res = db.questionDao().getQuestionById(updatedQuestion.id)
        // THEN
        assertThat(res, IsEqual<Question>(updatedQuestion))
    }

    @Test
    fun insertQuestionWithTagGetByTag()  = runBlockingTest {
        // GIVEN insert question, tag and associate relation between them
        val question = Question(
                id = 1,
                text = "question",
                wrong = listOf("wrong1", "wrong2"),
                right = listOf("right1", "right2"),
                docLink = "link"
        )
        db.questionDao().insertQuestion(question)
        val tag = Tag(
                name = "tag_name"
        )
        val tagId = db.tagDao().insertTag(tag).toInt()

        db.tagDao().insertQuestionTagJoin(
                QuestionTagJoin(
                        questionId = question.id,
                        tagId = tagId
                ))

        // WHEN request question by tag
        val actualQuestion = db.questionDao().getQuestionsByTag(tag.name)
        // THEN get one question
        assertThat(actualQuestion.size, `is`(1))
        assertThat(actualQuestion[0], IsEqual<Question>(question))
    }
}