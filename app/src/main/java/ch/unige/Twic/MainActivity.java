package ch.unige.Twic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import ch.unige.Twic.Twic.Exceptions.TwicException;
import ch.unige.Twic.Twic.PairsList;
import ch.unige.Twic.Twic.TwicFields;
import ch.unige.Twic.Twic.TwicXmlParser;
import ch.unige.Twic.Twic.tabs.Tab1Fragment;
import ch.unige.Twic.Twic.tabs.Tab2Fragment;
import ch.unige.Twic.listeners.SendListener;
import ch.unige.Twic.listeners.WifiState;
import ch.unige.Twic.listeners.WifiStateObserver;


public class MainActivity extends FragmentActivity implements WifiStateObserver  {

    /**
     * Ui instances
     */
    private static Button sendButton;
    private static TextView flashView;
    private static EditTextCustom inputField;
    private static TextView responseView;
    private static Spinner spinSrc, spinDest;
    private static SeekBar wordPositionSlider;
    private static TextView wordPositionView;
    private FragmentTabHost mTabHost;
    WifiState wifiState;

    public MainActivity() {
        Log.e("FUCK","Init");
        wifiState = WifiState.getWifiState();
        wifiState.addObserver(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container, new PlaceholderFragment())
//                    .commit();
//        }

        setContentView(R.layout.fragment_main);
        Intent intent = getIntent();
        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator("Tab1"),
                Tab1Fragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator("Tab2"),
                Tab2Fragment.class, null);
        sendButton = (Button) findViewById(R.id.sendButton);
        responseView = (TextView) findViewById(R.id.responseView);
        inputField = (EditTextCustom) findViewById(R.id.inputSentenceField);

        sendButton.setOnClickListener(new SendListener(this));

        flashView = (TextView) findViewById(R.id.flashView);

        spinSrc = (Spinner) findViewById(R.id.spinSrc);
        spinDest = (Spinner) findViewById(R.id.spinDest);

        wordPositionSlider = (SeekBar) findViewById(R.id.wordPositionSlider);
        wordPositionView = (TextView) findViewById(R.id.wordPositionView);

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
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
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
                wordPositionSlider.setMax(s.length());
                inputField.setSelection(currentPosition);
                wordPositionSlider.setProgress(inputField.getSelectionStart());
            }
        });

        // Load language pairs and codeNames
        foobar(wifiState.isOnline(getContext()));

        // Get intent, action and MIME type
        String action = intent.getAction();
        String type = intent.getType();

        if (type != null)
            if (Intent.ACTION_SEND.equals(action) && "text/plain".equals(type)) {
                handleSendText(intent);
            } else {
                flashView.setText(R.string.shareError);
            }
    }

    @Override
    public void update(WifiState observable, Boolean isOnline) {
        foobar(isOnline);
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    void foobar(Boolean isOnline) {
        if(isOnline) {
            flashView.setText("");
            initSpinners();
            sendButton.setEnabled(true);
        } else {
            flashView.setText(R.string.connexionError);
            sendButton.setEnabled(false);
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

    private void initSpinners(){
        try {
            TwicXmlParser.parseLanguagelist(WebService.callUrl(TwicFields.LANGUAGELISTURL));
            List<String> initList = PairsList.getSrcList(true);
            fillSpinner(spinSrc, initList);
            fillSpinner(spinDest, PairsList.getTgtFromSrc(initList.get(0), true));
        } catch (TwicException e) {
            flashView.setText(R.string.serverError);
        }

        spinSrc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_item, PairsList.getTgtBySrcId(position));
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinDest.setAdapter(dataAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            if (sharedText.charAt(0) == '"')
                sharedText = sharedText.substring(1, sharedText.lastIndexOf('"'));
            inputField.setText(sharedText);
        }
    }

    private void fillSpinner(Spinner spinner, List<String> list) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }
}

