package doit.study.droid.quiz

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import doit.study.droid.R
import doit.study.droid.app.BaseApp
import doit.study.droid.settings.SettingsFragment
import doit.study.droid.utils.AnalyticsData
import doit.study.droid.utils.Sound
import doit.study.droid.utils.Views
import timber.log.Timber
import javax.inject.Inject
import kotlin.random.Random


class QuizPageFragment: Fragment(){
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var viewModel: QuizPageViewModel
    lateinit var viewModelMain: QuizMainViewModel
    private val pagePosition: Int by lazy { arguments!!.getInt(ARG_POSITION_IN_QUIZ_KEY, 0) }
    private val commitButton by lazy { view!!.findViewById<FloatingActionButton>(R.id.commit_button) }
    private val thumpUpButton by lazy { view!!.findViewById<ImageButton>(R.id.thump_up_button)}
    private val thumpDownButton by lazy { view!!.findViewById<ImageButton>(R.id.thump_down_button)}
    private var toast: Toast? = null
    private val sound: Sound? by lazy { Sound.getInstance(context) }
    private val question by lazy { view!!.findViewById<TextView>(R.id.question) }
    private val answerContainer by lazy { view!!.findViewById<LinearLayout>(R.id.answers) }

    override fun onCreate(savedInstanceState: Bundle?) {
        BaseApp.dagger.inject(this)
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders
                .of(requireParentFragment(), viewModelFactory)
                .get(pagePosition.toString(), QuizPageViewModel::class.java)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_interrogator, container, false)
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

