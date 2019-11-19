package doit.study.droid.common

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import doit.study.droid.R
import doit.study.droid.utils.lazyAndroid


open class MainDrawerActivity : AppCompatActivity() {
    private val navController: NavController by lazyAndroid {
        findNavController(R.id.nav_host_fragment)
    }
    private val appBarConfiguration: AppBarConfiguration by lazyAndroid {
        AppBarConfiguration.Builder(setOf(R.id.topic_fragment_dest))
                .setDrawerLayout(findViewById(R.id.drawer_layout))
                .build()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_drawer)

        setupActionBar()
        setupDrawerNavMenu()
    }

    private fun setupDrawerNavMenu() {
        findViewById<NavigationView>(R.id.navigation_view).apply {
            setupWithNavController(navController)
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        // This allows NavigationUI to decide what label to show in the action bar
        // By using appBarConfig, it will also determine whether to
        // show the up arrow or drawer menu icon
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        // Allows NavigationUI to support proper up navigation or the drawer layout
        // drawer menu, depending on the situation
        return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration)
    }





    companion object {
        private const val TIME_WINDOW_IN_MS = 2_000
    }
}