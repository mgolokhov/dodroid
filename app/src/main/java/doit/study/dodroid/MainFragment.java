package doit.study.dodroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

        doitButton = (Button) view.findViewById(R.id.doit);
        doitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doitButtonHandler();
            }
        });

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

        return view;
    }


    // Will need to update this method to the non-deprecated version of this that now takes a Context argument
    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);

        // try to setup the connection with the hosting Activity so that callback methods can be used for cross fragment communication
        try{
            mCallback = (OnFragmentInteractionListener) context;
        }catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null; // clean up reference
    }


    public void doitButtonHandler(){
        Log.i(LOG_TAG, "doit clicked");
        String s = readFile();
        parseTests(s);

        Bundle args = new Bundle();
        args.putParcelableArrayList("questions", mQuestions);
        mCallback.replaceFragment(QuestionsFragment.class, args);

    }



    // Read raw data from resource file
    // Yeah, just read file, return contents
    private String readFile(){

        InputStream inputStream = getActivity().getApplicationContext().getResources().openRawResource(
                mTestFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream));

        StringBuffer buffer = new StringBuffer("");
        try {
            String line;
            while ((line = reader.readLine()) != null) {

                buffer.append(line);
            }

        } catch (IOException e) {
            Log.i(LOG_TAG, "IOException");
        }
        Log.i(LOG_TAG, buffer.toString());
        return buffer.toString();
    }


    // Parse and map json data to the Question object
    // Many questions => List of questions
    private void parseTests(String data){
        try {
            JSONArray questions = new JSONArray(data);
            for(int i=0; i < questions.length(); i++) {
                Question aQuestion = new Question();
                JSONObject currentQuestion = questions.getJSONObject(i);
                aQuestion.question = currentQuestion.getString("question");
                JSONArray wrongAnswers = currentQuestion.getJSONArray("wrong");
                for(int j=0; j<wrongAnswers.length(); j++){
                    aQuestion.wrong.add(wrongAnswers.get(j).toString());
                }
                JSONArray rightAnswers = currentQuestion.getJSONArray("right");
                for(int j=0; j<rightAnswers.length(); j++){
                    aQuestion.right.add(rightAnswers.get(j).toString());
                }
                mQuestions.add(aQuestion);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(LOG_TAG, mQuestions.toString());
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
//        public void onFragmentInteraction(Uri uri);
        public void replaceFragment(Class fragment, Bundle args);
    }

}
