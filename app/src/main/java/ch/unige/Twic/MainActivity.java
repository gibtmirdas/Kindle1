package ch.unige.Twic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
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
import ch.unige.Twic.Twic.tabs.InfoTab;
import ch.unige.Twic.Twic.tabs.ItsTab;
import ch.unige.Twic.Twic.tabs.MicrosoftTab;
import ch.unige.Twic.Twic.tabs.TabManager;
import ch.unige.Twic.Twic.tabs.TwicTab;
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
    private FragmentTabHost tabHost;
    WifiState wifiState;
    private final int spinnerStyleItem = R.layout.spinner_item;
    private final int spinnerStyleDropdown = R.layout.spinner_dropwdown;

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
        Intent intent = getIntent();

        // Set size of EditText
        setContentView(R.layout.fragment_main);
        inputField = (EditTextCustom) findViewById(R.id.editText);
        Point p = new Point();
        getWindowManager().getDefaultDisplay().getSize(p);
        inputField.setWidth((int) (p.x * (3.0 / 5.0)));
        flashView = (TextView) findViewById(R.id.flashView);


        // Init tabs
        tabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        tabHost.addTab(tabHost.newTabSpec("twic").setIndicator("Twic"),
                TwicTab.class, null);
        tabHost.addTab(tabHost.newTabSpec("its").setIndicator("Its"),
                ItsTab.class, null);
        tabHost.addTab(tabHost.newTabSpec("microsoft").setIndicator("Microsoft"),
                MicrosoftTab.class, null);
        tabHost.addTab(tabHost.newTabSpec("info").setIndicator("Info"),
                InfoTab.class, null);

        TabManager tabManager = new TabManager(tabHost, this);
        tabHost.setOnTabChangedListener(tabManager);
        // Init 1st part fields

        sendButton = (Button) findViewById(R.id.buttonSend);
        sendButton.setOnClickListener(new SendListener(this, tabManager));
//

        spinSrc = (Spinner) findViewById(R.id.spinnerSrc);
        spinDest = (Spinner) findViewById(R.id.spinnerDst);
//
//        wordPositionSlider = (SeekBar) findViewById(R.id.wordPositionSlider);
//        wordPositionView = (TextView) findViewById(R.id.wordPositionView);
//
//        // Word position logic
//        wordPositionSlider.setMax(inputField.length());
//        inputField.setWordPositionSlider(wordPositionSlider);
//        wordPositionSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                inputField.setSelection(progress, inputField.getSelectionEnd());
//                inputField.setCursorVisible(true);
//                wordPositionView.setText(""+progress);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) { }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) { }
//        });
//        inputField.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                int currentPosition = inputField.getSelectionStart();
//                wordPositionSlider.setMax(s.length());
//                inputField.setSelection(currentPosition);
//                wordPositionSlider.setProgress(inputField.getSelectionStart());
//            }
//        });
//
//        // Load language pairs and codeNames
        handleWifiState(wifiState.isOnline(getContext()));

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
        handleWifiState(isOnline);
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    void handleWifiState(Boolean isOnline) {
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
//        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

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
                        spinnerStyleItem, PairsList.getTgtBySrcId(position));
                dataAdapter.setDropDownViewResource(spinnerStyleDropdown);
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
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
               spinnerStyleItem, list);
        dataAdapter.setDropDownViewResource(spinnerStyleDropdown);
        spinner.setAdapter(dataAdapter);
    }

    public static void flash(int msg){
        if(flashView != null)
            flashView.setText(msg);
    }

    public static void cleanFlash(){
        if(flashView != null)
            flashView.setText("");
    }
}