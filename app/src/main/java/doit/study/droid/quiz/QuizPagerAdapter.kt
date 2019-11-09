package doit.study.droid.quiz

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import doit.study.droid.R


class QuizPagerAdapter(
        fm: FragmentManager,
        private val items: List<QuizView>,
        private val context: Context
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private var showResultPage: Boolean = false

    override fun getItem(position: Int): Fragment {
        return if (position < items.size)
            QuizPageFragment.newInstance(position)
        else {
            // TODO: move logic
            val rightAnswers = items.filter { it.answered && it.selectedVariants == it.rightVariants.toSet()}.size
            val wrongAnswers = items.size - rightAnswers
            OneTestSummaryFragment.newInstance(wrongAnswers, rightAnswers)
        }
    }

    override fun getCount(): Int {
        return if (showResultPage)
            items.size + 1
        else
            items.size
    }

    fun addResultPage() {
        showResultPage = true
        notifyDataSetChanged()
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (showResultPage && position == items.size)
            context.resources.getString(R.string.test_result_title)
        else
            "${items[position].title} ${position+1}/${items.size}"
    }
}
