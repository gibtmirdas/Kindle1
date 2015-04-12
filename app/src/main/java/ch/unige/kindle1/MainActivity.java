package ch.unige.kindle1;

import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
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
    private static EditTextCustom inputField;
    private static TextView responseView;
    private static Spinner spinSrc, spinDest;
    private static SeekBar wordPositionSlider;
    private static TextView wordPositionView;
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


        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        } else {
            // Handle other intents, such as being started from the home screen
        }

    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
        }
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
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
            inputField = (EditTextCustom) rootView.findViewById(R.id.inputSentenceField);

            sendButton.setOnClickListener(new SendListener(rootView));

            spinSrc = (Spinner) rootView.findViewById(R.id.spinSrc);
            spinDest = (Spinner) rootView.findViewById(R.id.spinDest);

            wordPositionSlider = (SeekBar) rootView.findViewById(R.id.wordPositionSlider);
            wordPositionView = (TextView) rootView.findViewById(R.id.wordPositionView);

            // Word position logic
            wordPositionSlider.setMax(inputField.length());
            inputField.setWordPositionSlider(wordPositionSlider);
            wordPositionSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    inputField.setSelection(progress, inputField.getSelectionEnd());
                    inputField.setCursorVisible(true);
                    wordPositionView.setText(""+progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            inputField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    int currentPosition = inputField.getSelectionStart();
//                    if(wordPositionSlider.getProgress() > s.length())
//                        wordPositionSlider.setProgress(s.length());
                    wordPositionSlider.setMax(s.length());
                    inputField.setSelection(currentPosition);
                    wordPositionSlider.setProgress(inputField.getSelectionStart());
                }
            });

            // Load only once language pairs and codeNames
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
