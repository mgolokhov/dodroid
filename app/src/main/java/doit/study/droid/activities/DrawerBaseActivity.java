package doit.study.droid.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import doit.study.droid.R;
import timber.log.Timber;

import static doit.study.droid.utils.Distribution.getVersion;

public class DrawerBaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final boolean DEBUG = false;
    private DrawerLayout mDrawer;
    protected FrameLayout mContainerContent;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private NavigationView mNavigationView;
    protected int mSelectionId = R.id.nav_set_topic;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (DEBUG) Timber.d("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_drawer);

        mContainerContent = (FrameLayout) findViewById(R.id.container_content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawer,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close) {
            // hide keyboard
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        };

        mDrawer.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setItemIconTintList(null);
        setNavTitle();
    }

    private void setNavTitle() {
        View header = mNavigationView.getHeaderView(0);
        mNavigationView.setNavigationItemSelectedListener(this);
        TextView tv = (TextView) header.findViewById(R.id.version_num_header);
        tv.setText(getVersion(this));
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        if (DEBUG) Timber.d("onPostCreate");
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mActionBarDrawerToggle.syncState();
    }

    @Override
    protected void onResume() {
        if (DEBUG) Timber.d("onResume %d", mSelectionId);
        super.onResume();
        mNavigationView.setCheckedItem(mSelectionId);
    }

    @Override
    public void onBackPressed() {
        if (DEBUG) Timber.d("onBackPressed");
        if (isNavDrawerOpen()) {
            closeNavDrawer();
        } else {
            super.onBackPressed();
        }
    }

    protected boolean isNavDrawerOpen() {
        return mDrawer != null && mDrawer.isDrawerOpen(GravityCompat.START);
    }

    protected void closeNavDrawer() {
        if (mDrawer != null) {
            mDrawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (DEBUG) Timber.d("onOptionsItemSelected");
        // The action bar home/up action should open or close the drawer.
        if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (DEBUG) Timber.d("onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        Timber.d("selected: %d", menuItem.getItemId());
        switch (menuItem.getItemId()) {
            case R.id.nav_get_motivation:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(getString(R.string.url_motivational_video)));
                startActivity(intent);
                break;
            case R.id.nav_set_topic:
                createBackStack(new Intent(this, TopicsChooserActivity.class));
                break;
            case R.id.nav_do_it:
                createBackStack(new Intent(this, InterrogatorActivity.class));
                break;
            default:
                break;
        }
        mDrawer.closeDrawer(GravityCompat.START);

        return true;
    }


    /**
     * Enables back navigation for activities that are launched from the NavBar. See
     * {@code AndroidManifest.xml} to find out the parent activity names for each activity.
     *
     * @param intent
     */
    private void createBackStack(Intent intent) {
        TaskStackBuilder builder = TaskStackBuilder.create(this);
        builder.addNextIntentWithParentStack(intent);
        builder.startActivities();
    }
}
