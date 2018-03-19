package doit.study.droid.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import doit.study.droid.R;
import doit.study.droid.activities.TotalSummaryActivity;
import doit.study.droid.adapters.TopicsAdapter;
import doit.study.droid.app.App;
import doit.study.droid.data.source.Tag;
import doit.study.droid.data.source.local.QuizDatabase;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

//import doit.study.droid.activities.InterrogatorActivity;


public class TopicsChooserFragment extends Fragment implements SearchView.OnQueryTextListener{
    private final static boolean DEBUG = false;
    private static final String RECYCLER_LAYOUT_STATE_KEY = "doit.study.droid.fragments.recycler_layout_state_key";
    private TopicsAdapter topicsAdapter;
    private RecyclerView recyclerView;
    private Parcelable savedRecyclerLayoutState;
    private Disposable disposable;

    @Inject
    QuizDatabase quizDatabase;

    public static TopicsChooserFragment newInstance() {
        TopicsChooserFragment fragment = new TopicsChooserFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        App.getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (DEBUG) Timber.d("onCreate");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_topics_chooser, container, false);
        return v;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.topics_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        topicsAdapter = new TopicsAdapter();
        recyclerView.setAdapter(topicsAdapter);

        final FloatingActionButton floatingActionButton = view.findViewById(R.id.commit_button);
        floatingActionButton.setOnClickListener(v -> {
//                Intent intent = new Intent(getContext(), InterrogatorActivity.class);
//                TaskStackBuilder builder = TaskStackBuilder.create(getContext());
//                builder.addNextIntentWithParentStack(intent);
//                builder.startActivities();
        });
        if(savedInstanceState != null) {   // restore scroll position
            if (DEBUG) Timber.d("Restore recycler state");
            savedRecyclerLayoutState = savedInstanceState.getParcelable(RECYCLER_LAYOUT_STATE_KEY);
            recyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }

        disposable = quizDatabase.getQuizDao().getTagStatistics()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // onNext
                        tags -> {
                            topicsAdapter.setTags(tags);
                            Timber.d(Arrays.toString(tags.toArray()));
                            },
                        throwable -> {},
                        () -> {}
                );
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.activity_topic, menu);
        menuInflater.inflate(R.menu.show_total_summary, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
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
            case R.id.total_summary:
                Timber.d("Start new activity");
                Intent intent = new Intent(getActivity(), TotalSummaryActivity.class);
                TaskStackBuilder builder = TaskStackBuilder.create(getContext());
                builder.addNextIntentWithParentStack(intent);
                builder.startActivities();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setSelectionToAllTags(boolean checked){
//        for (TagEntity tag: mMasterCopyTags)
//            tag.setChecked(checked);
        topicsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        if (DEBUG) Timber.d("onPause");
        super.onPause();
        List<Tag> tags = topicsAdapter.getTags();
        List<Integer> checkedTagIds = new ArrayList<>();
        for(Tag t: tags){
            if (t.checkedAnyQuestion) {
                checkedTagIds.add(t.getId());
            }
        }
        Completable update = Completable.fromAction(() -> quizDatabase
                .statisticsDao()
                .updateCheckedQuestionsByTags(checkedTagIds.toArray(new Integer[checkedTagIds.size()])));

        Observable.concat(update.toObservable(), quizDatabase.getQuizDao().getTagStatistics().toObservable())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
                updated -> Timber.d("updated: " + updated.get(0)),
                error -> Timber.e(error),
                () -> {}

        );
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save scroll position
        if (DEBUG) Timber.d("onSaveInstanceState");
        outState.putParcelable(RECYCLER_LAYOUT_STATE_KEY, recyclerView.getLayoutManager().onSaveInstanceState());
    }




    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<Tag> filteredModel = filter(topicsAdapter.getTags(), newText);
        topicsAdapter.animateTo(filteredModel);
        // don't know why but with scrollToPosition get buggy behavior
        recyclerView.smoothScrollToPosition(0);
        return true;
    }

    private List<Tag> filter (List<Tag> model, String query){
        query = query.toLowerCase();
        final List<Tag> filteredModel = new ArrayList<>();
        for (Tag tag: model){
            final String text = tag.text.toLowerCase();
            if (text.contains(query)){
                filteredModel.add(tag);
            }
        }
        return filteredModel;
    }

}
