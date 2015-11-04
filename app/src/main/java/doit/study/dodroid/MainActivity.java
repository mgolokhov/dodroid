package doit.study.dodroid;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;

// Entry point for the app.
// Because we set in manifest action=MAIN category=LAUNCHER
public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if(findViewById(R.id.fragment_container) != null){

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments
            if(savedInstanceState != null){
                return;
            }

            // insert the MainFragment into the Activity as the start of the application
            MainFragment mainFrag = new MainFragment();
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction()
                    .add(R.id.fragment_container, mainFrag)
                    .commit();
        }


    }

}
