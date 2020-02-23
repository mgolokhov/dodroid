package doit.study.droid.data.local.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import doit.study.droid.data.local.QuizDatabase
import doit.study.droid.data.local.dao.util.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule

abstract class DbTest {

    private lateinit var database: QuizDatabase

    val db: QuizDatabase
        get() = database

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // That rule conflicts with other rules and throws RuntimeException: Delegate runner
    // @Rule
    // var thrown: ExpectedException = ExpectedException.none()

    @Before
    fun initDb() {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                QuizDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDb() = database.close()
}