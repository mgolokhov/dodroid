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
import doit.study.droid.R
import doit.study.droid.app.BaseApp
import doit.study.droid.databinding.FragmentQuizPageBinding
import doit.study.droid.utils.*
import timber.log.Timber
import javax.inject.Inject


class QuizPageFragment: Fragment(){
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var sound: Sound
    private lateinit var viewDataBinding: FragmentQuizPageBinding
    private val viewModel: QuizPageViewModel by lazyAndroid {
        ViewModelProviders
                .of(requireParentFragment(), viewModelFactory)
                .get(pagePosition.toString(), QuizPageViewModel::class.java)
    }
    private val viewModelMain: QuizMainViewModel by lazyAndroid {
        ViewModelProviders
                .of(parentFragment!!, viewModelFactory)
                .get(QuizMainViewModel::class.java)
    }
    private val pagePosition: Int by lazyAndroid {
        arguments!!.getInt(ARG_POSITION_IN_QUIZ_KEY, 0)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        BaseApp.dagger.inject(this)
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModelMain
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        viewDataBinding = FragmentQuizPageBinding.inflate(inflater, container, false)
        return viewDataBinding.root
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

        viewModelMain.items.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                // setting every config change?
                Timber.d("pager pagePosition $pagePosition for items size ${it.size}")
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
            viewModelMain.refreshUi()
        })
    }

    private fun setupToastFeedbackForEvaluation() {
        viewModel.showToastForEvaluationEvent.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let {
                showFeedbackToast(it)
            }
        })
    }

    private fun setupQuestion() {
        viewModel.item.observe(viewLifecycleOwner, Observer { quizView ->
            Timber.d("setupQuestion[$pagePosition]: $quizView")
            quizView?.let {
                viewDataBinding.question.text = quizView.questionText
                Timber.d("setupQuestion ${viewDataBinding.question.text} ${this.hashCode()} ${viewDataBinding.question.hashCode()}")
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
                    viewDataBinding.answers.addView(answerViewVariant)
                }
            }
        })

        viewModel.lockInteraction.observe(viewLifecycleOwner, Observer {
            for (i in 0 until viewDataBinding.answers.childCount) {
                viewDataBinding.answers.getChildAt(i)?.apply {
                    isEnabled = false
                }
            }
        })
    }

    private fun inflateAnswerItemView(): View {
        val inflater  = LayoutInflater.from(context)
        return inflater.inflate(R.layout.answer_item_variant, viewDataBinding.answers, false)
    }

    private fun setupCommitButton() {
        viewModel.commitButtonState.observe(viewLifecycleOwner, Observer {
            viewDataBinding.commitButton.setImageDrawable(resources.getDrawable(it))
        })
        viewDataBinding.commitButton.setOnClickListener {
            viewModel.checkAnswer()
        }
        viewModel.lockInteraction.observe(viewLifecycleOwner, Observer {
            viewDataBinding.commitButton.isEnabled = false
        })
    }

    private fun setupThumbUpButton() {
        viewDataBinding.thumpUpButton.setOnClickListener {
            viewModel.handleThumpUpButton(
                    AnalyticsData(
                            category = getString(R.string.report_because),
                            action = getString(R.string.like),
                            label = viewDataBinding.question.text.toString()
                    ))
        }
    }

    private fun setupThumbDownButton() {
        viewDataBinding.thumpDownButton.setOnClickListener{
            // TODO: code smells - decision should be in viewModel
            // hrr, ping pong with long flow based on onActivityResult
            if (!viewModel.isEvaluated()) {
                val dislikeDialog = FeedbackDialogFragment.newInstance(viewDataBinding.question.text.toString())
                dislikeDialog.setTargetFragment(this, REPORT_DIALOG_REQUEST_CODE)
                dislikeDialog.show(fragmentManager!!, REPORT_DIALOG_TAG)
            } else {
                showFeedbackToast(R.string.already_voted)
            }
        }
    }

    private fun setupSoundFeedbackForAnswer() {
        viewModel.playSoundEvent.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { soundType ->
                sound?.play(soundType, lifecycle)
            }
        })
    }

    private fun setupToastFeedbackForAnswer() {
        viewModel.showToastSuccessEvent.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                showToastSuccess(it)
            }
        })

        viewModel.showToastFailureEvent.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                showToastFailure(it)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK)
            return
        if (requestCode == REPORT_DIALOG_REQUEST_CODE) {
            data?.let {
                val label = viewDataBinding.question.text.toString() + data.getStringExtra(FeedbackDialogFragment.EXTRA_CAUSE)
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
        private const val REPORT_DIALOG_TAG = "fragment_feedback_dialog"
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

const val QUIZ_QUESTION_ITEM_TYPE = "quiz_question_item_type"