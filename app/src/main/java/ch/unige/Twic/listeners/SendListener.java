package ch.unige.Twic.listeners;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import ch.unige.Twic.MainActivity;
import ch.unige.Twic.R;
import ch.unige.Twic.Twic.TranslationInfo;
import ch.unige.Twic.Twic.TwicFields;
import ch.unige.Twic.Twic.tabs.TabManager;


/**
 * Created by thomas on 2/27/15.
 */
public class SendListener implements View.OnClickListener, TwicFields {
    private Button sendButton;
    private EditText inputField;
    private Spinner spinnerSrc, spinnerDst;
    private TabManager tabManager;

    public SendListener(MainActivity rootView, TabManager tabManager) {
        this.tabManager = tabManager;
        this.sendButton = (Button) rootView.findViewById(R.id.buttonSend);
        this.inputField = (EditText) rootView.findViewById(R.id.editText);
        this.spinnerSrc = (Spinner) rootView.findViewById(R.id.spinnerSrc);
        this.spinnerDst = (Spinner) rootView.findViewById(R.id.spinnerDst);
    }

    @Override
    public void onClick(View v) {
        TranslationInfo.getInstance().set(
                inputField.getText().toString(),
                inputField.getSelectionStart(),
                spinnerSrc.getSelectedItem().toString(),
                spinnerDst.getSelectedItem().toString());
        sendButton.setEnabled(false);
        tabManager.update();
        sendButton.setEnabled(true);
    }
}
