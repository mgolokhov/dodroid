package doit.study.droid.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import java.util.Arrays;

import doit.study.droid.R;
import doit.study.droid.data.Question;
import doit.study.droid.data.QuizProvider;
import doit.study.droid.data.RelationTables;
import doit.study.droid.data.Tag;
import timber.log.Timber;

public class TotalSummaryFragment extends LifecycleLogFragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private final static boolean DEBUG = true;
    private TextView mTotalQuestions;
    private TextView mViewed;
    private TextView mAlmostStudied;
    private TextView mStudied;

    private static final int QUESTION_LOADER = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (DEBUG) Timber.d("onCreateView");
        View v = inflater.inflate(R.layout.fragment_total_summary, parent, false);
        mTotalQuestions = (TextView) v.findViewById(R.id.total_quesitons);
        mViewed= (TextView) v.findViewById(R.id.viewed);
        mAlmostStudied = (TextView) v.findViewById(R.id.almost_studied);
        mStudied = (TextView) v.findViewById(R.id.studied);
        getLoaderManager().initLoader(QUESTION_LOADER, null, this);
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (DEBUG) Timber.d("onCreateLoader");
        switch (id){
            case QUESTION_LOADER:
                String [] projection = Arrays.copyOf(RelationTables.JoinedQuestionTagProjection,
                        RelationTables.JoinedQuestionTagProjection.length + 1);
                projection[RelationTables.JoinedQuestionTagProjection.length] = "group_concat( " + Tag.Table.FQ_TEXT + ", '\n' ) as tags2";
                return new CursorLoader(getActivity(), QuizProvider.QUESTION_URI, projection, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (DEBUG) Timber.d("onLoadFinished");
        switch(loader.getId()){
            case QUESTION_LOADER:
                if (DEBUG) Timber.d("QUESTION_LOADER Total questions: %d", data.getCount());
                mTotalQuestions.setText(String.format(getContext().getResources().getString(R.string.total_questions),
                        data.getCount()));
                int viewed = 0;
                int almostStudied = 0;
                int studied = 0;
                while(data.moveToNext()){
                    Question q = Question.newInstance(data);
                    if (q.isStudied()){
                        studied++;
                        viewed++;
                    } else if (q.getConsecutiveRightCnt() != 0) {
                        almostStudied++;
                        viewed++;
                    } else if (q.getRightAnsCnt() != 0 || q.getWrongAnsCnt() != 0){
                        viewed++;
                    }
                }
                mStudied.setText(String.format(getContext().getResources().getString(R.string.studied_questions), studied));
                mAlmostStudied.setText(String.format(getContext().getResources().getString(R.string.almost_studied_questions), almostStudied));
                mViewed.setText(String.format(getContext().getResources().getString(R.string.viewed_questions), viewed));
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
