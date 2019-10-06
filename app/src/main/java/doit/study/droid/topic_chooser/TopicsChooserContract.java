package doit.study.droid.topic_chooser;

import com.arellomobile.mvp.MvpView;

import java.util.List;

public interface TopicsChooserContract {
    interface View extends MvpView{
        void navigateToInterrogator();
        void navigateToTotalSummary();
        void showFilteredTopics(List<TopicModel> topics);
        void updateTopics(List<TopicModel> topics);
        void showToastHowToExit();
        void closeDrawer();
        void exit();
    }
    interface Presenter {
        void navigateToInterrogatorClicked();
        void navigateToTotalSummaryClicked();
        void handleBackButton(boolean drawerIsOpen);
        void loadTopics();
        void selectTopic(TopicModel topic);
        void deselectTopic(TopicModel topic);
        void selectAllTopics(List<TopicModel> topics);
        void deselectAllTopics(List<TopicModel> topics);
        void filterBySearch(List<TopicModel> topics, String query);
    }
}
