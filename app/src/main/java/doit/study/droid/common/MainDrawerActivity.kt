package doit.study.droid.common

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import doit.study.droid.R
import doit.study.droid.activities.TopicFragment
import doit.study.droid.quiz.QuizMainFragment
import doit.study.droid.utils.Distribution.getVersion


open class MainDrawerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var containerContent: FrameLayout
    lateinit var drawer: DrawerLayout
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var navigationView: NavigationView
    lateinit var toolbar: Toolbar
    private var toast: Toast? = null
    private var previousTimeBackPressed: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_drawer)

        containerContent = findViewById(R.id.container_content)

        setupToolbar()
        setupDrawer()
        setupContent(savedInstanceState)
    }

    private fun setupContent(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container_content, TopicFragment.newInstance())
                    .commit()
        }
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
                R.string.drawer_close
        ) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                hideKeyboard()
            }
        }

        drawer.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView = findViewById(R.id.navigation_view)
        navigationView.itemIconTintList = null
        setNavTitle()
    }

    private fun hideKeyboard(){
        currentFocus?.let {
            it.clearFocus()
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun setNavTitle() {
        val header = navigationView.getHeaderView(0)
        navigationView.setNavigationItemSelectedListener(this)
        val tv = header.findViewById<TextView>(R.id.version_num_header)
        tv.text = getVersion(this)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState()
    }

    override fun onBackPressed() {
        when {
            drawer.isDrawerOpen(GravityCompat.START) -> {
                closeNavDrawer()
                return
            }
            else -> {
                supportFragmentManager.findFragmentById(R.id.container_content)?.let {
                    when (it) {
                        is TopicFragment -> {
                            when {
                                isTwoSequentialBackPressed() -> {
                                    super.onBackPressed()
                                    return
                                }
                                else -> {
                                    toast = Toast.makeText(baseContext, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT)
                                    toast?.show()
                                }
                            }
                        }
                        else -> {
                            if (supportFragmentManager.backStackEntryCount > 0) {
                                supportFragmentManager.popBackStack()
                            } else {
                                super.onBackPressed()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun isTwoSequentialBackPressed(): Boolean {
        val currentTime = System.currentTimeMillis()
        if (currentTime < previousTimeBackPressed + TIME_WINDOW_IN_MS)
            return true
        else {
            previousTimeBackPressed = currentTime
            return false
        }
    }

    protected fun closeNavDrawer() = drawer.closeDrawer(GravityCompat.START)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // The action bar home/up action should open or close the drawer.
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) true
        else super.onOptionsItemSelected(item)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Pass any configuration change to the drawer toggles
        actionBarDrawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        var fragment: Fragment? = null
        when(menuItem.itemId) {
            R.id.nav_get_motivation -> {
                navigateToYoutubeVideo()
            }
            R.id.nav_set_topic -> {
                fragment = TopicFragment.newInstance()
            }
            R.id.nav_do_it -> {
                fragment = QuizMainFragment.newInstance()
            }
            else -> {

            }
        }
        fragment?.let {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container_content, it)
                    .addToBackStack(null)
                    .commit()
        }

        menuItem.isChecked = true
        title = menuItem.title
        drawer.closeDrawers()
        return true
    }

    private fun navigateToYoutubeVideo(){
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(getString(R.string.url_motivational_video))
        startActivity(intent)
    }

    companion object {
        private const val TIME_WINDOW_IN_MS = 2_000
    }
}