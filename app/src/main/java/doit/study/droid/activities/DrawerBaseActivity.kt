package doit.study.droid.activities

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import doit.study.droid.utils2.Distribution.getVersion
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
    protected var containerContent: FrameLayout? = null
    protected var selectionId = R.id.nav_set_topic

    private var drawer: DrawerLayout? = null
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null
    private var navigationView: NavigationView? = null
    private var toolbar: Toolbar? = null

    protected val isNavDrawerOpen: Boolean
        get() = drawer?.isDrawerOpen(GravityCompat.START) ?: false

    override fun onCreate(savedInstanceState: Bundle?) {
        if (DEBUG) Timber.d("onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_drawer)

        containerContent = findViewById(R.id.container_content)

        setupToolbar()
        setupDrawer()
    }

    private fun setupToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    private fun setupDrawer() {
        drawer = findViewById(R.id.drawer_layout)
        actionBarDrawerToggle = object : ActionBarDrawerToggle(this,
                drawer,
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

        drawer!!.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle!!.syncState()

        navigationView = findViewById(R.id.navigation_view)
        navigationView!!.itemIconTintList = null
        setNavTitle()
    }

    private fun setNavTitle() {
        val header = navigationView!!.getHeaderView(0)
        navigationView!!.setNavigationItemSelectedListener(this)
        val tv = header.findViewById<TextView>(R.id.version_num_header)
        tv.text = getVersion(this)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        if (DEBUG) Timber.d("onPostCreate")
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle?.syncState()
    }

    override fun onResume() {
        if (DEBUG) Timber.d("onResume $selectionId")
        super.onResume()
        navigationView!!.setCheckedItem(selectionId)
    }

    override fun onBackPressed() {
        if (DEBUG) Timber.d("onBackPressed")
        if (isNavDrawerOpen) {
            closeNavDrawer()
        } else {
            super.onBackPressed()
        }
    }

    protected fun closeNavDrawer() = drawer?.closeDrawer(GravityCompat.START)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (DEBUG) Timber.d("onOptionsItemSelected")
        // The action bar home/up action should open or close the drawer.
        return if (actionBarDrawerToggle!!.onOptionsItemSelected(item)) true
        else super.onOptionsItemSelected(item)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        if (DEBUG) Timber.d("onConfigurationChanged")
        super.onConfigurationChanged(newConfig)
        // Pass any configuration change to the drawer toggles
        actionBarDrawerToggle!!.onConfigurationChanged(newConfig)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        Timber.d("selected: %d", menuItem.itemId)
        when (menuItem.itemId) {
            R.id.nav_get_motivation -> {
                navigateToYoutubeVideo()
            }
            R.id.nav_set_topic -> createBackStack(Intent(this, TopicsChooserActivity::class.java))
            R.id.nav_do_it -> createBackStack(Intent(this, InterrogatorActivity::class.java))
            else -> {
            }
        }
        drawer?.closeDrawer(GravityCompat.START)

        return true
    }

    private fun navigateToYoutubeVideo(){
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(getString(R.string.url_motivational_video))
        startActivity(intent)
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
        const val NONE_SELECTED = -1
    }
}
