package doit.study.dodroid;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

// Entry point for the app.
// Because we set in manifest action=MAIN category=LAUNCHER
public class MainActivity extends Activity implements MainFragment.OnFragmentInteractionListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments
            if (savedInstanceState != null) {
                return;
            }

            // insert the MainFragment into the Activity as the start of the application
            MainFragment mainFrag = new MainFragment();
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, mainFrag)
                    .commit();

        }
    }

    /*
     * Fragments are never suppose to directly communicate/interact with each other, so wanted functionality between fragments
     * should be implemented through an interface in the hosting Activity. So replaceFragment() is currently used to pass the questions list
     * from MainFragment to QuestionsFragment
     */

    /*
     * This  method would need to be moved out of MainFragment class and into a separate interface if this was to be used for more fragments added to the application later
     */
    @Override
    public void replaceFragment(Class frag, Bundle args) {

        Fragment newFrag = null;

        try {
            newFrag = (Fragment) frag.newInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Pass arguments if any
        if (args != null)
            newFrag.setArguments(args);


        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, newFrag)
                .addToBackStack(null)
                .commit();

    }

}
