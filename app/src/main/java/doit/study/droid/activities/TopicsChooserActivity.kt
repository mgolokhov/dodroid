package doit.study.droid.activities

import android.os.Bundle
import android.widget.Toast

import doit.study.droid.R
import doit.study.droid.fragments.TopicsChooserFragment
import timber.log.Timber


class TopicsChooserActivity : DrawerBaseActivity() {
    private var toast: Toast? = null
    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        if (DEBUG) Timber.d("onCreate")
        super.onCreate(savedInstanceState)
        mSelectionId = R.id.nav_set_topic
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
        if (isNavDrawerOpen) {
            closeNavDrawer()
            return
        } else if (backPressedTime + TIME_BETWEEN_TWO_BACK_PRESSES_MS > System.currentTimeMillis()) {
            super.onBackPressed()
            return
        } else {
            toast = Toast.makeText(baseContext, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT)
            toast?.show()
        }
        backPressedTime = System.currentTimeMillis()
    }

    companion object {
        private const val DEBUG = false
        private const val TIME_BETWEEN_TWO_BACK_PRESSES_MS = 2_000
    }
}

