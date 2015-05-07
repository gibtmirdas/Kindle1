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
    private static Spinner spinSrc, spinDest;
    private static Button prevButton, nextButton;

    private FragmentTabHost tabHost;
    WifiState wifiState;
    private final int spinnerStyleItem = R.layout.spinner_item;
    private final int spinnerStyleDropdown = R.layout.spinner_dropwdown;

    public MainActivity() {
        Log.e("Twic","Init");
        wifiState = WifiState.getWifiState();
        wifiState.addObserver(this);
    }


    private int[] findNextWord(String text, int position) {
        // Get next word start position
        String furtherText = text.substring(position);
        int nextWordStart = furtherText.indexOf(" ")+1;
        nextWordStart = position + (nextWordStart == -1 ? 0 : nextWordStart);

        // If there's many spaces between the two words, seek the next word true beginning
        while (nextWordStart+1 < text.length() && text.charAt(nextWordStart) == ' ')
            nextWordStart++;

        // Get next word end position
        furtherText = text.substring(nextWordStart);
        int nextWordEnd = furtherText.indexOf(" ");
        nextWordEnd = (nextWordEnd == -1 ? text.length() : nextWordStart + nextWordEnd);

        return new int[] {nextWordStart, nextWordEnd};
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

        spinSrc = (Spinner) findViewById(R.id.spinnerSrc);
        spinDest = (Spinner) findViewById(R.id.spinnerDst);


        prevButton = (Button) findViewById(R.id.buttonPrev);
        nextButton = (Button) findViewById(R.id.buttonNext);

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputField.getText().toString();
                int position = inputField.getSelectionEnd();

                // Reverse the text & indexes in order to search the "next" word (i.e. the previous)
                String rText = new StringBuilder(text).reverse().toString();
                int rPosition = text.length() - position;

                int[] nextWordPosition = findNextWord(rText, rPosition);

                int nextWordStart = text.length() - nextWordPosition[1];
                int nextWordEnd = text.length() - nextWordPosition[0];

                // Select next word
                inputField.requestFocus();
                inputField.setSelection(nextWordStart, nextWordEnd);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputField.getText().toString();
                int currentPosition = inputField.getSelectionStart();
                int[] nextWordPosition = findNextWord(text, currentPosition);

                int nextWordStart = nextWordPosition[0];
                int nextWordEnd = nextWordPosition[1];

                // Select next word
                inputField.requestFocus();
                inputField.setSelection(nextWordStart, nextWordEnd);
            }
        });

        // Load language pairs and codeNames
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
         return super.onOptionsItemSelected(item);
    }

    private void initSpinners(){
        try {
            TwicXmlParser.parseLanguagelist(WebService.callUrl(TwicFields.LANGUAGELISTURL));
            List<String> initList = PairsList.getSrcList(true);
            clearSpinner(spinSrc);
            clearSpinner(spinDest);
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

    private void clearSpinner(Spinner spinner) {
        spinner.setAdapter(null);
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