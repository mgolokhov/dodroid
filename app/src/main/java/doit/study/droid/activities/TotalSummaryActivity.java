package doit.study.droid.activities;


import android.os.Bundle;
import doit.study.droid.R;
import doit.study.droid.fragments.TotalSummaryFragment;
import timber.log.Timber;

public class TotalSummaryActivity extends DrawerBaseActivity {
    private final static boolean DEBUG = true;
    // getTotalQuestions
    // getTotalStudied
    // getViewed
    // getStrugglingQuestions
    // timeInvested ?


    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (DEBUG) Timber.d("onCreate");
        super.onCreate(savedInstanceState);
        mSelectionId = NONE_SELECTED;
//        getLayoutInflater().inflate(R.layout.activity_total_summary, mContainerContent);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_content, new TotalSummaryFragment())
                .commit();
    }
}
