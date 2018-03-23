package doit.study.droid.topic_chooser;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import doit.study.droid.app.App;
import doit.study.droid.data.source.Tag;
import doit.study.droid.data.source.local.QuizDatabase;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@InjectViewState
public class TopicsChooserPresenter extends MvpPresenter<TopicsChooserContract.View> implements TopicsChooserContract.Presenter{
    // desired time passed between two clicks before exit
    private static final int TIME_INTERVAL = 2_000; // milliseconds
    private long backPressedTime = 0;
    @Inject
    QuizDatabase quizDatabase;

    public TopicsChooserPresenter(){
        App.getAppComponent().inject(this);
    }


    @Override
    public void navigateToInterrogatorClicked() {
        getViewState().navigateToInterrogator();
    }

    @Override
    public void navigateToTotalSummaryClicked() {
        getViewState().navigateToTotalSummary();
    }

    @Override
    public void handleBackButton(boolean drawerIsOpen) {
        if (drawerIsOpen) {
            getViewState().closeDrawer();
        } else if (System.currentTimeMillis() > backPressedTime + TIME_INTERVAL){
            backPressedTime = System.currentTimeMillis();
            getViewState().showToastHowToExit();
        } else {
            getViewState().exit();
        }
    }

    @Override
    public void loadTopics() {
        Disposable disposable = quizDatabase.getQuizDao().getTagStatistics()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // onNext
                        tags -> {
                            //topicsAdapter.setTags(tags);
                            getViewState().updateViewModel(tags);
                            Timber.d(Arrays.toString(tags.toArray()));
                        },
                        throwable -> {},
                        () -> {}
                );
    }

    @Override
    public void selectTopic(int tagId) {

    }

    @Override
    public void selectAllTopics(List<Tag> tags) {
        setSelectionToAllTags(tags, true);
    }

    @Override
    public void selectNoneTopics(List<Tag> tags) {
        setSelectionToAllTags(tags, false);
    }

    private void setSelectionToAllTags(List<Tag> tags, boolean checked) {
        for (Tag tag : tags)
            tag.setChecked(checked);
        getViewState().updateViewModel(tags);
    }

    @Override
    public void filterBySearch(List<Tag> model, String query) {
        query = query.toLowerCase();
        final List<Tag> filteredModel = new ArrayList<>();
        for (Tag tag: model){
            final String text = tag.getText().toLowerCase();
            if (text.contains(query)){
                filteredModel.add(tag);
            }
        }
        getViewState().showFilteredTopics(filteredModel);
    }
}
