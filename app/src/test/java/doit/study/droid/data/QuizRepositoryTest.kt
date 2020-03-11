package doit.study.droid.data

import CoroutineTestRule
import androidx.room.withTransaction
import doit.study.droid.R
import doit.study.droid.data.local.QuizContentVersion
import doit.study.droid.data.local.QuizDatabase
import doit.study.droid.data.remote.Configuration
import doit.study.droid.data.remote.QuizData
import doit.study.droid.data.remote.QuizDataClient
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import runBlocking

@ExperimentalCoroutinesApi
class QuizRepositoryTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutineRule = CoroutineTestRule()

    private val quizDatabase = mockk<QuizDatabase>()
    private val quizContentVersion = mockk<QuizContentVersion>()
    private val quizDataClient = mockk<QuizDataClient>()

    private val coroutineDispatchers = CoroutineDispatchers(
            coroutineRule.testDispatcher,
            coroutineRule.testDispatcher,
            coroutineRule.testDispatcher
    )

    private val quizRepository = QuizRepository(
            quizDatabase = quizDatabase,
            quizContentVersion = quizContentVersion,
            quizDataClient = quizDataClient,
            coroutineDispatchers = coroutineDispatchers
    )

    private val fakeQuizDataItem = QuizData(
            id = 1,
            text = "text",
            docRef = "doc",
            rightAnswers = emptyList(),
            wrongAnswers = emptyList(),
            tags = listOf("tag")
    )

    @Before
    fun initMocks() {
        MockKAnnotations.init(this)
        // mock suspend function RoomDatabase.withTransaction
        mockkStatic(
                "androidx.room.RoomDatabaseKt"
        )
        val transactionLambda = slot<suspend () -> R>()
        coEvery { quizDatabase.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }
    }

    private fun setConfigurationVersions(local: Int, remote: Int) {
        every { quizContentVersion.getVersion() } returns local
        coEvery { quizDataClient.configuration() } returns Configuration(remote)
    }

    private fun `mock DB chained calls`() {
        coEvery { quizDatabase.questionDao().insertQuestion(any()) } returns Unit
        coEvery { quizDatabase.tagDao().insertTag(any()) } returns 0
        coEvery { quizDatabase.tagDao().getTags() } returns emptyList()
    }

    private fun `population of DB is expected (question, tag, relation tag question)`() {
        coVerify { quizDatabase.questionDao().insertQuestion(any()) }
        coVerify { quizDatabase.tagDao().insertTag(any()) }
        coVerify { quizDatabase.tagDao().insertQuestionTagJoin(any()) }
    }

    private fun `do not update DB`() {
        coVerify(exactly = 0) { quizDatabase.questionDao() }
        coVerify(exactly = 0) { quizDatabase.tagDao() }
    }

    @Test
    fun `should populate DB when remote config version higher then local`() = coroutineRule.runBlocking {
        // GIVEN
        setConfigurationVersions(local = 0, remote = 1)
        coEvery { quizDataClient.quizData() } returns listOf(fakeQuizDataItem)
        `mock DB chained calls`()
        // WHEN
        quizRepository.sync(forceUpdate = false)
        // THEN
        `population of DB is expected (question, tag, relation tag question)`()
    }

    @Test
    fun `should not populate DB without force when remote and local versions are same`() = coroutineRule.runBlocking {
        // GIVEN
        setConfigurationVersions(local = 1, remote = 1)
        coEvery { quizDataClient.quizData() } returns listOf(fakeQuizDataItem)
        `mock DB chained calls`()
        // WHEN
        quizRepository.sync(forceUpdate = false)
        // THEN
        `do not update DB`()
    }

    @Test
    fun `should populate DB when sync is forced`() = coroutineRule.runBlocking {
        // GIVEN
        setConfigurationVersions(local = 1, remote = 1)
        coEvery { quizDataClient.quizData() } returns listOf(fakeQuizDataItem)
        `mock DB chained calls`()
        // WHEN
        quizRepository.sync(forceUpdate = true)
        // THEN
        `population of DB is expected (question, tag, relation tag question)`()
    }
}
