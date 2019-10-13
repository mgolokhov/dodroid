package doit.study.droid.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.EditText


import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

import doit.study.droid.R

class DislikeDialogFragment : DialogFragment() {
    private var hostActivity: Activity? = null
    private var aview: View? = null
    private val causeIds = intArrayOf(R.id.question_incorrect, R.id.answer_incorrect, R.id.documentation_irrelevant)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        hostActivity = activity
        val inflater = hostActivity!!.layoutInflater
        aview = inflater.inflate(R.layout.fragment_dialog_dislike, null)

        val builder = AlertDialog.Builder(hostActivity!!)
        builder.setMessage(getString(R.string.report_because))
                .setView(aview)
                .setPositiveButton(hostActivity!!.getString(R.string.report)) { dialog, id ->
                    val fr = targetFragment
                    if (fr != null) {
                        val intent = Intent()
                        intent.putExtra(EXTRA_CAUSE, formReport())
                        fr.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                    }
                }
                .setNegativeButton(android.R.string.cancel) { dialog, id ->
                    // User cancelled the dialog
                }
        // Create the AlertDialog object and return it
        return builder.create()
    }

    private fun formReport(): String {
        val editText = aview!!.findViewById<View>(R.id.comment) as EditText
        val result = StringBuilder(" Cause:")
        for (id in causeIds) {
            val checkBox = aview!!.findViewById<View>(id) as CheckBox
            if (checkBox.isChecked)
                result.append(checkBox.text)
                        .append(",")
        }
        result.append(" Comment:")
        result.append(editText.text)
        return result.toString()
    }

    companion object {
        const val EXTRA_CAUSE = "doit.study.droid.extra_cause"
        private const val QUESTION_TEXT_KEY = "doit.study.droid.question_text_key"

        @JvmStatic
        fun newInstance(questionText: String): DislikeDialogFragment {
            val dislikeDialog = DislikeDialogFragment()
            val arg = Bundle()
            arg.putString(QUESTION_TEXT_KEY, questionText)
            dislikeDialog.arguments = arg
            return dislikeDialog
        }
    }

}