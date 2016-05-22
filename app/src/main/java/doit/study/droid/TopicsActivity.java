package doit.study.droid;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import doit.study.droid.data.Question;
import doit.study.droid.data.QuizProvider;
import doit.study.droid.data.Tag;
import timber.log.Timber;


public class TopicsActivity extends ActivityWithDrawer implements LoaderManager.LoaderCallbacks<Cursor>{
    private final static boolean DEBUG = false;
    private TopicAdapter mTopicAdapter;
    private static final int TAG_LOADER = 0;
    private static final int QUESTION_LOADER = 1;
    private List<Tag> mTags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.topics_layout, mFrameLayout);
        getSupportLoaderManager().initLoader(TAG_LOADER, null, this);
        getSupportLoaderManager().initLoader(QUESTION_LOADER, null, this);

        RecyclerView rv = (RecyclerView) findViewById(R.id.topics_view);
        rv.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        rv.addItemDecoration(new TopicAdapter.DividerItemDecoration(this, R.drawable.divider));
        mTopicAdapter = new TopicAdapter();
        rv.setAdapter(mTopicAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_topic, menu);
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
        for (Tag tag: mTags)
            tag.setChecked(checked);
        mTopicAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
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
                    Timber.d("Update result: %d", res.length);
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
        switch(loader.getId()){
            case TAG_LOADER:
                while(data.moveToNext()){
                    mTags.add(Tag.newInstance(data));
                }
                mTopicAdapter.setTags(mTags);
                break;
            case QUESTION_LOADER:
                setTitle("Total questions: " + data.getCount());
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}

