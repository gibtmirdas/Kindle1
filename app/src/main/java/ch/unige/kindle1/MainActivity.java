package ch.unige.kindle1;

import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import ch.unige.kindle1.Twic.PairsList;
import ch.unige.kindle1.Twic.TwicFields;
import ch.unige.kindle1.Twic.TwicXmlParser;
import ch.unige.kindle1.listeners.SendListener;


public class MainActivity extends ActionBarActivity {

    /**
     * Ui instances
     */
    private static Button sendButton;
    private static EditText inputField;
    private static TextView responseView;
    private static Spinner spinSrc, spinDest;
    private static boolean startingFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            sendButton = (Button) rootView.findViewById(R.id.sendButton);
            responseView = (TextView) rootView.findViewById(R.id.responseView);
            inputField = (EditText) rootView.findViewById(R.id.inputSentenceField);


            sendButton.setOnClickListener(new SendListener(sendButton, responseView));
            inputField.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    inputField.setText("");
                }
            });

            spinSrc = (Spinner) rootView.findViewById(R.id.spinSrc);
            spinDest = (Spinner) rootView.findViewById(R.id.spinDest);
            if(startingFlag){
                initSpinners(rootView);
                startingFlag = false;
            }
            return rootView;
        }

        private void initSpinners(final View rootView){
            TwicXmlParser.parseLanguagelist(WebService.callUrl(TwicFields.LANGUAGELISTURL));
            List<String> initList = PairsList.getSrcList(true);
            fillSpinner(rootView, spinSrc, initList);
            fillSpinner(rootView, spinDest, PairsList.getTgtFromSrc(initList.get(0), true));

            spinSrc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(rootView.getContext(),
                            android.R.layout.simple_spinner_item, PairsList.getTgtBySrcId(position));
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinDest.setAdapter(dataAdapter);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        }

        private void fillSpinner(View rootView, Spinner spinner, List<String> list){
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(rootView.getContext(),
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(dataAdapter);
        }
    }
}
