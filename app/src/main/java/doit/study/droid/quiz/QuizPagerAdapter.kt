package doit.study.droid.quiz

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import doit.study.droid.quiz_summary.ONE_TEST_SUMMARY_TYPE
import doit.study.droid.quiz_summary.QuizSummaryFragment

class QuizPagerAdapter(
    fm: FragmentManager,
    private val viewModel: QuizMainViewModel
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (viewModel.getItemType(position)) {
            QUIZ_QUESTION_ITEM_TYPE -> {
                QuizPageFragment.newInstance(position)
            }
            ONE_TEST_SUMMARY_TYPE -> {
                val (counterRightAnswers, counterWrongAnswers) = viewModel.getResultCounters()
                QuizSummaryFragment.newInstance(
                        wrongCnt = counterWrongAnswers,
                        rightCnt = counterRightAnswers
                )
            }
            else -> {
                throw IllegalArgumentException("Unknown type for pager")
            }
        }
    }

    override fun getCount(): Int {
        return viewModel.getCountForPager()
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return viewModel.getTabTitle(position)
    }
}
