package doit.study.droid.fragments

import android.content.ContentProviderOperation
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.os.Parcelable
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
    private var topicsAdapter: TopicsAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var masterCopyTags: MutableList<Tag> = ArrayList()
    // loaders resets state, have to save in var
    private var savedRecyclerLayoutState: Parcelable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        setupLoaders()
    }

    private fun setupLoaders() {
        loaderManager.initLoader(TAG_LOADER, null, this)
        loaderManager.initLoader(QUESTION_LOADER, null, this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_topics_chooser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLayout(view)
        // restore scroll position
        savedInstanceState?.let {
            if (DEBUG) Timber.d("Restore recycler state")
            savedRecyclerLayoutState = it.getParcelable(RECYCLER_LAYOUT_STATE_KEY)
            recyclerView?.layoutManager?.onRestoreInstanceState(savedRecyclerLayoutState)
        }
    }

    private fun setupLayout(view: View){
        recyclerView = view.findViewById(R.id.topics_view)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        topicsAdapter = TopicsAdapter()
        recyclerView?.adapter = topicsAdapter

        val floatingActionButton = view.findViewById(R.id.commit_button) as FloatingActionButton
        floatingActionButton.setOnClickListener {
            navigateToInterrogatorActivity()
        }
    }

    private fun navigateToInterrogatorActivity(){
        val intent = Intent(context, InterrogatorActivity::class.java)
        val builder = TaskStackBuilder.create(context!!)
        builder.addNextIntentWithParentStack(intent)
        builder.startActivities()
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
                navigateToTotalSummaryActivity()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun navigateToTotalSummaryActivity(){
        val intent = Intent(activity, TotalSummaryActivity::class.java)
        val builder = TaskStackBuilder.create(context!!)
        builder.addNextIntentWithParentStack(intent)
        builder.startActivities()
    }


    private fun setSelectionToAllTags(checked: Boolean) {
        masterCopyTags.forEach{ it.setChecked(checked) }
        topicsAdapter?.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
        topicsAdapter?.let {
            val ops = ArrayList<ContentProviderOperation>()
            createContentProviderOperationBuilder(forSelectedTag = false)?.let {
                ops.add(it.build())
            }
            createContentProviderOperationBuilder(forSelectedTag = true)?.let {
                ops.add(it.build())
            }
            try {
                val res = activity
                        ?.contentResolver
                        ?.applyBatch(QuizProvider.AUTHORITY, ops)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    private fun createContentProviderOperationBuilder(forSelectedTag: Boolean = false): ContentProviderOperation.Builder? {
        val selection = StringBuilder()
        topicsAdapter?.tags?.forEach{ tag ->
            if (forSelectedTag == tag.selectionStatus) {
                appendSelection(selection, tag.id)
            }
        }
        if (selection.isNotEmpty()) {
            return ContentProviderOperation.newUpdate(QuizProvider.TAG_URI)
                    .withValue(Tag.Table.SELECTED, forSelectedTag)
                    .withSelection(selection.toString(), null)
        }
        return null
    }


    private fun appendSelection(s: StringBuilder, id: Int) {
        if (s.isNotEmpty()) s.append(" OR ")
        s.append(Tag.Table._ID).append(" = ").append(id)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // save scroll position
        if (DEBUG) Timber.d("onSaveInstanceState")
        outState.putParcelable(RECYCLER_LAYOUT_STATE_KEY, recyclerView?.layoutManager?.onSaveInstanceState())
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        if (DEBUG) Timber.d("onCreateLoader")
        return when (id) {
            TAG_LOADER -> CursorLoader(activity!!, QuizProvider.TAG_URI, null, null, null, null)
            QUESTION_LOADER -> CursorLoader(activity!!, QuizProvider.QUESTION_URI, arrayOf(Question.Table.FQ_ID), null, null, null)
            else -> throw Exception("Wrong id for CursorLoader")
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        if (DEBUG) Timber.d("onLoadFinished")
        when (loader.id) {
            TAG_LOADER -> {
                masterCopyTags = ArrayList()
                while (data.moveToNext()) {
                    masterCopyTags.add(Tag.newInstance(data))
                }
                if (DEBUG) Timber.d("TAG_LOADER Loaded size: %d", masterCopyTags.size)
                topicsAdapter?.tags = masterCopyTags
            }
            QUESTION_LOADER ->
                if (DEBUG) Timber.d("QUESTION_LOADER Total questions: %d", data.count)
            else -> { }
        }
        if (savedRecyclerLayoutState != null) {
            if (DEBUG) Timber.d("Restore layout in loader")
            recyclerView?.layoutManager?.onRestoreInstanceState(savedRecyclerLayoutState)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        if (DEBUG) Timber.d("onLoaderReset")
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        val filteredModel = filter(masterCopyTags, newText)
        topicsAdapter?.animateTo(filteredModel)
        // don't know why but with scrollToPosition get buggy behavior
        recyclerView?.smoothScrollToPosition(0)
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
