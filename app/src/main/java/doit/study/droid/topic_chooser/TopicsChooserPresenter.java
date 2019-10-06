package doit.study.droid.topic_chooser;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import doit.study.droid.app.App;
import doit.study.droid.data.source.QuestionsRepository;
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
    @Inject QuestionsRepository questionsRepository;

    public TopicsChooserPresenter(){
        App.getAppComponent().inject(this);
        questionsRepository.getQuestions();
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
        // load questions from repository
        // map questions to topics
        // update view

        List<TopicModel> topicModels = new ArrayList<>();
        topicModels.add(new TopicModel(0, "topic1", 10, 5,
                        new ArrayList<>(Arrays.asList(1, 2, 3)), true));
        topicModels.add(new TopicModel(1, "topic2", 10, 5,
                new ArrayList<>(Arrays.asList(1, 2, 3)), false));
        topicModels.add(new TopicModel(2, "topic3", 10, 5,
                new ArrayList<>(Arrays.asList(1, 2, 3)), true));

        getViewState().updateTopics(topicModels);

    }

    @Override
    public void selectTopic(TopicModel tag) {
        tag.setChecked(true);
        //TODO: update repo
    }

    @Override
    public void deselectTopic(TopicModel tag) {
        tag.setChecked(false);
        //TODO: update repo
    }


    @Override
    public void selectAllTopics(List<TopicModel> tags) {
        setSelectionToAllTags(tags, true);
        //TODO: update repo
    }

    @Override
    public void deselectAllTopics(List<TopicModel> tags) {
        setSelectionToAllTags(tags, false);
        //TODO: update repo
    }

    private void setSelectionToAllTags(List<TopicModel> tags, boolean checked) {
        List<Long> tagIds = new ArrayList<>(tags.size());
        for (TopicModel tag : tags) {
            tag.setChecked(checked);
            if (tag.isChecked()){
                tagIds.add(tag.getId());
            }
        }
        getViewState().updateTopics(tags);
    }

    @Override
    public void filterBySearch(List<TopicModel> model, String query) {
        query = query.toLowerCase();
        final List<TopicModel> filteredModel = new ArrayList<>();
        for (TopicModel tag: model){
            final String text = tag.getText().toLowerCase();
            if (text.contains(query)){
                filteredModel.add(tag);
            }
        }
        getViewState().showFilteredTopics(filteredModel);
    }
}
