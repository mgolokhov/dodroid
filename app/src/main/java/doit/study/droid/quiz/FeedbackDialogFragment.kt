package doit.study.droid.quiz

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import doit.study.droid.R
import doit.study.droid.databinding.FragmentFeedbackDialogBinding

class FeedbackDialogFragment : DialogFragment() {
    private lateinit var viewDataBinding: FragmentFeedbackDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            viewDataBinding = FragmentFeedbackDialogBinding.inflate(it.layoutInflater, null, false)
            val builder = AlertDialog.Builder(it)
            builder.setMessage(getString(R.string.report_because))
                    .setView(viewDataBinding.root)
                    .setPositiveButton(it.getString(R.string.report)) { _, _ ->
                        targetFragment?.let { tfr ->
                            val intent = Intent()
                            intent.putExtra(EXTRA_CAUSE, formReport())
                            tfr.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                        }
                    }
                    .setNegativeButton(android.R.string.cancel) { _, _ ->
                        // User cancelled the dialog
                    }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun formReport(): String {
        val causes = arrayOf(
                viewDataBinding.answerIncorrect,
                viewDataBinding.documentationIrrelevant,
                viewDataBinding.questionIncorrect
        )
        val result = StringBuilder(" Cause:")
        causes.filter { it.isChecked }.forEach {
            result.append(it.text).append(";")
        }
        result
                .append(" Comment:")
                .append(viewDataBinding.comment.text)
        return result.toString()
    }

    companion object {
        const val EXTRA_CAUSE = "doit.study.droid.extra_cause"
        private const val QUESTION_TEXT_KEY = "doit.study.droid.question_text_key"

        @JvmStatic
        fun newInstance(questionText: String): FeedbackDialogFragment {
            return FeedbackDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(QUESTION_TEXT_KEY, questionText)
                }
            }
        }
    }
}
