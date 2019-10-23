package doit.study.droid.fragments

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

import timber.log.Timber



class OneTestSummaryFragment : LifecycleLogFragment() {
    private var progressBar: CircularProgressBar? = null
    private var percentage: TextView? = null
    private var textSummary: TextView? = null
    private var wrongCnt: TextView? = null
    private var rightCnt: TextView? = null
    private var aview: View? = null
    private var animatorSet: AnimatorSet? = null

    init {
        DEBUG = true
    }


    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (DEBUG) Timber.d("onCreateView")
        aview = inflater.inflate(R.layout.fragment_result_test, parent, false)
        progressBar = aview?.findViewById<View>(R.id.progressBar) as CircularProgressBar
        percentage = aview?.findViewById<View>(R.id.percentage) as TextView
        textSummary = aview?.findViewById<View>(R.id.textSummary) as TextView
        showTextSummary()
        rightCnt = aview?.findViewById<View>(R.id.right_cnt) as TextView
        var `val` = arguments!!.getInt(RIGHT_CNT_KEY)
        rightCnt!!.text = String.format(resources.getString(R.string.test_result_correct), `val`)
        wrongCnt = aview?.findViewById<View>(R.id.wrong_cnt) as TextView
        `val` = arguments!!.getInt(WRONG_CNT_KEY)
        wrongCnt!!.text = String.format(resources.getString(R.string.test_result_wrong), `val`)
        return aview
    }

    override fun setUserVisibleHint(visible: Boolean) {
        super.setUserVisibleHint(visible)
        if (DEBUG) Timber.d("setUserVisibleHint")
        if (visible && isResumed) {
            if (DEBUG) Timber.d("visible")
            aview?.post { animateProgress() }
        }
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun animateProgress() {
        if (DEBUG) Timber.d("animateProgress")
        val start = 0
        val end = arguments!!.getInt(RIGHT_CNT_KEY) * 100 / (arguments!!.getInt(RIGHT_CNT_KEY) + arguments!!.getInt(WRONG_CNT_KEY))
        val duration = 3000 // in milliseconds


        val progressBarAnimation = ObjectAnimator.ofFloat(progressBar, "progress", start.toFloat(), end.toFloat())
        progressBarAnimation.setDuration(duration.toLong())
        progressBarAnimation.setInterpolator(DecelerateInterpolator())


        val percentageAnimator = ValueAnimator()
        percentageAnimator.setObjectValues(start, end)
        percentageAnimator.addUpdateListener { animation -> percentage!!.text = String.format("%d%%", animation.animatedValue) }

        val typeEvaluator = TypeEvaluator<Int> { fraction, startValue, endValue
            -> Math.round(startValue!!.toFloat() + (endValue!!.toFloat() - startValue.toFloat()) * fraction)
        }
        percentageAnimator.setEvaluator(typeEvaluator)


        percentageAnimator.duration = duration.toLong()

        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0f, 1f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f, 1f)

        val textSummaryAnimator = ObjectAnimator.ofPropertyValuesHolder(textSummary, alpha, scaleX, scaleY)
        textSummaryAnimator.duration = (duration + 2000).toLong() // show text summary a bit slower


        // move from left side off-screen
        val wrongCntAnimation = ObjectAnimator.ofFloat(wrongCnt, "X", -wrongCnt!!.width.toFloat(), wrongCnt!!.left.toFloat())
        wrongCntAnimation.setDuration(duration.toLong())
        wrongCntAnimation.setInterpolator(BounceInterpolator())

        // move from right side off-screen
        val screenWidth = resources.displayMetrics.widthPixels
        val rightCntAnimation = ObjectAnimator.ofFloat(rightCnt, "X", screenWidth + rightCnt!!.width.toFloat(), rightCnt!!.left.toFloat())
        rightCntAnimation.setDuration(duration.toLong())
        rightCntAnimation.setInterpolator(BounceInterpolator())


        animatorSet = AnimatorSet()
        animatorSet!!.play(progressBarAnimation)
                .with(percentageAnimator)
                .with(textSummaryAnimator)
                .with(rightCntAnimation)
                .with(wrongCntAnimation)
        animatorSet!!.start()
    }

    override fun onResume() {
        super.onResume()
        aview?.post { animateProgress() }
    }

    override fun onPause() {
        animatorSet!!.cancel()
        super.onPause()
    }

    private fun showTextSummary() {
        val end = arguments!!.getInt(RIGHT_CNT_KEY) * 100 / (arguments!!.getInt(RIGHT_CNT_KEY) + arguments!!.getInt(WRONG_CNT_KEY))
        if (end <= 40)
            textSummary!!.text = resources.getString(R.string.test_result_summary40)
        else if (end > 40 && end <= 70)
            textSummary!!.text = resources.getString(R.string.test_result_summary70)
        else if (end > 70 && end < 100)
            textSummary!!.text = resources.getString(R.string.test_result_summary99)
        else if (end == 100)
            textSummary!!.text = resources.getString(R.string.test_result_summary100)
    }

    companion object {
        private const val WRONG_CNT_KEY = "doit.study.dodroid.wrong_cnt_key"
        private const val RIGHT_CNT_KEY = "doit.study.dodroid.right_cnt_key"

        fun newInstance(wrongCnt: Int, rightCnt: Int): Fragment {
            val oneTestSummaryFragment = OneTestSummaryFragment()
            val bundle = Bundle()
            bundle.putInt(WRONG_CNT_KEY, wrongCnt)
            bundle.putInt(RIGHT_CNT_KEY, rightCnt)
            oneTestSummaryFragment.arguments = bundle
            return oneTestSummaryFragment
        }
    }

}
