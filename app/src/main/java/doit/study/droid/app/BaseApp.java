package doit.study.droid.app;

import android.app.Application;
import android.os.UserManager;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.tasks.RuntimeExecutionException;

import doit.study.droid.BuildConfig;
import doit.study.droid.R;
import doit.study.droid.data.QuizDBHelper;
import doit.study.droid.data.QuizDataClient;
import io.fabric.sdk.android.Fabric;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Use application singleton
*/
public abstract class BaseApp extends Application {
    private Tracker mTracker;
    private static final String API_BASE_URL = "https://raw.githubusercontent.com/mgolokhov/dodroid_questions/master/";
    private QuizDataClient mQuizDataClient;


    @Override
    public void onCreate() {
        super.onCreate();
        CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build(), new Crashlytics());
    }

    // Get the tracker associated with this app
    public void startTracking() {

        // Initialize an Analytics tracker using a Google Analytics property ID.

        // Does the Tracker already exist?
        // If not, create it
        if (mTracker == null) {
            GoogleAnalytics ga = GoogleAnalytics.getInstance(this);

            // Get the config data for the tracker
            mTracker = ga.newTracker(R.xml.track_app);

            // Enable tracking of activities
            ga.enableAutoActivityReports(this);
        }
    }

    public Tracker getTracker() {
        // Make sure the tracker exists
        startTracking();

        // Then return the tracker
        return mTracker;
    }

    public QuizDataClient getQuizService(){
        if (mQuizDataClient == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            //httpClient.addInterceptor(createLogInterceptor());

            Retrofit.Builder builder = new Retrofit.Builder()
                            .baseUrl(API_BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create());

            Retrofit retrofit = builder
                            .client(httpClient.build())
                            .build();

            mQuizDataClient = retrofit.create(QuizDataClient.class);
        }
        return mQuizDataClient;
    }


    private HttpLoggingInterceptor createLogInterceptor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        HttpLoggingInterceptor.Level level = BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE;
        return logging.setLevel(level);
    }
}
