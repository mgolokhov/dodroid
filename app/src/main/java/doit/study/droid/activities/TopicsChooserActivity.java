package doit.study.droid.activities;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import doit.study.droid.R;
import doit.study.droid.adapters.TopicAdapter;
import doit.study.droid.data.Question;
import doit.study.droid.data.QuizProvider;
import doit.study.droid.data.Tag;
import timber.log.Timber;


public class TopicsChooserActivity extends DrawerBaseActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener{
    private final static boolean DEBUG = false;
    private TopicAdapter mTopicAdapter;
    private RecyclerView mRecyclerView;
    private static final int TAG_LOADER = 0;
    private static final int QUESTION_LOADER = 1;
    private List<Tag> mMasterCopyTags = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG) Timber.d("onCreate");
        getLayoutInflater().inflate(R.layout.activity_topics_chooser, mFrameLayout);
        getSupportLoaderManager().initLoader(TAG_LOADER, null, this);
        getSupportLoaderManager().initLoader(QUESTION_LOADER, null, this);

        mRecyclerView = (RecyclerView) findViewById(R.id.topics_view);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
//        mRecyclerView.addItemDecoration(new TopicAdapter.DividerItemDecoration(this, R.drawable.divider));
        mTopicAdapter = new TopicAdapter();
        mRecyclerView.setAdapter(mTopicAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_topic, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.select_all:
                setSelectionToAllTags(true);
                return true;
            case R.id.unselect_all:
                setSelectionToAllTags(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setSelectionToAllTags(boolean checked){
        for (Tag tag: mMasterCopyTags)
            tag.setChecked(checked);
        mTopicAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        if (DEBUG) Timber.d("onPause");
        new Thread(){
            @Override
            public void run() {
                StringBuilder selected = new StringBuilder();
                StringBuilder unselected = new StringBuilder();
                for (Tag tag: mTopicAdapter.getTags()) {
                    if (tag.getSelectionStatus()) {
                        appendSelection(selected, tag.getId());
                    }
                    else {
                        appendSelection(unselected, tag.getId());
                    }
                }
                ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                ContentProviderOperation.Builder builder;
                if (selected.length() != 0) {
                    builder = ContentProviderOperation.newUpdate(QuizProvider.TAG_URI)
                            .withValue(Tag.Table.SELECTED, true)
                            .withSelection(selected.toString(), null);
                    ops.add(builder.build());
                }
                if (unselected.length() != 0) {
                    builder = ContentProviderOperation.newUpdate(QuizProvider.TAG_URI)
                            .withValue(Tag.Table.SELECTED, false)
                            .withSelection(unselected.toString(), null);
                    ops.add(builder.build());
                }
                try {
                    ContentProviderResult[] res = getContentResolver().applyBatch(QuizProvider.AUTHORITY, ops);
                    if (DEBUG) Timber.d("Update result: %d", res.length);
                } catch (RemoteException | OperationApplicationException e) {
                    e.printStackTrace();
                }
            }
            private void appendSelection(StringBuilder s, int id){
                if (s.length() != 0)
                    s.append(" OR ");
                s.append(Tag.Table._ID).append(" = ").append(id);
            }
        }.start();
        super.onPause();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (DEBUG) Timber.d("onCreateLoader");
        switch(id){
            case TAG_LOADER:
                return new CursorLoader(this, QuizProvider.TAG_URI, null, null, null, null);
            case QUESTION_LOADER:
                return new CursorLoader(this, QuizProvider.QUESTION_URI, new String[]{Question.Table._ID}, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (DEBUG) Timber.d("onLoadFinished");
        switch(loader.getId()){
            case TAG_LOADER:
                mMasterCopyTags = new ArrayList<>();
                while(data.moveToNext()){
                    mMasterCopyTags.add(Tag.newInstance(data));
                }
                if (DEBUG) Timber.d("TAG_LOADER Loaded size: %d", mMasterCopyTags.size());
                mTopicAdapter.setTags(mMasterCopyTags);
                break;
            case QUESTION_LOADER:
                if (DEBUG) Timber.d("QUESTION_LOADER Total questions: %d", data.getCount());
                setTitle("Total questions: " + data.getCount());
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (DEBUG) Timber.d("onLoaderReset");

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<Tag> filteredModel = filter(mMasterCopyTags, newText);
        mTopicAdapter.animateTo(filteredModel);
        // don't know why but with scrollToPosition get buggy behavior
        mRecyclerView.smoothScrollToPosition(0);
        return true;
    }

    private List<Tag> filter (List<Tag> model, String query){
        query = query.toLowerCase();
        final List<Tag> filteredModel = new ArrayList<>();
        for (Tag tag: model){
            final String text = tag.getName().toLowerCase();
            if (text.contains(query)){
                filteredModel.add(tag);
            }
        }
        return filteredModel;
    }
}

