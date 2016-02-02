package doit.study.droid;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;

import doit.study.droid.model.GlobalData;


// Entry point for the app.
// Because we set in manifest action=MAIN category=LAUNCHER
public class MainActivity extends AppCompatActivity {
    // Shia LaBeouf - Just Do it! (Auto-tuned)
    private final String URL = "http://www.youtube.com/watch?v=gJscrxxl_Bg";
    @SuppressWarnings("unused")
    private final String TAG = "NSA " + getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setVersionInTitle();
        // Make sure that Analytics tracking has started
        ((GlobalData) getApplication()).startTracking();
    }

    private void setVersionInTitle(){
        String title = getTitle().toString();
        try {
            title += " " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        setTitle(title);
    }

    public void motivationButton(View v){
        Intent motivIntent = new Intent(Intent.ACTION_VIEW);
        motivIntent.setData(Uri.parse(URL));
        startActivity(motivIntent);
    }

    public void setTopicButton(View v){
        Intent intent = new Intent(MainActivity.this, TopicsActivity.class);
        startActivity(intent);
    }

    public void doitButton(View v){
        Intent intent = new Intent(MainActivity.this, QuestionsActivity.class);
        startActivity(intent);
    }
}
