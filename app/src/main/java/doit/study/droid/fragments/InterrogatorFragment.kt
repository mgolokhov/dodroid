package doit.study.droid.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.google.android.gms.analytics.HitBuilders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

import java.util.ArrayList
import java.util.Collections
import java.util.Random

import doit.study.droid.BuildConfig
import doit.study.droid.R
import doit.study.droid.utils2.Sound
import doit.study.droid.utils2.Views
import doit.study.droid.activities.SettingsActivity
import doit.study.droid.app.App
import doit.study.droid.data.Question
import timber.log.Timber


class InterrogatorFragment : LifecycleLogFragment(), View.OnClickListener {
    override var DEBUG = true
    // Callbacks
    private var mOnFragmentActivityChatter: OnFragmentActivityChatter? = null
    private var mState = State.NEW
    private var mVote = Vote.NONE
    private lateinit var mCurrentQuestion: Question
    private var mSound: Sound? = null

    private var mFeedbackWrongAnswered: Array<String>? = null
    private var mFeedbackRightAnswered: Array<String>? = null
    // View stuff
    private var mView: View? = null
    private var mvCheckBoxes: MutableList<CheckBox>? = null
    private var mvCommitButton: FloatingActionButton? = null
    private var mvQuestionText: TextView? = null
    private var mvAnswersLayout: ViewGroup? = null
    private var mvToast: Toast? = null

    private val isVoted: Boolean
        get() {
            if (mVote != Vote.NONE) {
                val mes = resources.getString(R.string.already_voted)
                val t = Toast.makeText(activity, mes, Toast.LENGTH_SHORT)
                t.setGravity(Gravity.CENTER, 0, 0)
                t.show()
                return true
            }
            return false
        }

    private enum class State {
        NEW, TRIED, ANSWERED_RIGHT, ANSWERED_WRONG
    }

    private enum class Vote {
        NONE, LIKED, DISLIKED
    }


