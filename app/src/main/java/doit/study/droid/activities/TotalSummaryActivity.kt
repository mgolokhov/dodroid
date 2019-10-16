package doit.study.droid.activities


import android.os.Bundle

import doit.study.droid.R
import doit.study.droid.fragments.TotalSummaryFragment
import timber.log.Timber

class TotalSummaryActivity : DrawerBaseActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        if (DEBUG) Timber.d("onCreate")
        super.onCreate(savedInstanceState)
        selectionId = NONE_SELECTED
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container_content, TotalSummaryFragment())
                .commit()
    }

    companion object {
        private const val DEBUG = true
    }
}
