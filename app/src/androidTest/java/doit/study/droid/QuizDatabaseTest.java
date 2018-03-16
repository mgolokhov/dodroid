package doit.study.droid;


import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import doit.study.droid.data.source.local.QuizDatabase;
import doit.study.droid.data.source.local.entities.QuestionEntity;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class QuizDatabaseTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private QuizDatabase quizDatabase;

    @Before
    public void initDb() throws Exception {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        quizDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                QuizDatabase.class)
                // allowing main thread queries, just for testing
                .allowMainThreadQueries()
                .build();
    }

    @After
    public void closeDb() throws Exception {
        quizDatabase.close();
    }

    @Test
    public void questionsInsertByOne(){
        List<QuestionEntity> questions = new ArrayList<>();
        QuestionEntity q1 = new QuestionEntity(1, "text1", "w1\nw2", "r1\nr2", "link1");
        QuestionEntity q2 = new QuestionEntity(2, "text2", "w1\nw2", "r1\nr2", "link2");
        questions.add(q1);
        questions.add(q2);

        quizDatabase.questionDao().insert(q1);
        quizDatabase.questionDao().insert(q2);
        quizDatabase.questionDao().getAllQuestions()
                .test()
                .assertValue(v -> v.size() == questions.size())
                .assertValue(questions::containsAll)
        ;
    }


    @Test
    public void questionsInsertMultiple(){
        List<QuestionEntity> questions = new ArrayList<>();
        QuestionEntity q1 = new QuestionEntity(1, "text1", "w1\nw2", "r1\nr2", "link1");
        QuestionEntity q2 = new QuestionEntity(2, "text2", "w1\nw2", "r1\nr2", "link2");
        questions.add(q1);
        questions.add(q2);

        quizDatabase.questionDao().insert(questions);
        quizDatabase.questionDao().getAllQuestions()
                .test()
                .assertValue(v -> v.size() == questions.size())
                .assertValue(questions::containsAll)
        ;
    }


    @Test
    public void questionsInsertDuplicates(){
        List<QuestionEntity> questions = new ArrayList<>();
        QuestionEntity q1 = new QuestionEntity(1, "text1", "w1\nw2", "r1\nr2", "link1");
        QuestionEntity q2 = new QuestionEntity(2, "text2", "w1\nw2", "r1\nr2", "link2");
        questions.add(q1);
        questions.add(q2);

        quizDatabase.questionDao().insert(questions);
        // insert & check id of inserted items
        assertEquals(1, quizDatabase.questionDao().insert(q1));
        assertEquals(2, quizDatabase.questionDao().insert(q2));
        // query back items & compare
        quizDatabase.questionDao().getAllQuestions()
                .test()
                .assertValue(v -> v.size() == questions.size())
                .assertValue(questions::containsAll)
        ;
    }

    @Test
    public void questionsInsertWithRewrite(){
        List<QuestionEntity> questions = new ArrayList<>();
        QuestionEntity q1 = new QuestionEntity(1, "text1", "w1\nw2", "r1\nr2", "link1");
        QuestionEntity q2 = new QuestionEntity(2, "text2", "w1\nw2", "r1\nr2", "link2");
        QuestionEntity q3 = new QuestionEntity(2, "text3", "w1\nw2", "r1\nr2", "link3");
        questions.add(q1);
        questions.add(q3); // emulate rewrite

        assertEquals(1, quizDatabase.questionDao().insert(q1));
        assertEquals(2, quizDatabase.questionDao().insert(q2));
        assertEquals(2, quizDatabase.questionDao().insert(q3));
        // query back items & compare
        quizDatabase.questionDao().getAllQuestions()
                .test()
                .assertValue(v -> v.size() == questions.size())
                .assertValue(questions::containsAll)
        ;
    }
}
