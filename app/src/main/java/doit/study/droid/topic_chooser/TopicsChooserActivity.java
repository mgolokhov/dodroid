package doit.study.droid.topic_chooser;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.mikepenz.materialdrawer.Drawer;

import java.util.List;

import javax.inject.Inject;

import doit.study.droid.R;
import doit.study.droid.app.App;
import doit.study.droid.data.source.Tag;
import doit.study.droid.data.source.local.QuizDatabase;
import doit.study.droid.utils.DrawerHelper;
import timber.log.Timber;


public class TopicsChooserActivity extends MvpAppCompatActivity implements TopicsChooserContract.View, SearchView.OnQueryTextListener{
    private final static boolean DEBUG = false;
    private Toast toast;

    @InjectPresenter
    TopicsChooserPresenter presenter;

    private Drawer drawer;
    private RecyclerView recyclerView;
    private TopicsAdapter topicsAdapter;
    @Inject
    QuizDatabase quizDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_topics_chooser);

        recyclerView = findViewById(R.id.topics_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        topicsAdapter = new TopicsAdapter();
        recyclerView.setAdapter(topicsAdapter);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = DrawerHelper.getDrawer(this, toolbar);

        final FloatingActionButton floatingActionButton = findViewById(R.id.commit_button);
    }


    @Override
    protected void onResume() {
        super.onResume();
        presenter.loadTopics();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_topics_chooser, menu);
        getMenuInflater().inflate(R.menu.show_total_summary, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.select_all:
                presenter.selectAllTopics(topicsAdapter.getTags());
                return true;
            case R.id.unselect_all:
                presenter.selectNoneTopics(topicsAdapter.getTags());
                return true;
            case R.id.total_summary:
                Timber.d("Start new activity");
//                Intent intent = new Intent(getActivity(), TotalSummaryActivity.class);
//                TaskStackBuilder builder = TaskStackBuilder.create(getContext());
//                builder.addNextIntentWithParentStack(intent);
//                builder.startActivities();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void updateViewModel(List<Tag> tags) {
        topicsAdapter.setTags(tags);
    }

    @Override
    protected void onDestroy() {
        if (toast != null){
            toast.cancel();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        presenter.handleBackButton(drawer != null && drawer.isDrawerOpen());
    }


    @Override
    public void navigateToInterrogator() {

    }

    @Override
    public void navigateToTotalSummary() {

    }

    @Override
    public void showToastHowToExit() {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(getBaseContext(), getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void closeDrawer() {
        if (drawer != null){
            drawer.closeDrawer();
        }
    }

    @Override
    public void exit() {
        super.onBackPressed();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        presenter.filterBySearch(topicsAdapter.getTags(), newText);
        return true;
    }

    @Override
    public void showFilteredTopics(List<Tag> tags) {
        topicsAdapter.animateTo(tags);
        // don't know why but with scrollToPosition get buggy behavior
        recyclerView.smoothScrollToPosition(0);
    }
}

