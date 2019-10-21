package doit.study.droid.activities

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.getVersion
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.TaskStackBuilder
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

import com.google.android.material.navigation.NavigationView

import doit.study.droid.R
import timber.log.Timber


open class DrawerBaseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var mDrawer: DrawerLayout? = null
    protected var mContainerContent: FrameLayout? = null
    private var mActionBarDrawerToggle: ActionBarDrawerToggle? = null
    private var mNavigationView: NavigationView? = null
    protected var NONE_SELECTED = -1
    protected var mSelectionId = R.id.nav_set_topic

    protected val isNavDrawerOpen: Boolean
        get() = mDrawer != null && mDrawer!!.isDrawerOpen(GravityCompat.START)


    override fun onCreate(savedInstanceState: Bundle?) {
        if (DEBUG) Timber.d("onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_drawer)

        mContainerContent = findViewById<View>(R.id.container_content) as FrameLayout
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)


        mDrawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        mActionBarDrawerToggle = object : ActionBarDrawerToggle(this,
                mDrawer,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close) {
            // hide keyboard
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            }
        }

        mDrawer!!.addDrawerListener(mActionBarDrawerToggle!!)
        mActionBarDrawerToggle!!.syncState()

        mNavigationView = findViewById<View>(R.id.navigation_view) as NavigationView
        mNavigationView!!.itemIconTintList = null
        setNavTitle()
    }

    private fun setNavTitle() {
        val header = mNavigationView!!.getHeaderView(0)
        mNavigationView!!.setNavigationItemSelectedListener(this)
        val tv = header.findViewById<View>(R.id.version_num_header) as TextView
        tv.text = getVersion(this)
    }


    override fun onPostCreate(savedInstanceState: Bundle?) {
        if (DEBUG) Timber.d("onPostCreate")
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mActionBarDrawerToggle!!.syncState()
    }

    override fun onResume() {
        if (DEBUG) Timber.d("onResume %d", mSelectionId)
        super.onResume()
        mNavigationView!!.setCheckedItem(mSelectionId)
    }

    override fun onBackPressed() {
        if (DEBUG) Timber.d("onBackPressed")
        if (isNavDrawerOpen) {
            closeNavDrawer()
        } else {
            super.onBackPressed()
        }
    }

    protected fun closeNavDrawer() {
        if (mDrawer != null) {
            mDrawer!!.closeDrawer(GravityCompat.START)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (DEBUG) Timber.d("onOptionsItemSelected")
        // The action bar home/up action should open or close the drawer.
        return if (mActionBarDrawerToggle!!.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        if (DEBUG) Timber.d("onConfigurationChanged")
        super.onConfigurationChanged(newConfig)
        // Pass any configuration change to the drawer toggles
        mActionBarDrawerToggle!!.onConfigurationChanged(newConfig)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        Timber.d("selected: %d", menuItem.itemId)
        when (menuItem.itemId) {
            R.id.nav_get_motivation -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(getString(R.string.url_motivational_video))
                startActivity(intent)
            }
            R.id.nav_set_topic -> createBackStack(Intent(this, TopicsChooserActivity::class.java))
            R.id.nav_do_it -> createBackStack(Intent(this, InterrogatorActivity::class.java))
            else -> {
            }
        }
        mDrawer!!.closeDrawer(GravityCompat.START)

        return true
    }


    /**
     * Enables back navigation for activities that are launched from the NavBar. See
     * `AndroidManifest.xml` to find out the parent activity names for each activity.
     *
     * @param intent
     */
    private fun createBackStack(intent: Intent) {
        val builder = TaskStackBuilder.create(this)
        builder.addNextIntentWithParentStack(intent)
        builder.startActivities()
    }

    companion object {
        private const val DEBUG = false
    }
}