    // Host Activity must implement these interfaces
    interface OnFragmentActivityChatter {
        fun swipeToNext(delay: Int)
        fun saveStat(question: Question)
        fun updateProgress(isRight: Boolean)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater)
        menuInflater.inflate(R.menu.fragment_question, menu)
    }


    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.doc_reference -> {
                openDocumentation()
                return true
            }
            R.id.action_settings -> {
                startActivity(Intent(context, SettingsActivity::class.java))
                return true
            }
            else -> return super.onOptionsItemSelected(menuItem)
        }
    }


    private fun openDocumentation() {
        if (mCurrentQuestion!!.docRef.isEmpty())
            Toast.makeText(activity, "Not yet for this question", Toast.LENGTH_SHORT).show()
        else {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(mCurrentQuestion!!.docRef)
            startActivity(intent)
        }
    }

    override fun onAttach(activity: Context) {
        super.onAttach(activity)
        try {
            mOnFragmentActivityChatter = activity as OnFragmentActivityChatter
        } catch (e: ClassCastException) {
            Timber.e(e, null)
            throw ClassCastException("$activity must implement OnFragmentActivityChatter")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        mFeedbackRightAnswered = resources.getStringArray(R.array.feedback_right_answer)
        mFeedbackWrongAnswered = resources.getStringArray(R.array.feedback_wrong_answer)
        mSound = Sound.getInstance(context)
        if (savedInstanceState != null) {
            mState = savedInstanceState.getSerializable(QUESTION_STATE_KEY) as State
            mVote = savedInstanceState.getSerializable(VOTE_STATE_KEY) as Vote
            mCurrentQuestion = savedInstanceState.getParcelable(QUESTION_KEY)
        } else
            mCurrentQuestion = arguments!!.getParcelable(QUESTION_KEY)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mkViewLinks(inflater, container)
        updateViews(savedInstanceState)
        return mView
    }


    private fun mkViewLinks(inflater: LayoutInflater, container: ViewGroup?) {
        if (DEBUG) Timber.d("mkViewLinks %s", hashCode())
        mView = inflater.inflate(R.layout.fragment_interrogator, container, false)
        mvQuestionText = mView!!.findViewById<View>(R.id.question) as TextView
        mvAnswersLayout = mView!!.findViewById<View>(R.id.answers) as ViewGroup
        mvCommitButton = mView!!.findViewById<View>(R.id.commit_button) as FloatingActionButton
        mvCommitButton!!.setOnClickListener(this)
        mView!!.findViewById<View>(R.id.thump_up_button).setOnClickListener(this)
        mView!!.findViewById<View>(R.id.thump_down_button).setOnClickListener(this)
    }


    // Map data from the current Question to the View elements
    private fun updateViews(savedInstanceState: Bundle?) {
        mvQuestionText!!.text = mCurrentQuestion!!.text
        updateAnswers(savedInstanceState)
        updateCommitButton()
        wodooWithPadding()
    }

    private fun wodooWithPadding() {
        // add padding to the answers list after the view is build
        // so floating button doesn't overlay content
        val vCtrlPanel = mView!!.findViewById<View>(R.id.ctrl_panel) as LinearLayout
        val vto = vCtrlPanel.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val obs = vCtrlPanel.viewTreeObserver
                val bottom_padding = vCtrlPanel.height
                // if (DEBUG) Timber.d("height %d", bottom_padding);
                mvAnswersLayout!!.setPadding(0, 0, 0, bottom_padding)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeOnGlobalLayoutListener(this)
                } else {
                    obs.removeGlobalOnLayoutListener(this)
                }
            }
        })
    }

    private fun updateAnswers(savedInstanceState: Bundle?) {
        val isDisabled = mState == State.ANSWERED_RIGHT || mState == State.ANSWERED_WRONG

        if (isDisabled && mvAnswersLayout!!.childCount != 0) {
            for (c in mvCheckBoxes!!)
                c.isEnabled = false
        } else {
            mvAnswersLayout!!.removeAllViewsInLayout()
            mvCheckBoxes = mutableListOf()
            val inflater = LayoutInflater.from(activity)
            for (answer in generateAnswers()) {
                val v = inflater.inflate(R.layout.fragment_interrogator_answer_item, mvAnswersLayout, true)
                val checkBox = v.findViewById<View>(R.id.checkbox_id) as CheckBox
                checkBox.text = answer
                checkBox.id = Views.generateViewId()
                if (null != savedInstanceState) {
                    checkBox.isChecked = savedInstanceState.getInt(answer) == 1
                    if (isDisabled)
                        checkBox.isEnabled = false
                }
                mvCheckBoxes!!.add(checkBox)
            }
        }
    }

    private fun generateAnswers(): List<String> {
        val allAnswers = ArrayList<String>()
        allAnswers.addAll(mCurrentQuestion!!.rightAnswers)
        allAnswers.addAll(mCurrentQuestion!!.wrongAnswers)
        Collections.shuffle(allAnswers)
        return allAnswers
    }


    private fun updateCommitButton() {
        when (mState) {
            InterrogatorFragment.State.ANSWERED_RIGHT -> mvCommitButton!!.setImageDrawable(resources.getDrawable(R.drawable.ic_sentiment_satisfied_black_24dp))
            InterrogatorFragment.State.ANSWERED_WRONG -> mvCommitButton!!.setImageDrawable(resources.getDrawable(R.drawable.ic_sentiment_dissatisfied_black_24dp))
            else -> return
        }
        mvCommitButton!!.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
        mvCommitButton!!.isEnabled = false
    }

    private fun checkAnswer() {
        if (DEBUG) Timber.d("checkAnswer %d", hashCode())
        mState = State.ANSWERED_RIGHT
        for (cb in mvCheckBoxes!!) {
            // You can have multiple right answers
            val cbText = cb.text.toString()
            if (cb.isChecked) {
                if (!mCurrentQuestion!!.rightAnswers.contains(cbText)) {
                    mState = State.TRIED
                    break
                }
            } else if (mCurrentQuestion!!.rightAnswers.contains(cbText)) {
                mState = State.TRIED
                break
            }
        }
    }

    private fun updateModel() {
        if (mState == State.ANSWERED_RIGHT) {
            mCurrentQuestion!!.incRightCounter()
            mOnFragmentActivityChatter!!.updateProgress(true)
        } else {
            if (mCurrentQuestion!!.incWrongCounter() >= ATTEMPTS_LIMIT) {
                mState = State.ANSWERED_WRONG
                mOnFragmentActivityChatter!!.updateProgress(false)
            }
        }
    }

    private fun showResultToast() {
        if (mvToast != null)
            mvToast!!.cancel()
        val rand = Random()
        if (mState == State.ANSWERED_RIGHT) {
            mvToast = Views.CustomToast.showToastSuccess(context,
                    mFeedbackRightAnswered!![rand.nextInt(mFeedbackRightAnswered!!.size)])
        } else {
            mvToast = Views.CustomToast.showToastError(context,
                    mFeedbackWrongAnswered!![rand.nextInt(mFeedbackWrongAnswered!!.size)])
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(QUESTION_STATE_KEY, mState)
        outState.putSerializable(VOTE_STATE_KEY, mVote)
        outState.putParcelable(QUESTION_KEY, mCurrentQuestion)
        for (cb in mvCheckBoxes!!) {
            outState.putInt(cb.text.toString(), if (cb.isChecked) 1 else 0)
        }
        super.onSaveInstanceState(outState)
    }


    override fun onClick(v: View) {
        if (DEBUG) Timber.d(v.id.toString())
        when (v.id) {
            R.id.commit_button -> handleCommitButton()
            R.id.thump_up_button -> handleThumpUpButton()
            R.id.thump_down_button -> handleThumpDownButton()
            else -> {
            }
        }
    }

    private fun handleThumpUpButton() {
        if (isVoted) return
        mVote = Vote.LIKED
        sendReport(getString(R.string.report_because), getString(R.string.like), mCurrentQuestion!!.text)
        val mes = resources.getString(R.string.thank_upvote)
        val t = Toast.makeText(activity, mes, Toast.LENGTH_SHORT)
        t.setGravity(Gravity.CENTER, 0, 0)
        t.show()
    }

    private fun handleThumpDownButton() {
        if (isVoted) return
        val dislikeDialog = DislikeDialogFragment.newInstance(mCurrentQuestion!!.text)
        dislikeDialog.setTargetFragment(this, REPORT_DIALOG_REQUEST_CODE)
        dislikeDialog.show(fragmentManager!!, REPORT_DIALOG_TAG)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK)
            return
        if (requestCode == REPORT_DIALOG_REQUEST_CODE) {
            if (data != null) {
                val label = mCurrentQuestion!!.text + data.getStringExtra(DislikeDialogFragment.EXTRA_CAUSE)
                sendReport(getString(R.string.report_because), getString(R.string.dislike), label)
                mVote = Vote.DISLIKED
                val mes = resources.getString(R.string.report_was_sent)
                val t = Toast.makeText(activity, mes, Toast.LENGTH_SHORT)
                t.setGravity(Gravity.CENTER, 0, 0)
                t.show()
            }
        }
    }

    private fun sendReport(category: String, action: String, label: String) {
        if (!BuildConfig.DEBUG) {
            val tracker = (activity!!.application as App).tracker
            tracker.send(HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action)
                    .setLabel(label)
                    .build())
        }
    }


    private fun handleCommitButton() {
        checkAnswer()
        showResultToast()
        showDocumentationSnackBar()
        updateModel()
        updateViews(null)
        mSound!!.play(mState == State.ANSWERED_RIGHT)
        if (mState == State.ANSWERED_RIGHT) {
            mOnFragmentActivityChatter!!.swipeToNext(SWIPE_DELAY)
        }
    }

    private fun showDocumentationSnackBar() {
        if (mState != State.ANSWERED_RIGHT) {
            val mes = resources.getString(R.string.check_docs)
            Snackbar.make(mView!!, mes, Snackbar.LENGTH_LONG)
                    .setAction(resources.getString(R.string.go)) { openDocumentation() }
                    .show()
        }
    }

    override fun onPause() {
        if (DEBUG) Timber.d("onPause, should be save")
        if (mvToast != null)
            mvToast!!.cancel()
        mOnFragmentActivityChatter!!.saveStat(mCurrentQuestion)
        mSound!!.stop()
        super.onPause()
    }

    override fun onStop() {
        mSound!!.release()
        super.onStop()
    }

    override fun onDetach() {
        // with setRetainInstance(true) can be a leak
        mOnFragmentActivityChatter = null
        super.onDetach()
    }

    companion object {
        private const val REPORT_DIALOG_REQUEST_CODE = 0
        const val REPORT_DIALOG_TAG = "fragment_dialog_dislike"
        // Keys for bundle to save state
        private const val QUESTION_KEY = "doit.study.dodroid.id_key"
        private const val VOTE_STATE_KEY = "doit.study.dodroid.vote_state_key"
        private const val QUESTION_STATE_KEY = "doit.study.dodroid.question_state_key"
        // Model stuff
        private const val SWIPE_DELAY = 2000
        private const val ATTEMPTS_LIMIT = 2

        @JvmStatic
        fun newInstance(question: Question): InterrogatorFragment {
            Timber.d("newInstance %s", question)
            val fragment = InterrogatorFragment()
            val bundle = Bundle()
            bundle.putParcelable(QUESTION_KEY, question)
            fragment.arguments = bundle
            return fragment
        }
    }
}
