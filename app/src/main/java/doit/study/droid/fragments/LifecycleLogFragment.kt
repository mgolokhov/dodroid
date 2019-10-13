package doit.study.droid.fragments


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment

import timber.log.Timber

/**
 * This abstract class extends the Fragment class and overrides lifecycle
 * callbacks for logging various lifecycle events.
 * See [
 * https://raw.githubusercontent.com/mgolokhov/android-lifecycle/master/complete_android_fragment_lifecycle.png
](the complete Activity/Fragment lifecycle) *
 */

abstract class LifecycleLogFragment : Fragment() {
    protected open var DEBUG = false

    private fun log(message: String) {
        if (DEBUG) Timber.d(message + " Hash: " + hashCode())
    }

    private fun logWithState(message: String, savedInstanceState: Bundle?) {
        if (DEBUG) {
            if (savedInstanceState == null)
                log("$message brand new")
            else
                log("$message re-creation")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        log("onAttach")
    }

    // Called to do _initial_ creation of a fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logWithState("onCreate", savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        logWithState("onCreateView", savedInstanceState)
        return super.onCreateView(inflater, parent, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        logWithState("onCreateView", savedInstanceState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        logWithState("onViewStateRestored", savedInstanceState)
    }

    // Called when the Fragment is visible to the user
    override fun onStart() {
        super.onStart()
        log("onStart")
    }

    override fun onResume() {
        super.onResume()
        log("onResume")
    }

    override fun onPause() {
        log("onPause")
        super.onPause()
    }

    override fun onStop() {
        log("onStop")
        super.onStop()
    }

    override fun onDestroyView() {
        log("onDestroyView")
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        log("onSaveInstanceState")
    }

    override fun onDestroy() {
        log("onDestroy")
        super.onDestroy()
    }

    override fun onDetach() {
        log("onDetach")
        super.onDetach()
    }
}
