package doit.study.droid.activities

import android.database.Cursor
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView


import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.viewpager.widget.PagerTabStrip
import androidx.viewpager.widget.ViewPager

import doit.study.droid.R
import doit.study.droid.adapters.InterrogatorPagerAdapter
import doit.study.droid.data.Question
import doit.study.droid.data.QuizProvider
import doit.study.droid.data.RelationTables
import doit.study.droid.fragments.InterrogatorFragment
import timber.log.Timber


class InterrogatorActivity : DrawerBaseActivity(), InterrogatorFragment.OnFragmentActivityChatter, LoaderManager.LoaderCallbacks<Cursor> {
    private var pager: ViewPager? = null
    private var rightCnt: Int = 0
    private var wrongCnt: Int = 0
    private var currentPageInFocus: Int = 0
    private var quizSize: Int = 0 // actual size can be lesser
    private var aprogress = -1 // quantity of answered questions
    private var pagerAdapter: InterrogatorPagerAdapter? = null
    private var handler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (DEBUG) Timber.d("onCreate")
        selectionId = R.id.nav_do_it

        handler = Handler()

        layoutInflater.inflate(R.layout.activity_interrogator, containerContent)
        supportLoaderManager.initLoader(QUESTION_LOADER, null, this@InterrogatorActivity)
        pager = findViewById(R.id.view_pager)
        configPagerTabStrip()
        pagerAdapter = InterrogatorPagerAdapter(supportFragmentManager, this)
        pager?.adapter = pagerAdapter

        savedInstanceState?.let {
            if (DEBUG) Timber.d("Restore saved state")
            rightCnt = it.getInt(RIGHT_CNT_KEY)
            wrongCnt = it.getInt(WRONG_CNT_KEY)
            aprogress = it.getInt(PROGRESS_KEY)
            if (aprogress == 0) {
                showProgress()
            } else {
                currentPageInFocus = it.getInt(PAGE_INDEX_IN_FOCUS_KEY)
                if (DEBUG) Timber.d("Cur page: %d", currentPageInFocus)
                pager?.setCurrentItem(currentPageInFocus, true)
            }
        }
        val questionsLeft = if (aprogress >= 0) aprogress else 0 // initial value -1
        title = resources.getQuantityString(R.plurals.numberOfQuestionsInTest, questionsLeft, questionsLeft)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (DEBUG) Timber.d("onSaveInstanceState")
        outState.putInt(WRONG_CNT_KEY, wrongCnt)
        outState.putInt(RIGHT_CNT_KEY, rightCnt)
        outState.putInt(PROGRESS_KEY, aprogress)
        outState.putInt(PAGE_INDEX_IN_FOCUS_KEY, pager!!.currentItem)
        super.onSaveInstanceState(outState)
    }


    private fun configPagerTabStrip() {
        val pagerTabStrip = pager!!.findViewById<PagerTabStrip>(R.id.pager_title_strip)
        // show one title
        pagerTabStrip.setNonPrimaryAlpha(0f)
        // set the black underlining
        pagerTabStrip.tabIndicatorColor = 0x000000
    }


    override fun saveStat(question: Question) {
        if (DEBUG) Timber.d("saveStat %s", question)
        contentResolver.update(QuizProvider.QUESTION_URI, Question.getContentValues(question), "_ID = " + question.id, null)
    }

    override fun updateProgress(isRight: Boolean) {
        if (isRight)
            ++rightCnt
        else
            ++wrongCnt
        --aprogress
        title = resources.getQuantityString(R.plurals.numberOfQuestionsInTest, aprogress, aprogress)
        if (aprogress == 0) {
            showProgress()
        }
    }

    private fun showProgress() {
        handler?.postDelayed({
            if (DEBUG) Timber.d("swipe to the result page")
            pagerAdapter?.addResultPage(rightCnt, wrongCnt)
            title = getString(R.string.test_completed)
            pager?.setCurrentItem(quizSize, true)
        }, 2000)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler?.removeCallbacksAndMessages(null)
    }

    override fun swipeToNext(delay: Int) {
        val posInFocus = pager!!.currentItem
        val handler = Handler()
        handler.postDelayed({
            if (posInFocus == pager!!.currentItem) {
                if (DEBUG) Timber.d("swipe to the next page")
                pager!!.setCurrentItem(posInFocus + 1, true)
            }
        }, delay.toLong())
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val randQuestionUri = QuizProvider.QUESTION_URI
                .buildUpon()
                .appendPath("rand").appendPath(Integer.toString(QUIZ_SIZE))
                .build()
        return CursorLoader(this, randQuestionUri, RelationTables.JoinedQuestionTagProjection, null, null, null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        if (DEBUG) Timber.d("load finished: %d", data.hashCode())
        val size = data.count
        if (size == 0) {
            val noTopic = findViewById<View>(R.id.no_topic_selected) as TextView
            noTopic.visibility = View.VISIBLE
            pager!!.visibility = View.GONE
        } else if (quizSize == 0) {
            quizSize = size
            if (aprogress == -1)
                aprogress = size
            pagerAdapter!!.setData(data)
            pager!!.setCurrentItem(currentPageInFocus, true)
            title = resources.getQuantityString(R.plurals.numberOfQuestionsInTest, aprogress, aprogress)
        }// load just once
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        if (DEBUG) Timber.d("onLoaderReset")
    }

    companion object {
        private const val DEBUG = false
        // counters for current test
        private const val WRONG_CNT_KEY = "doit.study.dodroid.wrong_cnt_key"
        private const val RIGHT_CNT_KEY = "doit.study.dodroid.right_cnt_key"
        private const val PAGE_INDEX_IN_FOCUS_KEY = "doit.study.dodroid.page_index_in_focus_key"
        private const val PROGRESS_KEY = "doit.study.dodroid.progress_key"
        private const val QUESTION_LOADER = 0
        private const val QUIZ_SIZE = 10 // default quiz size
    }
}

