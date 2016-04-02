package doit.study.droid;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class DislikeDialog extends DialogFragment {
    public static final String EXTRA_CAUSE = "doit.study.droid.extra_cause";
    private static final String QUESTION_TEXT_KEY = "doit.study.droid.question_text_key";
    private Activity mHostActivity;
    private View mView;
    private int[] mCauseIds = {R.id.question_incorrect, R.id.answer_incorrect, R.id.documentation_irrelevant};

    public static DislikeDialog newInstance(String questionText) {
        DislikeDialog dislikeDialog = new DislikeDialog();
        Bundle arg = new Bundle();
        arg.putString(QUESTION_TEXT_KEY, questionText);
        dislikeDialog.setArguments(arg);
        return dislikeDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mHostActivity = getActivity();
        LayoutInflater inflater = mHostActivity.getLayoutInflater();
        mView = inflater.inflate(R.layout.dislike_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(mHostActivity);
        builder.setMessage(getString(R.string.report_because))
                .setView(mView)
                .setPositiveButton(mHostActivity.getString(R.string.report), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Fragment fr = getTargetFragment();
                        if (fr != null) {
                            Intent intent = new Intent();
                            intent.putExtra(EXTRA_CAUSE, formReport());
                            fr.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private String formReport() {
        EditText editText = (EditText) mView.findViewById(R.id.comment);
        StringBuilder result = new StringBuilder(" Cause:");
        for (int id : mCauseIds) {
            CheckBox checkBox = (CheckBox) mView.findViewById(id);
            if (checkBox.isChecked())
                result.append(checkBox.getText())
                        .append(",");
        }
        result.append(" Comment:");
        result.append(editText.getText());
        return result.toString();
    }

}