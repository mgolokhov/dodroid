package doit.study.droid.topic_chooser;

import com.arellomobile.mvp.MvpView;

import java.util.List;

import doit.study.droid.data.source.Tag;

public interface TopicsChooserContract {
    interface View extends MvpView{
        void navigateToInterrogator();
        void navigateToTotalSummary();
        void showFilteredTopics(List<Tag> tags);
        void updateViewModel(List<Tag> tags);
        void showToastHowToExit();
        void closeDrawer();
        void exit();
    }
    interface Presenter {
        void navigateToInterrogatorClicked();
        void navigateToTotalSummaryClicked();
        void handleBackButton(boolean drawerIsOpen);
        void loadTopics();
        void selectTopic(int tagId);
        void selectAllTopics(List<Tag> tags);
        void selectNoneTopics(List<Tag> tags);
        void filterBySearch(List<Tag> tags, String query);
    }
}
