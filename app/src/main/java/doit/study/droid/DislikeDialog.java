package doit.study.droid;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import doit.study.droid.model.GlobalData;

public class DislikeDialog extends DialogFragment implements View.OnClickListener{
    private static final String QUESTION_TEXT_KEY = "doit.study.droid.question_text_key";
    private Activity mHostActivity;
    private View mView;
    private String mCause = "Not selected";

    public static DislikeDialog newInstance(String questionText){
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
        mView.findViewById(R.id.answer_incorrect).setOnClickListener(this);
        mView.findViewById(R.id.question_incorrect).setOnClickListener(this);
        mView.findViewById(R.id.documentation_irrelevant).setOnClickListener(this);

        final Tracker tracker = ((GlobalData) mHostActivity.getApplication()).getTracker();
        AlertDialog.Builder builder = new AlertDialog.Builder(mHostActivity);
        builder.setMessage(getString(R.string.report_because))
                .setView(mView)
                .setPositiveButton("Report", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        tracker.send(new HitBuilders.EventBuilder()
                                .setCategory(getString(R.string.report_because))
                                .setAction("dislike")
                                .setLabel(formReport())
                                .build()
                        );
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private String formReport(){
        EditText editText = (EditText) mView.findViewById(R.id.comment);
        String result = getArguments().getString(QUESTION_TEXT_KEY) + "#"
                + mCause + "#"
                + editText.getText();
        return result;
    }

    @Override
    public void onClick(View v) {
        Log.i("NSA", "CLICK " + v);
        switch(v.getId()) {
            case (R.id.answer_incorrect): {
                mCause = getString(R.string.answer_incorrect);
                break;
            }
            case (R.id.question_incorrect): {
                mCause = getString(R.string.question_incorrect);
                break;
            }
            case (R.id.documentation_irrelevant): {
                mCause = getString(R.string.documentation_irrelevant);
                break;
            }
            default:
                mCause = "Not selected";
        }
    }
}