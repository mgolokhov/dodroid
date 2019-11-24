package doit.study.droid.quiz

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.TextView

import androidx.fragment.app.Fragment

import com.mikhaellopez.circularprogressbar.CircularProgressBar
import doit.study.droid.R
import doit.study.droid.views.DeathStarLoader
import kotlin.math.roundToInt


class OneTestSummaryFragment : Fragment() {
    private val progressBar by lazy { view!!.findViewById<View>(R.id.progressBar) as DeathStarLoader }
    private val textSummary by lazy { view!!.findViewById<View>(R.id.textSummary) as TextView }
    private val wrongCnt by lazy { view!!.findViewById<View>(R.id.wrong_cnt) as TextView }
    private val rightCnt by lazy { view!!.findViewById<View>(R.id.right_cnt) as TextView }
    private val percentageOfRightAnswers by lazy {
        arguments!!.let {
            it.getInt(RIGHT_CNT_KEY) * 100 / (it.getInt(RIGHT_CNT_KEY) + it.getInt(WRONG_CNT_KEY))
        }
    }
    private var animatorSet: AnimatorSet? = null


    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_result_test, parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showTextSummary()
        arguments!!.let {
            rightCnt.text = String.format(resources.getString(R.string.test_result_correct), it.getInt(RIGHT_CNT_KEY))
            wrongCnt.text = String.format(resources.getString(R.string.test_result_wrong), it.getInt(WRONG_CNT_KEY))
        }
    }

    override fun setUserVisibleHint(visible: Boolean) {
        super.setUserVisibleHint(visible)
        if (visible && isResumed) {
            view?.post { animateProgress() }
        }
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun animateProgress() {
        val start = 0F
        val end = percentageOfRightAnswers.toFloat()
        val durationMs = 3_000L // in milliseconds

        progressBar.setProgress(percentageOfRightAnswers, durationMs)

        val textSummaryAnimator = setupTextSummaryAnimator(durationMs)
        val wrongCntAnimation = setupWrongCntAnimation(durationMs)
        val rightCntAnimation = setupRightCntAnimation(durationMs)

        animatorSet = AnimatorSet().apply {
            play(textSummaryAnimator)
                    .with(rightCntAnimation)
                    .with(wrongCntAnimation)
            start()
        }
    }

    private fun setupRightCntAnimation(durationMs: Long): ObjectAnimator {
        // move from right side off-screen
        val screenWidth = resources.displayMetrics.widthPixels
        return ObjectAnimator.ofFloat(rightCnt, "X", screenWidth + rightCnt.width.toFloat(), rightCnt.left.toFloat()).apply {
            duration = durationMs
            interpolator = BounceInterpolator()
        }
    }

    private fun setupWrongCntAnimation(durationMs: Long): ObjectAnimator {
        // move from left side off-screen
        return ObjectAnimator.ofFloat(wrongCnt, "X", -wrongCnt.width.toFloat(), wrongCnt.left.toFloat()).apply {
            duration = durationMs
            interpolator = BounceInterpolator()
        }
    }

    private fun setupTextSummaryAnimator(durationMs: Long): ObjectAnimator {
        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0f, 1f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f, 1f)

        val textSummaryAnimator = ObjectAnimator.ofPropertyValuesHolder(textSummary, alpha, scaleX, scaleY)
        textSummaryAnimator.duration = (durationMs + 2000) // show text summary a bit slower
        return textSummaryAnimator
    }

    override fun onResume() {
        super.onResume()
        view?.post { animateProgress() }
    }

    override fun onPause() {
        animatorSet?.cancel()
        super.onPause()
    }

    private fun showTextSummary() {
        textSummary.text = when {
            percentageOfRightAnswers <= 40 -> resources.getString(R.string.test_result_summary40)
            percentageOfRightAnswers in 41..70 -> resources.getString(R.string.test_result_summary70)
            percentageOfRightAnswers in 71..99 -> resources.getString(R.string.test_result_summary99)
            else -> resources.getString(R.string.test_result_summary100)
        }
    }

    companion object {
        private const val WRONG_CNT_KEY = "doit.study.dodroid.wrong_cnt_key"
        private const val RIGHT_CNT_KEY = "doit.study.dodroid.right_cnt_key"

        fun newInstance(wrongCnt: Int, rightCnt: Int): Fragment {
            return OneTestSummaryFragment().apply {
                arguments = Bundle().apply {
                    putInt(WRONG_CNT_KEY, wrongCnt)
                    putInt(RIGHT_CNT_KEY, rightCnt)
                }
            }
        }
    }

}

const val ONE_TEST_SUMMARY_TYPE = "one_test_summary_type"
