package doit.study.droid.quiz

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import doit.study.droid.R
import doit.study.droid.app.App
import doit.study.droid.databinding.AnswerItemBinding
import doit.study.droid.databinding.FragmentQuizPageBinding
import doit.study.droid.utils.AnalyticsData
import doit.study.droid.utils.SoundPlayer
import doit.study.droid.utils.lazyAndroid
import doit.study.droid.utils.showToastFailure
import doit.study.droid.utils.showToastSuccess
import javax.inject.Inject
import timber.log.Timber

class QuizPageFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var soundPlayer: SoundPlayer
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
        App.dagger.inject(this)
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            Toast.makeText(
                    activity, getString(R.string.not_yet_for_this_question),
                    Toast.LENGTH_SHORT
            ).show()
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
        viewModel.item.observe(viewLifecycleOwner, Observer { quizItem ->
            Timber.d("setupQuestion[$pagePosition]: $quizItem")
            quizItem?.let {
                viewDataBinding.questionTextView.text = quizItem.questionText
                Timber.d("setupQuestion ${viewDataBinding.questionTextView.text} ${this.hashCode()} ${viewDataBinding.questionTextView.hashCode()}")
            }
        })
    }

    private fun setupAnswerVariants() {
        viewModel.item.observe(viewLifecycleOwner, Observer { quizItem ->
            Timber.d("setupAnswerVariants[$pagePosition]: $quizItem")
            quizItem?.let {
                val layoutInflater = LayoutInflater.from(view?.context)
                viewDataBinding.containerAnswerVariantsLinearLayout.removeAllViews()
                quizItem.answerVariants.forEach { variant ->
                    val answerViewVariant = AnswerItemBinding.inflate(layoutInflater, viewDataBinding.containerAnswerVariantsLinearLayout, false)
                    answerViewVariant.viewmodel = viewModel
                    answerViewVariant.answerVariantItem = variant
                    answerViewVariant.executePendingBindings()

                    viewDataBinding.containerAnswerVariantsLinearLayout.addView(answerViewVariant.root)
                }
            }
        })

        viewModel.lockInteraction.observe(viewLifecycleOwner, Observer {
            for (i in 0 until viewDataBinding.containerAnswerVariantsLinearLayout.childCount) {
                viewDataBinding.containerAnswerVariantsLinearLayout.getChildAt(i)?.apply {
                    isEnabled = false
                }
            }
        })
    }

    private fun setupCommitButton() {
        viewModel.commitButtonState.observe(viewLifecycleOwner, Observer {
            viewDataBinding.commitFabButton.setImageDrawable(resources.getDrawable(it))
        })
        viewDataBinding.commitFabButton.setOnClickListener {
            viewModel.checkAnswer()
        }
        viewModel.lockInteraction.observe(viewLifecycleOwner, Observer {
            viewDataBinding.commitFabButton.isEnabled = false
        })
    }

    private fun setupThumbUpButton() {
        viewDataBinding.thumbUpImageButton.setOnClickListener {
            viewModel.handleThumbUpButton(
                    AnalyticsData(
                            category = getString(R.string.report_because),
                            action = getString(R.string.like),
                            label = viewDataBinding.questionTextView.text.toString()
                    ))
        }
    }

    private fun setupThumbDownButton() {
        viewDataBinding.thumbDownImageButton.setOnClickListener {
            // TODO: code smells - decision should be in viewModel
            // hrr, ping pong with long flow based on onActivityResult
            if (!viewModel.isEvaluated()) {
                val dislikeDialog = FeedbackDialogFragment.newInstance(viewDataBinding.questionTextView.text.toString())
                dislikeDialog.setTargetFragment(this, REPORT_DIALOG_REQUEST_CODE)
                dislikeDialog.show(fragmentManager!!, REPORT_DIALOG_TAG)
            } else {
                showFeedbackToast(R.string.already_voted)
            }
        }
    }

    private fun setupSoundFeedbackForAnswer() {
        viewModel.playSoundEvent.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { fileName ->
                soundPlayer.play(lifecycle, fileName)
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
                val label = viewDataBinding.questionTextView.text.toString() + data.getStringExtra(FeedbackDialogFragment.EXTRA_CAUSE)
                viewModel.handleThumbDownButton(
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
