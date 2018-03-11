//package doit.study.droid.fragments;
//
//import android.content.ContentProviderOperation;
//import android.content.ContentProviderResult;
//import android.content.Intent;
//import android.content.OperationApplicationException;
//import android.database.Cursor;
//import android.os.Bundle;
//import android.os.Parcelable;
//import android.os.RemoteException;
//import android.support.annotation.Nullable;
//import android.support.design.widget.FloatingActionButton;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.LoaderManager;
//import android.support.v4.app.TaskStackBuilder;
//import android.support.v4.content.CursorLoader;
//import android.support.v4.content.Loader;
//import android.support.v4.view.MenuItemCompat;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.SearchView;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import doit.study.droid.R;
//import doit.study.droid.activities.InterrogatorActivity;
//import doit.study.droid.activities.TotalSummaryActivity;
//import doit.study.droid.adapters.TopicsAdapter;
//import doit.study.droid.data.Question;
//import doit.study.droid.data.Tag;
//import timber.log.Timber;
//
//
//public class TopicsChooserFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener{
//    private final static boolean DEBUG = false;
//    private static final String RECYCLER_LAYOUT_STATE_KEY = "doit.study.droid.fragments.recycler_layout_state_key";
//    private TopicsAdapter mTopicsAdapter;
//    private RecyclerView mRecyclerView;
//    private static final int TAG_LOADER = 0;
//    private static final int QUESTION_LOADER = 1;
//    private List<Tag> mMasterCopyTags = new ArrayList<>();
//    // loaders resets state, have to save in var
//    private Parcelable mSavedRecyclerLayoutState;
//
//    public TopicsChooserFragment() {
//        // Required empty public constructor
//    }
//
//    public static TopicsChooserFragment newInstance() {
//        TopicsChooserFragment fragment = new TopicsChooserFragment();
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
//
//        if (DEBUG) Timber.d("onCreate");
//        getLoaderManager().initLoader(TAG_LOADER, null, this);
//        getLoaderManager().initLoader(QUESTION_LOADER, null, this);
//
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.fragment_topics_chooser, container, false);
//        return v;
//    }
//
//    @Override
//    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        mRecyclerView = (RecyclerView) view.findViewById(R.id.topics_view);
////        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        mTopicsAdapter = new TopicsAdapter();
//        mRecyclerView.setAdapter(mTopicsAdapter);
//        final FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.commit_button);
//        floatingActionButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), InterrogatorActivity.class);
//                TaskStackBuilder builder = TaskStackBuilder.create(getContext());
//                builder.addNextIntentWithParentStack(intent);
//                builder.startActivities();
//            }
//        });
//        if(savedInstanceState != null)
//        {   // restore scroll position
//            if (DEBUG) Timber.d("Restore recycler state");
//            mSavedRecyclerLayoutState = savedInstanceState.getParcelable(RECYCLER_LAYOUT_STATE_KEY);
//            mRecyclerView.getLayoutManager().onRestoreInstanceState(mSavedRecyclerLayoutState);
//        }
//    }
//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
//        super.onCreateOptionsMenu(menu, menuInflater);
//        menuInflater.inflate(R.menu.activity_topic, menu);
//        menuInflater.inflate(R.menu.show_total_summary, menu);
//
//        final MenuItem item = menu.findItem(R.id.action_search);
//        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
//        searchView.setOnQueryTextListener(this);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.select_all:
//                setSelectionToAllTags(true);
//                return true;
//            case R.id.unselect_all:
//                setSelectionToAllTags(false);
//                return true;
//            case R.id.total_summary:
//                Timber.d("Start new activity");
//                Intent intent = new Intent(getActivity(), TotalSummaryActivity.class);
//                TaskStackBuilder builder = TaskStackBuilder.create(getContext());
//                builder.addNextIntentWithParentStack(intent);
//                builder.startActivities();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
//
//    private void setSelectionToAllTags(boolean checked){
//        for (Tag tag: mMasterCopyTags)
//            tag.setChecked(checked);
//        mTopicsAdapter.notifyDataSetChanged();
//    }
//
//    @Override
//    public void onPause() {
//        if (DEBUG) Timber.d("onPause");
//        new Thread(){
//            @Override
//            public void run() {
//                if (mTopicsAdapter == null)
//                    return;
//                StringBuilder selected = new StringBuilder();
//                StringBuilder unselected = new StringBuilder();
//                for (Tag tag: mTopicsAdapter.getTags()) {
//                    if (tag.getSelectionStatus()) {
//                        appendSelection(selected, tag.getId());
//                    }
//                    else {
//                        appendSelection(unselected, tag.getId());
//                    }
//                }
//                ArrayList<ContentProviderOperation> ops = new ArrayList<>();
//                ContentProviderOperation.Builder builder;
//                if (selected.length() != 0) {
//                    builder = ContentProviderOperation.newUpdate(QuizProvider.TAG_URI)
//                            .withValue(Tag.Table.SELECTED, true)
//                            .withSelection(selected.toString(), null);
//                    ops.add(builder.build());
//                }
//                if (unselected.length() != 0) {
//                    builder = ContentProviderOperation.newUpdate(QuizProvider.TAG_URI)
//                            .withValue(Tag.Table.SELECTED, false)
//                            .withSelection(unselected.toString(), null);
//                    ops.add(builder.build());
//                }
//                try {
//                    ContentProviderResult[] res = getActivity().getContentResolver().applyBatch(QuizProvider.AUTHORITY, ops);
//                    if (DEBUG) Timber.d("Update result: %d", res.length);
//                } catch (RemoteException | OperationApplicationException e) {
//                    Timber.e(e, null);
//                }
//            }
//            private void appendSelection(StringBuilder s, int id){
//                if (s.length() != 0)
//                    s.append(" OR ");
//                s.append(Tag.Table._ID).append(" = ").append(id);
//            }
//        }.start();
//        super.onPause();
//    }
//
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        // save scroll position
//        if (DEBUG) Timber.d("onSaveInstanceState");
//        outState.putParcelable(RECYCLER_LAYOUT_STATE_KEY, mRecyclerView.getLayoutManager().onSaveInstanceState());
//    }
//
//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        if (DEBUG) Timber.d("onCreateLoader");
//        switch(id){
//            case TAG_LOADER:
//                return new CursorLoader(getActivity(), QuizProvider.TAG_URI, null, null, null, null);
//            case QUESTION_LOADER:
//                return new CursorLoader(getActivity(), QuizProvider.QUESTION_URI, new String[]{Question.Table.FQ_ID}, null, null, null);
//            default:
//                return null;
//        }
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        if (DEBUG) Timber.d("onLoadFinished");
//        switch(loader.getId()){
//            case TAG_LOADER:
//                mMasterCopyTags = new ArrayList<>();
//                while(data.moveToNext()){
//                    mMasterCopyTags.add(Tag.newInstance(data));
//                }
//                if (DEBUG) Timber.d("TAG_LOADER Loaded size: %d", mMasterCopyTags.size());
//                mTopicsAdapter.setTags(mMasterCopyTags);
//                break;
//            case QUESTION_LOADER:
//                if (DEBUG) Timber.d("QUESTION_LOADER Total questions: %d", data.getCount());
//                break;
//            default:
//                break;
//        }
//        if (mSavedRecyclerLayoutState != null) {
//            if (DEBUG) Timber.d("Restore layout in loader");
//            mRecyclerView.getLayoutManager().onRestoreInstanceState(mSavedRecyclerLayoutState);
//        }
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//        if (DEBUG) Timber.d("onLoaderReset");
//    }
//
//    @Override
//    public boolean onQueryTextSubmit(String query) {
//        return false;
//    }
//
//    @Override
//    public boolean onQueryTextChange(String newText) {
//        final List<Tag> filteredModel = filter(mMasterCopyTags, newText);
//        mTopicsAdapter.animateTo(filteredModel);
//        // don't know why but with scrollToPosition get buggy behavior
//        mRecyclerView.smoothScrollToPosition(0);
//        return true;
//    }
//
//    private List<Tag> filter (List<Tag> model, String query){
//        query = query.toLowerCase();
//        final List<Tag> filteredModel = new ArrayList<>();
//        for (Tag tag: model){
//            final String text = tag.getName().toLowerCase();
//            if (text.contains(query)){
//                filteredModel.add(tag);
//            }
//        }
//        return filteredModel;
//    }
//
//}
