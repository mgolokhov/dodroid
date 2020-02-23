package doit.study.droid.data.local.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import doit.study.droid.data.local.QuizDatabase
import doit.study.droid.data.local.dao.util.MainCoroutineRule
import doit.study.droid.data.local.entity.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class TagDaoTest: DbTest() {

    @Test
    fun insertTagAndGetById() = runBlockingTest{
        // GIVEN
        val tag = Tag(
                name = "tag name"
                // new tags unselected by default
        )
        val tagId = db.tagDao().insertTag(tag).toInt()
        // WHEN
        val actualTag = db.tagDao().getTag(tagId)
        // THEN
        assertThat(actualTag.name, `is`(tag.name))
        assertThat(actualTag.selected, `is`(tag.selected))
    }

    @Test
    fun updateTagSelectionGetById() = runBlockingTest {
        // GIVEN
        val tag = Tag(
                name = "tag name"
                // new tags unselected by default
        )
        val tagId = db.tagDao().insertTag(tag)
        val tagSelected = tag.copy(id=tagId.toInt(), selected = true)
        // WHEN
        val tagIdUpdated = db.tagDao().insertTag(tagSelected)
        val actualTag  = db.tagDao().getTag(tagId.toInt())
        // THEN
        assertThat(tagIdUpdated, `is`(tagId))
        assertThat(actualTag, IsEqual<Tag>(tagSelected))
    }

}