package doit.study.droid.adapters

import android.content.Context
import android.database.Cursor
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

import java.util.ArrayList

import doit.study.droid.R
import doit.study.droid.data.Question
import doit.study.droid.fragments.InterrogatorFragment
import doit.study.droid.fragments.OneTestSummaryFragment
import timber.log.Timber


class InterrogatorPagerAdapter(fm: FragmentManager, private val mContext: Context) : FragmentStatePagerAdapter(fm) {
    private val questions = ArrayList<Question>()
    private var size: Int = 0
    private var rightCnt: Int = 0
    private var wrongCnt: Int = 0


    override fun getItem(position: Int): Fragment {
        //        if (DEBUG) Timber.d("getItem, pos=%d, question=%s", position, questions.get(position));
        return if (position < questions.size) {
            InterrogatorFragment.newInstance(questions[position])
        } else {
            OneTestSummaryFragment.newInstance(wrongCnt, rightCnt)
        }
    }


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        if (DEBUG) Timber.d("instantiateItem, pos=%d", position)
        return super.instantiateItem(container, position)
    }


    override fun getCount(): Int {
        //        if (DEBUG) Timber.d("Size: %d", size);
        return size
    }

    // don't know why, but getPageTitle called before getItem
    override fun getPageTitle(position: Int): CharSequence? {
        if (position < questions.size) {
            if (DEBUG) Timber.d("getPageTitle pos: %d, questions: %s", position, questions[position].id)
            val title = StringBuffer()
            // at exit pager asks title, cursor invalid
            for (tag in questions[position].tags)
                title.append(tag).append(" ")
            title.append(String.format(" %d/%d", position + 1, questions.size))
            return title
        } else {
            return mContext.resources.getString(R.string.test_result_title)
        }
    }

    fun addResultPage(rightCnt: Int, wrongCnt: Int) {
        this.rightCnt = rightCnt
        this.wrongCnt = wrongCnt
        size++
        notifyDataSetChanged()
    }

    fun setData(newCursor: Cursor) {
        if (DEBUG) Timber.d("setData:id:############")
        if (questions.size == 0) {
            while (newCursor.moveToNext()) {
                val q = Question.newInstance(newCursor)
                if (DEBUG) Timber.d("id: %d %s %s", q.id, q.tags, q.text)
                questions.add(q)
            }
            size = questions.size
            notifyDataSetChanged()
        }
    }

    companion object {
        private const val DEBUG = false
    }
}
