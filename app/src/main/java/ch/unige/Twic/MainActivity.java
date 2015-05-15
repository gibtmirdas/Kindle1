package ch.unige.Twic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import ch.unige.Twic.Twic.PairsList;
import ch.unige.Twic.Twic.TwicFields;
import ch.unige.Twic.Twic.TwicXmlParser;
import ch.unige.Twic.Twic.tabs.InfoTab;
import ch.unige.Twic.Twic.tabs.ItsTab;
import ch.unige.Twic.Twic.tabs.MicrosoftTab;
import ch.unige.Twic.Twic.tabs.TabManager;
import ch.unige.Twic.Twic.tabs.TwicTab;
import ch.unige.Twic.listeners.SendListener;
import ch.unige.Twic.listeners.ConnectivityState;
import ch.unige.Twic.listeners.ConnectivityStateObserver;


public class MainActivity extends FragmentActivity implements ConnectivityStateObserver, WebServiceObserver {

    /**
     * Ui instances
     */
    private static Button sendButton;
    private static TextView flashView;
    private static EditText inputField;
    private static Spinner spinSrc, spinDest;
    private static ImageButton keyboardButton;
    private static Button prevButton, nextButton;
    private FragmentTabHost tabHost;

    private ConnectivityState connectivityState;
    private final static int spinnerStyleItem = R.layout.spinner_item;

    private final static int spinnerStyleDropdown = R.layout.spinner_dropwdown;
    private static int positionSpinnerDst;

    private static boolean clickUpdateSpinner = true;


    /**
     * From the current cursor position, find the next word (starting and ending) position.
     * @param text      Search the next word in this text
     * @param position  Current position of the cursor (on the selected word)
     * @return A two dimensional array; index 0 = start position, index 1 = end position.
     */
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

    /**
     * Handle connexion/deconnexion of the wifi to update the UI and notify the user.
     * @param isOnline current state of the wifi.
     */
    private void handleWifiState(Boolean isOnline) {
        if(isOnline) {
            cleanFlash();
            new WebService(this).execute(TwicFields.LANGUAGELISTURL);
            sendButton.setEnabled(true);
        } else {
            flash(R.string.connexionError);
            sendButton.setEnabled(false);
        }
    }

