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
import androidx.navigation.fragment.findNavController
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
    private lateinit var viewModel: QuizPageViewModel
    private lateinit var viewModelMain: QuizMainViewModel
    private val pagePosition: Int by lazy { arguments!!.getInt(ARG_POSITION_IN_QUIZ_KEY, 0) }
    private lateinit var commitButton: FloatingActionButton
    private lateinit var thumpUpButton: ImageButton
    private lateinit var thumpDownButton: ImageButton
    private var toast: Toast? = null
    private val sound: Sound by lazy { Sound.getInstance(activity?.applicationContext) }
    private lateinit var question: TextView
    private lateinit var answerContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        BaseApp.dagger.inject(this)
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders
                .of(requireParentFragment(), viewModelFactory)
                .get(pagePosition.toString(), QuizPageViewModel::class.java)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        commitButton = view.findViewById(R.id.commit_button)
        thumpUpButton = view.findViewById(R.id.thump_up_button)
        thumpDownButton = view.findViewById(R.id.thump_down_button)
        question = view.findViewById(R.id.question)
        answerContainer = view.findViewById(R.id.answers)
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy ${this.hashCode()}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.d("onDestroyView ${this.hashCode()}")
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
                findNavController().navigate(R.id.settings_fragment_dest)
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
        Timber.d("onActivityCreated, page: $pagePosition; ${this.hashCode()}")
        viewModelMain = ViewModelProviders.of(parentFragment!!, viewModelFactory)[QuizMainViewModel::class.java]

        viewModelMain.items.observe(viewLifecycleOwner, Observer {
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
        viewModel.lockInteraction.observe(viewLifecycleOwner, Observer {
            Timber.d("try to update title")
            viewModelMain.updateQuestionsLeft()
        })
    }

    private fun setupToastFeedbackForEvaluation() {
        viewModel.showToastForEvaluation.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let {
                showFeedbackToast(it)
            }
        })
    }

    private fun setupQuestion() {
        viewModel.item.observe(viewLifecycleOwner, Observer { quizView ->
            Timber.d("setupQuestion[$pagePosition]: $quizView")
            quizView?.let {
                question.text = quizView.questionText
                Timber.d("setupQuestion ${question.text} ${this.hashCode()} ${question.hashCode()}")
            }
        })
    }

    private fun setupAnswerVariants() {
        viewModel.item.observe(viewLifecycleOwner, Observer { quizView ->
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

        viewModel.lockInteraction.observe(viewLifecycleOwner, Observer {
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
        viewModel.commitButtonState.observe(viewLifecycleOwner, Observer {
            commitButton.setImageDrawable(resources.getDrawable(it))
        })
        commitButton.setOnClickListener {
            viewModel.checkAnswer()
        }
        viewModel.lockInteraction.observe(viewLifecycleOwner, Observer {
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
        viewModel.playSound.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { soundType ->
                sound?.play(soundType)
            }
        })
    }

    private fun setupToastFeedbackForAnswer() {
        viewModel.showToastForAnswer.observe(viewLifecycleOwner, Observer {
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
                viewModel.handleThumpDownButton(
                        AnalyticsData(
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