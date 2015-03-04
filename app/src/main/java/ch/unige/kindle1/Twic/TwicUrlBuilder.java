package ch.unige.kindle1.Twic;

import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;

import ch.unige.kindle1.EditTextCustom;

/**
 * Created by thomas on 2/27/15.
 */
public class TwicUrlBuilder {

    private String baseUrl = "http://latlapps.unige.ch/Twicff?act=twic";
    private String srclg = "de";
    private String tgtlg = "fr";
    private String path = "";//baseUrl + "&pos=" + a.getOffset() + "&srclg=" + srclg + "&tgtlg=" + tgtlg + "&text=" + encodedText;

    // View
    private EditTextCustom inputField;
    private Spinner spinSrc, spinDest;
    private SeekBar wordPositionSlider;

    public TwicUrlBuilder(EditTextCustom inputField, Spinner spinSrc, Spinner spinDest, SeekBar wordPositionSlider) {
        this.inputField = inputField;
        this.spinSrc = spinSrc;
        this.spinDest = spinDest;
        this.wordPositionSlider = wordPositionSlider;
    }

    public String getRequestUrl(){
        TextAnalysis a = TextAnalyzer.analyse(new TextAnalysis(inputField.getText().toString(), wordPositionSlider.getProgress()));
        String encodedText = "";
        try {
            encodedText = java.net.URLEncoder.encode(a.getText(), "UTF-8");
        } catch (UnsupportedEncodingException e) { }
        String baseUrl = "http://latlapps.unige.ch/Twicff?act=twic";
        String srclg = CodeNamesMap.getCodeFromName(spinSrc.getSelectedItem().toString());
        String tgtlg = CodeNamesMap.getCodeFromName(spinDest.getSelectedItem().toString());
        String path = baseUrl + "&pos=" + a.getOffset() + "&srclg=" + srclg + "&tgtlg=" + tgtlg + "&text=" + encodedText;

        return path;
    }


}
