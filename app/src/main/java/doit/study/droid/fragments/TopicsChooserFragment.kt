package doit.study.droid.fragments

import android.content.ContentProviderOperation
import android.content.ContentProviderResult
import android.content.Intent
import android.content.OperationApplicationException
import android.database.Cursor
import android.os.Bundle
import android.os.Parcelable
import android.os.RemoteException
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.app.TaskStackBuilder
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.android.material.floatingactionbutton.FloatingActionButton

import java.util.ArrayList

import doit.study.droid.R
import doit.study.droid.activities.InterrogatorActivity
import doit.study.droid.activities.TotalSummaryActivity
import doit.study.droid.adapters.TopicsAdapter
import doit.study.droid.data.Question
import doit.study.droid.data.QuizProvider
import doit.study.droid.data.Tag
import timber.log.Timber


class TopicsChooserFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener {
    private var mTopicsAdapter: TopicsAdapter? = null
    private var mRecyclerView: RecyclerView? = null
    private var mMasterCopyTags: MutableList<Tag> = ArrayList()
    // loaders resets state, have to save in var
    private var mSavedRecyclerLayoutState: Parcelable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        if (DEBUG) Timber.d("onCreate")
        loaderManager.initLoader(TAG_LOADER, null, this)
        loaderManager.initLoader(QUESTION_LOADER, null, this)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_topics_chooser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRecyclerView = view.findViewById<View>(R.id.topics_view) as RecyclerView
        //        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView!!.layoutManager = LinearLayoutManager(context)
        mTopicsAdapter = TopicsAdapter()
        mRecyclerView!!.adapter = mTopicsAdapter
        val floatingActionButton = view.findViewById<View>(R.id.commit_button) as FloatingActionButton
        floatingActionButton.setOnClickListener {
            val intent = Intent(context, InterrogatorActivity::class.java)
            val builder = TaskStackBuilder.create(context!!)
            builder.addNextIntentWithParentStack(intent)
            builder.startActivities()
        }
        if (savedInstanceState != null) {   // restore scroll position
            if (DEBUG) Timber.d("Restore recycler state")
            mSavedRecyclerLayoutState = savedInstanceState.getParcelable(RECYCLER_LAYOUT_STATE_KEY)
            mRecyclerView!!.layoutManager!!.onRestoreInstanceState(mSavedRecyclerLayoutState)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater)
        menuInflater.inflate(R.menu.activity_topic, menu)
        menuInflater.inflate(R.menu.show_total_summary, menu)

        val item = menu.findItem(R.id.action_search)
        val searchView = MenuItemCompat.getActionView(item) as SearchView
        searchView.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.select_all -> {
                setSelectionToAllTags(true)
                return true
            }
            R.id.unselect_all -> {
                setSelectionToAllTags(false)
                return true
            }
            R.id.total_summary -> {
                Timber.d("Start new activity")
                val intent = Intent(activity, TotalSummaryActivity::class.java)
                val builder = TaskStackBuilder.create(context!!)
                builder.addNextIntentWithParentStack(intent)
                builder.startActivities()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun setSelectionToAllTags(checked: Boolean) {
        for (tag in mMasterCopyTags)
            tag.setChecked(checked)
        mTopicsAdapter!!.notifyDataSetChanged()
    }

    override fun onPause() {
        if (DEBUG) Timber.d("onPause")
        object : Thread() {
            override fun run() {
                if (mTopicsAdapter == null)
                    return
                val selected = StringBuilder()
                val unselected = StringBuilder()
                for (tag in mTopicsAdapter!!.tags!!) {
                    if (tag.selectionStatus) {
                        appendSelection(selected, tag.id!!)
                    } else {
                        appendSelection(unselected, tag.id!!)
                    }
                }
                val ops = ArrayList<ContentProviderOperation>()
                var builder: ContentProviderOperation.Builder
                if (selected.length != 0) {
                    builder = ContentProviderOperation.newUpdate(QuizProvider.TAG_URI)
                            .withValue(Tag.Table.SELECTED, true)
                            .withSelection(selected.toString(), null)
                    ops.add(builder.build())
                }
                if (unselected.length != 0) {
                    builder = ContentProviderOperation.newUpdate(QuizProvider.TAG_URI)
                            .withValue(Tag.Table.SELECTED, false)
                            .withSelection(unselected.toString(), null)
                    ops.add(builder.build())
                }
                try {
                    val res = activity!!.contentResolver.applyBatch(QuizProvider.AUTHORITY, ops)
                    if (DEBUG) Timber.d("Update result: %d", res.size)
                } catch (e: RemoteException) {
                    Timber.e(e, null)
                } catch (e: OperationApplicationException) {
                    Timber.e(e, null)
                }

            }

            private fun appendSelection(s: StringBuilder, id: Int) {
                if (s.length != 0)
                    s.append(" OR ")
                s.append(Tag.Table._ID).append(" = ").append(id)
            }
        }.start()
        super.onPause()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // save scroll position
        if (DEBUG) Timber.d("onSaveInstanceState")
        outState.putParcelable(RECYCLER_LAYOUT_STATE_KEY, mRecyclerView!!.layoutManager!!.onSaveInstanceState())
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        if (DEBUG) Timber.d("onCreateLoader")
        when (id) {
            TAG_LOADER -> return CursorLoader(activity!!, QuizProvider.TAG_URI, null, null, null, null)
            QUESTION_LOADER -> return CursorLoader(activity!!, QuizProvider.QUESTION_URI, arrayOf(Question.Table.FQ_ID), null, null, null)
            else -> return throw Exception("Wrong id for CursorLoader")
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        if (DEBUG) Timber.d("onLoadFinished")
        when (loader.id) {
            TAG_LOADER -> {
                mMasterCopyTags = ArrayList()
                while (data.moveToNext()) {
                    mMasterCopyTags.add(Tag.newInstance(data))
                }
                if (DEBUG) Timber.d("TAG_LOADER Loaded size: %d", mMasterCopyTags.size)
                mTopicsAdapter!!.tags = mMasterCopyTags
            }
            QUESTION_LOADER -> if (DEBUG) Timber.d("QUESTION_LOADER Total questions: %d", data.count)
            else -> {
            }
        }
        if (mSavedRecyclerLayoutState != null) {
            if (DEBUG) Timber.d("Restore layout in loader")
            mRecyclerView!!.layoutManager!!.onRestoreInstanceState(mSavedRecyclerLayoutState)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        if (DEBUG) Timber.d("onLoaderReset")
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        val filteredModel = filter(mMasterCopyTags, newText)
        mTopicsAdapter!!.animateTo(filteredModel)
        // don't know why but with scrollToPosition get buggy behavior
        mRecyclerView!!.smoothScrollToPosition(0)
        return true
    }

    private fun filter(model: List<Tag>, query: String): List<Tag> {
        var query = query
        query = query.toLowerCase()
        val filteredModel = ArrayList<Tag>()
        for (tag in model) {
            val text = tag.name.toLowerCase()
            if (text.contains(query)) {
                filteredModel.add(tag)
            }
        }
        return filteredModel
    }

    companion object {
        private const val DEBUG = false
        private const val RECYCLER_LAYOUT_STATE_KEY = "doit.study.droid.fragments.recycler_layout_state_key"
        private const val TAG_LOADER = 0
        private const val QUESTION_LOADER = 1

        @JvmStatic
        fun newInstance(): TopicsChooserFragment {
            return TopicsChooserFragment()
        }
    }

}
