package doit.study.droid.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import doit.study.droid.activities.SplashActivity;
import doit.study.droid.topic_chooser.TopicsChooserActivity;
import doit.study.droid.topic_chooser.TopicsChooserPresenter;

@Singleton
@Component(modules = {AppModule.class, NetworkModule.class})
public interface AppComponent {
    void inject(Application application);
    void inject(SplashActivity activity);
    void inject(TopicsChooserActivity activity);
    void inject(TopicsChooserPresenter presenter);
    void inject(TopicsChooserFragment fragment);
}