    /**
     * Initialize the spinners with the language list and set their listeners.
     * @param languageList list of languages to load in the spinners
     */
    private void initSpinners(String languageList){
        TwicXmlParser.parseLanguageList(languageList);
        List<String> initList = PairsList.getSrcList(true);
        clearSpinner(spinSrc);
        clearSpinner(spinDest);
        fillSpinner(spinSrc, initList);
        fillSpinner(spinDest, PairsList.getTgtFromSrc(initList.get(0), true));

        spinSrc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(),
                        spinnerStyleItem, PairsList.getTgtBySrcId(position));
                dataAdapter.setDropDownViewResource(spinnerStyleDropdown);
                spinDest.setAdapter(dataAdapter);
                spinDest.setSelection(0, true);
                if (!clickUpdateSpinner) {
                    spinDest.setSelection(positionSpinnerDst);
                    clickUpdateSpinner = true;
                    positionSpinnerDst = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (!clickUpdateSpinner) {
                    spinDest.setSelection(positionSpinnerDst);
                    clickUpdateSpinner = true;
                    positionSpinnerDst = 0;
                }
            }
        });
        spinDest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clickUpdateSpinner = true;
                positionSpinnerDst = 0;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                clickUpdateSpinner = true;
                positionSpinnerDst = 0;
            }
        });
    }

    /**
     * Handle text shared from other applications in order to put them in the EditText.
     * @param intent
     */
    private void handleSendText(Intent intent) {
        Object extra = intent.getExtras().get(Intent.EXTRA_TEXT);
        if (extra != null) {
            String sharedText = extra.toString();
            // Text shared from the Kindle Books application is surrounded by quotes
            // If the shared text start with one, extract the text from them, and remove the rest.
            int lastQuotePosition = sharedText.lastIndexOf('"');
            if (sharedText.charAt(0) == '"' && lastQuotePosition > 0)
                sharedText = sharedText.substring(1, lastQuotePosition);
            inputField.setText(sharedText);
        }
    }

    /**
     * Clear the spinner content.
     * @param spinner component to clear
     */
    private void clearSpinner(Spinner spinner) {
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{""}));
    }

    /**
     * Fill a spinner with a list of string elements.
     * @param spinner component to fill
     * @param list list of string to load in the spinner
     */
    private void fillSpinner(Spinner spinner, List<String> list) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, spinnerStyleItem, list);
        dataAdapter.setDropDownViewResource(spinnerStyleDropdown);
        spinner.setAdapter(dataAdapter);
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager)  getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void update(ConnectivityState observable, Boolean isOnline) {
        handleWifiState(isOnline);
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Write a new text in the flashView
     * @param msg  Message to write in the flashView
     */
    public static void flash(int msg){
        if(flashView != null)
            flashView.setText(msg);
    }

    /**
     * Remove current text from the flashView
     */
    public static void cleanFlash(){
        if(flashView != null)
            flashView.setText("");
    }

    /**
     * Called after a web service get the requested answer.
     * @param response web service answer
     */
    @Override
    public void updateResponse(String response) {
        initSpinners(response);
    }

    public static void setSpinnerValue(int[] selectionPosition){
        // Select item of src spinner
        spinSrc.setSelection(selectionPosition[0], true);
        spinSrc.performItemClick(spinSrc, selectionPosition[0],selectionPosition[0]);
        positionSpinnerDst = selectionPosition[1];
        clickUpdateSpinner = false;
    }

    public MainActivity() {
        Log.e("Twic", "Init");
        connectivityState = ConnectivityState.getConnectivityState();
        connectivityState.addObserver(this);
    }

    /**
     * Initialize the main activity view:
     * <ul>
     *    <li>EditText size
     *    <li>Buttons listeners
     *    <li>Load language list in the spinners
     * </ul>
     */
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
        inputField = (EditText) findViewById(R.id.editText);
        Point p = new Point();
        getWindowManager().getDefaultDisplay().getSize(p);
        inputField.setWidth((int) (p.x * (3.0 / 5.0)));
        flashView = (TextView) findViewById(R.id.flashView);

        // Init tabs
        tabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        tabHost.addTab(tabHost.newTabSpec("twic").setIndicator("Twic"), TwicTab.class, null);
        tabHost.addTab(tabHost.newTabSpec("its").setIndicator("Its"), ItsTab.class, null);
        tabHost.addTab(tabHost.newTabSpec("microsoft").setIndicator("Microsoft"), MicrosoftTab.class, null);
        tabHost.addTab(tabHost.newTabSpec("info").setIndicator("Info"), InfoTab.class, null);

        TabManager tabManager = new TabManager(tabHost, this);
        tabHost.setOnTabChangedListener(tabManager);
        // Init 1st part fields

        sendButton = (Button) findViewById(R.id.buttonSend);
        sendButton.setOnClickListener(new SendListener(this, tabManager));

        spinSrc = (Spinner) findViewById(R.id.spinnerSrc);
        spinDest = (Spinner) findViewById(R.id.spinnerDst);


        prevButton = (Button) findViewById(R.id.buttonPrev);
        nextButton = (Button) findViewById(R.id.buttonNext);

        keyboardButton = (ImageButton) findViewById(R.id.buttonKeyboard);

        keyboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager manager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.toggleSoftInput(R.id.editText, 0);
            }
        });

        // Dont show the keyboard but set the cursor at the right position
        // From: http://stackoverflow.com/questions/10263384/android-how-to-get-text-position-from-touch-event
        inputField.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                inputField.requestFocus();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        Layout layout = ((EditText) v).getLayout();
                        float x = event.getX() + inputField.getScrollX();
                        float y = event.getY() + inputField.getScrollY();
                        int line = layout.getLineForVertical((int) y);

                        // Here is what you wanted:

                        int offset = layout.getOffsetForHorizontal(line, x);

                        if (offset > 0)
                            if (x > layout.getLineMax(0))
                                inputField.setSelection(offset);     // touch was at end of text
                            else
                                inputField.setSelection(offset - 1);

                        break;
                }

                return true;
            }
        });



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

        // Get intent, action and MIME type
        String action = intent.getAction();
        String type = intent.getType();

        if (type != null)
            if (Intent.ACTION_SEND.equals(action) && "text/plain".equals(type)) {
                handleSendText(intent);
            } else {
                flash(R.string.shareError);
            }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Load language pairs and codeNames
        handleWifiState(connectivityState.isOnline(getContext()));
    }
}