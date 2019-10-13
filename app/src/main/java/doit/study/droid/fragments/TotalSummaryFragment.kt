package doit.study.droid.fragments

import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader

import java.util.Arrays

import doit.study.droid.R
import doit.study.droid.data.Question
import doit.study.droid.data.QuizProvider
import doit.study.droid.data.RelationTables
import doit.study.droid.data.Tag
import timber.log.Timber

class TotalSummaryFragment : LifecycleLogFragment(), LoaderManager.LoaderCallbacks<Cursor> {
    private var mTotalQuestions: TextView? = null
    private var mViewed: TextView? = null
    private var mAlmostStudied: TextView? = null
    private var mStudied: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (DEBUG) Timber.d("onCreateView")
        val v = inflater.inflate(R.layout.fragment_total_summary, parent, false)
        mTotalQuestions = v.findViewById<View>(R.id.total_quesitons) as TextView
        mViewed = v.findViewById<View>(R.id.viewed) as TextView
        mAlmostStudied = v.findViewById<View>(R.id.almost_studied) as TextView
        mStudied = v.findViewById<View>(R.id.studied) as TextView
        loaderManager.initLoader(QUESTION_LOADER, null, this)
        return v
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)

    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        if (DEBUG) Timber.d("onCreateLoader")
        when (id) {
            QUESTION_LOADER -> {
                val projection = Arrays.copyOf(RelationTables.JoinedQuestionTagProjection,
                        RelationTables.JoinedQuestionTagProjection.size + 1)
                projection[RelationTables.JoinedQuestionTagProjection.size] = "group_concat( " + Tag.Table.FQ_TEXT + ", '\n' ) as tags2"
                return CursorLoader(activity!!, QuizProvider.QUESTION_URI, projection, null, null, null)
            }
            else -> throw Exception("Wrong id for Cursor loader")
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        if (DEBUG) Timber.d("onLoadFinished")
        when (loader.id) {
            QUESTION_LOADER -> {
                if (DEBUG) Timber.d("QUESTION_LOADER Total questions: %d", data.count)
                mTotalQuestions!!.text = String.format(context!!.resources.getString(R.string.total_questions),
                        data.count)
                var viewed = 0
                var almostStudied = 0
                var studied = 0
                while (data.moveToNext()) {
                    val q = Question.newInstance(data)
                    if (q.isStudied) {
                        studied++
                        viewed++
                    } else if (q.consecutiveRightCnt != 0) {
                        almostStudied++
                        viewed++
                    } else if (q.rightAnsCnt != 0 || q.wrongAnsCnt != 0) {
                        viewed++
                    }
                }
                mStudied!!.text = String.format(context!!.resources.getString(R.string.studied_questions), studied)
                mAlmostStudied!!.text = String.format(context!!.resources.getString(R.string.almost_studied_questions), almostStudied)
                mViewed!!.text = String.format(context!!.resources.getString(R.string.viewed_questions), viewed)
            }
            else -> {
            }
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {

    }

    companion object {
        private const val DEBUG = true

        private const val QUESTION_LOADER = 1
    }
}
