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

import doit.study.droid.data.source.Tag;
import doit.study.droid.data.source.local.QuizDatabase;
import doit.study.droid.data.source.local.entities.QuestionEntity;
import doit.study.droid.data.source.local.entities.QuestionTagJoin;
import doit.study.droid.data.source.local.entities.StatisticEntity;
import doit.study.droid.data.source.local.entities.TagEntity;
import timber.log.Timber;

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

    @Test
    public void statisticInsertDuplicates(){
        StatisticEntity statisticEntity = new StatisticEntity(1,0,0,0,false,0,0,0);
        // check inserted ID
        assertEquals(1, quizDatabase.statisticsDao().insert(statisticEntity));
        quizDatabase.statisticsDao().getAllStatistics()
                .test()
                .assertValue(v -> v.size() == 1)
                .assertValue(v -> statisticEntity.equals(v.get(0)));


        StatisticEntity statisticEntity2 = new StatisticEntity(1,1,1,1,false,1,1,1);
        // check inserted ID, should indicate not inserted
        assertEquals(-1, quizDatabase.statisticsDao().insert(statisticEntity2));
        quizDatabase.statisticsDao().getAllStatistics()
                .test()
                .assertValue(v -> v.size() == 1)
                .assertValue(v -> {
                    Timber.d("res " + v.get(0));
                    return statisticEntity.equals(v.get(0));
                });
    }

    @Test
    public void statisticAndTagUpdate(){
        // question0 has tag0, tag1
        // question1 has tag0, tag2
        // question2 has tag2, tag3
        // question3 has tag0
        // choosing tag0 query tag and statistics will return
        // tag0 -> checked because all 3 questions checked
        // tag1 -> checked because all 1 question checked
        // tag2 -> unchecked because not all questions checked
        // tag3 -> unchecked because nothing is checked
        long [] id = new long[]{1, 2, 3, 4};
        QuestionEntity q0 = new QuestionEntity((int)id[0], "text0", "w1\nw2", "r1\nr2", "link1");
        QuestionEntity q1 = new QuestionEntity((int)id[1], "text1", "w1\nw2", "r1\nr2", "link1");
        QuestionEntity q2 = new QuestionEntity((int)id[2], "text2", "w1\nw2", "r1\nr2", "link1");
        QuestionEntity q3 = new QuestionEntity((int)id[3], "text3", "w1\nw2", "r1\nr2", "link1");
        quizDatabase.questionDao().insert(q0);
        quizDatabase.questionDao().insert(q1);
        quizDatabase.questionDao().insert(q2);
        quizDatabase.questionDao().insert(q3);
        StatisticEntity s0 = new StatisticEntity((int)id[0],0,0,0,false,0,0,0);
        StatisticEntity s1 = new StatisticEntity((int)id[1],0,0,0,false,0,0,0);
        StatisticEntity s2 = new StatisticEntity((int)id[2],0,0,0,false,0,0,0);
        StatisticEntity s3 = new StatisticEntity((int)id[3],0,0,0,false,0,0,0);
        quizDatabase.statisticsDao().insert(s0);
        quizDatabase.statisticsDao().insert(s1);
        quizDatabase.statisticsDao().insert(s2);
        quizDatabase.statisticsDao().insert(s3);
        TagEntity t0 = new TagEntity("tag0", checked);
        TagEntity t1 = new TagEntity("tag1", checked);
        TagEntity t2 = new TagEntity("tag2", checked);
        TagEntity t3 = new TagEntity("tag3", checked);
        long tagId0 = quizDatabase.tagDao().insert(t0);
        long tagId1 = quizDatabase.tagDao().insert(t1);
        long tagId2 = quizDatabase.tagDao().insert(t2);
        long tagId3 = quizDatabase.tagDao().insert(t3);
        quizDatabase.tagDao().insert(new QuestionTagJoin(id[0], tagId0));
        quizDatabase.tagDao().insert(new QuestionTagJoin(id[0], tagId1));
        quizDatabase.tagDao().insert(new QuestionTagJoin(id[1], tagId0));
        quizDatabase.tagDao().insert(new QuestionTagJoin(id[1], tagId2));
        quizDatabase.tagDao().insert(new QuestionTagJoin(id[2], tagId2));
        quizDatabase.tagDao().insert(new QuestionTagJoin(id[2], tagId3));
        quizDatabase.tagDao().insert(new QuestionTagJoin(id[3], tagId0));

        quizDatabase.statisticsDao().updateCheckedQuestionsByTags((int)id[0]);

        quizDatabase.getQuizDao().getTagStatistics()
                .test()
                .assertValue(v -> {
                    for (Tag t: v) {
                        // tag0, tag1 should be checked
                        if ((t.getId() == 1 || t.getId() == 2) && !t.isChecked()) return false;
                        // tag2, tag3 should be unchecked
                        if ((t.getId() == 3 || t.getId() == 4) && t.isChecked()) return false;
                    }
                    return true;
                });
    }

}
