package doit.study.droid.activities

import android.os.Bundle
import android.widget.Toast
import doit.study.droid.R
import doit.study.droid.fragments.TopicsChooserFragment


class TopicsChooserActivity : DrawerBaseActivity() {
    private var toast: Toast? = null
    private var previousTimeBackPressed: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectionId = R.id.nav_set_topic
        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container_content, TopicsChooserFragment.newInstance())
                    .commit()
        }
    }


    override fun onDestroy() {
        toast?.cancel()
        super.onDestroy()
    }

    override fun onBackPressed() {
        when {
            isNavDrawerOpen -> {
                closeNavDrawer()
                return
            }
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

    private fun isTwoSequentialBackPressed(): Boolean {
        val currentTime = System.currentTimeMillis()
        if (currentTime < previousTimeBackPressed + TIME_WINDOW_IN_MS)
            return true
        else {
            previousTimeBackPressed = currentTime
            return false
        }
    }

    companion object {
        private const val TIME_WINDOW_IN_MS = 2_000
    }
}

