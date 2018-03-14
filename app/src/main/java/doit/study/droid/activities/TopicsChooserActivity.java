package doit.study.droid.activities;

import android.os.Bundle;
import android.widget.Toast;

import doit.study.droid.R;
import doit.study.droid.fragments.TopicsChooserFragment;
import timber.log.Timber;


public class TopicsChooserActivity extends DrawerBaseActivity {
    private final static boolean DEBUG = false;
    private Toast mToast;
    private static final int TIME_INTERVAL = 2000; // milliseconds, desired time passed between two back presses.
    private long mBackPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (DEBUG) Timber.d("onCreate");
        super.onCreate(savedInstanceState);
		mSelectionId = R.id.nav_set_topic;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_content, TopicsChooserFragment.newInstance())
                .commit();
    }


    @Override
    protected void onDestroy()
    {
        if (mToast != null) mToast.cancel();
        super.onDestroy();
    }

    @Override
    public void onBackPressed()
    {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
            return;
        } else if (mBackPressedTime + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        }
        else {
            mToast = Toast.makeText(getBaseContext(), getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT);
            mToast.show();
        }
        mBackPressedTime = System.currentTimeMillis();
    }
}

