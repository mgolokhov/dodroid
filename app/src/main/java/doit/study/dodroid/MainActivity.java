package doit.study.dodroid;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


// Entry point for the app.
// Because we set in manifest action=MAIN category=LAUNCHER
public class MainActivity extends FragmentActivity  {



    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private final int PAGE_COUNT = 3; // number of pages/tabs to use for viewpager


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_layout);

        mPager = (ViewPager)findViewById(R.id.view_pager);
        mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);


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
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, mainFrag)
                    .commit();

        }
    }



    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
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


    // No need for replaceFragment if using viewpager/swiping, fragment_container returns null then, so can replace fragments based in that container

//    @Override
//    public void replaceFragment(Class frag, Bundle args) {
//
//        android.support.v4.app.Fragment newFrag = null;
//
//        try {
//            newFrag = (android.support.v4.app.Fragment) frag.newInstance();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        // Pass arguments if any
//        if (args != null)
//            newFrag.setArguments(args);
//
//
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.fragment_container, newFrag)
//                .addToBackStack(null)
//                .commit();
//
//    }


    private class MainPagerAdapter extends FragmentPagerAdapter{

        public MainPagerAdapter(FragmentManager fm){
            super(fm);
        }


        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position){
                case 0: return MainFragment.newInstance();
                case 1: return QuestionsFragment.newInstance();
                case 2: return TestFragment.newInstance();
                default: return null;
            }
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }



        @Override
        public CharSequence getPageTitle(int position){

            switch (position){
                case 0: return "Maxin";
                case 1: return "Loves";
                case 2: return "Janice";
                default: return null;
            }
        }
    }


}