                activity?.run {
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.container_content, SettingsFragment())
                            .commit()

                }
                return true
            }
            else -> return super.onOptionsItemSelected(menuItem)
        }
    }

    private fun openDocumentation() {
        if (viewModel.getDocRef().isEmpty())
            Toast.makeText(activity, "Not yet for this question", Toast.LENGTH_SHORT).show()
        else {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(viewModel.getDocRef())
            startActivity(intent)
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Timber.d("onActivityCreated, page: $pagePosition; $this")
        viewModelMain = ViewModelProviders.of(parentFragment!!, viewModelFactory)[QuizMainViewModel::class.java]

        viewModelMain.items.observe(this, Observer {
            if (it.isNotEmpty()) {
                // setting every config change?
                viewModel.setItem(it[pagePosition])
                setupQuestion()
                setupAnswerVariants()
                setupCommitButton()
                setupThumbUpButton()
                setupThumbDownButton()
                setupToastFeedbackForEvaluation()
                setupToastFeedbackForAnswer()
                setupSoundFeedbackForAnswer()
                setupTitle()
            }
        })
    }

    private fun setupTitle() {
        viewModel.lockInteraction.observe(this, Observer {
            Timber.d("try to update title")
            viewModelMain.updateQuestionsLeft()
        })
    }

    private fun setupToastFeedbackForEvaluation() {
        viewModel.showToastForEvaluation.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let {
                showFeedbackToast(it)
            }
        })
    }

    private fun setupQuestion() {
        viewModel.item.observe(this, Observer { quizView ->
            Timber.d("setupQuestion[$pagePosition]: $quizView")
            quizView?.let {
                question.text = quizView.questionText
            }
        })
    }

    private fun setupAnswerVariants() {
        viewModel.item.observe(this, Observer { quizView ->
            Timber.d("setupAnswerVariants[$pagePosition]: $quizView")
            quizView?.let {
                quizView.answerVariants.forEach { variant ->
                    val answerViewVariant = inflateAnswerItemView()
                    answerViewVariant
                            .findViewById<CheckBox>(R.id.checkbox_id)
                            .apply {
                                text = variant
                                isChecked = (variant in quizView.selectedVariants)
                                setOnClickListener {
                                    val item = this as CheckBox
                                    viewModel.saveCheckState(
                                            text = item.text as String,
                                            isChecked = item.isChecked
                                    )
                                }
                            }
                    answerContainer.addView(answerViewVariant)
                }
            }
        })

        viewModel.lockInteraction.observe(this, Observer {
            for (i in 0 until answerContainer.childCount) {
                answerContainer.getChildAt(i)?.apply {
                    isEnabled = false
                }
            }
        })
    }

    private fun inflateAnswerItemView(): View {
        val inflater  = LayoutInflater.from(context)
        return inflater.inflate(R.layout.answer_item_variant, answerContainer, false)
    }

    private fun setupCommitButton() {
        viewModel.commitButtonState.observe(this, Observer {
            commitButton.setImageDrawable(resources.getDrawable(it))
        })
        commitButton.setOnClickListener {
            viewModel.checkAnswer()
        }
        viewModel.lockInteraction.observe(this, Observer {
            commitButton.isEnabled = false
        })
    }

    private fun setupThumbUpButton() {
        thumpUpButton.setOnClickListener {
            viewModel.handleThumpUpButton(
                    AnalyticsData(
                            category = getString(R.string.report_because),
                            action = getString(R.string.like),
                            label = question.text.toString()
                    ))
        }
    }

    private fun setupThumbDownButton() {
        thumpDownButton.setOnClickListener{
            // TODO: code smells - decision should be in viewModel
            // hrr, ping pong with long flow based on onActivityResult
            if (!viewModel.isEvaluated()) {
                val dislikeDialog = DislikeDialogFragment.newInstance(question.text.toString())
                dislikeDialog.setTargetFragment(this, REPORT_DIALOG_REQUEST_CODE)
                dislikeDialog.show(fragmentManager!!, REPORT_DIALOG_TAG)
            } else {
                showFeedbackToast(R.string.already_voted)
            }
        }
    }

    private fun setupSoundFeedbackForAnswer() {
        viewModel.playSound.observe(this, Observer {
            it.getContentIfNotHandled()?.let { soundType ->
                sound?.play(soundType)
            }
        })
    }

    private fun setupToastFeedbackForAnswer() {
        viewModel.showToastForAnswer.observe(this, Observer {
            // TODO: randomization logic leaked in a view
            // what's about AndroidViewModel & config change? (point for testing)
            it.getContentIfNotHandled()?.let { resourceId ->
                val message = getRandomMessageFromResources(resourceId)
                toast = when(resourceId) {
                    R.array.feedback_right_answer -> {
                        Views.CustomToast.showToastSuccess(context, message)
                    }
                    R.array.feedback_wrong_answer -> {
                        Views.CustomToast.showToastError(context, message)
                    }
                    else -> throw IllegalArgumentException("Wrong resource id for ToastFeedbackForAnswer")
                }
            }
        })
    }

    private fun getRandomMessageFromResources(resourceId: Int): String {
        val variants = resources.getStringArray(resourceId)
        val pos = Random.nextInt(variants.size)
        return variants[pos]
    }

    override fun onPause() {
        super.onPause()
        // TODO: lifecycle aware + DI
        sound?.stop()
    }

    override fun onStop() {
        super.onStop()
        sound?.release()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK)
            return
        if (requestCode == REPORT_DIALOG_REQUEST_CODE) {
            data?.let {
                val label = question.text.toString() + data.getStringExtra(DislikeDialogFragment.EXTRA_CAUSE)
                viewModel.handleThumpDownButton(pagePosition, AnalyticsData(
                        category = getString(R.string.report_because),
                        action = getString(R.string.dislike),
                        label = label
                ))
            }
        }
    }

    private fun showFeedbackToast(@StringRes message: Int) {
        val mes = resources.getString(message)
        Toast.makeText(activity, mes, Toast.LENGTH_SHORT).apply {
            setGravity(Gravity.CENTER, 0, 0)
            show()
        }
    }

    companion object {
        private const val REPORT_DIALOG_REQUEST_CODE = 0
        private const val REPORT_DIALOG_TAG = "fragment_dialog_dislike"
        private const val ARG_POSITION_IN_QUIZ_KEY = "arg_position_in_quiz_key"
        fun newInstance(position: Int): QuizPageFragment {
            return QuizPageFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_POSITION_IN_QUIZ_KEY, position)
                }
            }
        }
    }
}