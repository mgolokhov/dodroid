package doit.study.dodroid;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MainFragment extends Fragment {


    // Shia LaBeouf - Just Do it! (Auto-tuned)
    private final String URL = "http://www.youtube.com/watch?v=gJscrxxl_Bg";
    // Define logging tag so it easier to filter messages
    private final String LOG_TAG = "NSA " + getClass().getName();
    // Link to the resource file, in our case it's a json file
    // I think we can say it some kind of descriptor, so it's an integer
    private final Integer mTestFile = R.raw.tests;
    // Actually our quiz is a list of questions
    private ArrayList<Question> mQuestions = new ArrayList<>();

    private Button doitButton;
    Button needMotivationButton;

    private OnFragmentInteractionListener mCallback;


    // Provided stub factory method
    public static MainFragment newInstance() {

        MainFragment fragment = new MainFragment();

        // add Bundle args if needed here before returning new instance of this class

        return fragment;
    }

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /* You can not use the findViewById method the way you can in an Activity in a Fragment
         * So we get a reference to the view/layout_file that we used for this Fragment
         * That allows use to then reference the views by id in that file
         */
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // You can not add onclick listener to a button in a fragment's xml
        needMotivationButton = (Button) view.findViewById(R.id.need_motivation);
        needMotivationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Create implicit intent
                Intent motivationIntent = new Intent(Intent.ACTION_VIEW);
                // Parser defines type of data
                motivationIntent.setData(Uri.parse(URL));
                // Action type + data type (extracted by uri parser)
                // should start youtube app or browser app
                startActivity(motivationIntent);
            }
        });
//        doitButton = (Button) view.findViewById(R.id.doit);
//        doitButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCallback.replaceFragment(QuestionsFragment.class, null);
//            }
//        });

        return view;
    }


    // Will need to update this method to the non-deprecated version of this that now takes a Context argument
    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);

//        // try to setup the connection with the hosting Activity so that callback methods can be used for cross fragment communication
//        try{
//            mCallback = (OnFragmentInteractionListener) context;
//        }catch (ClassCastException e) {
//            throw new ClassCastException(getActivity().toString()
//                    + " must implement OnHeadlineSelectedListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null; // clean up reference
    }


    // Android studio generated stub interface here. Added new replaceFragment method
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    // This is not needed for the replacing fragments ability if we are using viewpager, can still implement fucntions for data passing between fragments though
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
//        public void onFragmentInteraction(Uri uri);
        void replaceFragment(Class fragment, Bundle args);
    }

}
