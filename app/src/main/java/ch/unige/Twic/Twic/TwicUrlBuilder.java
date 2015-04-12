package ch.unige.Twic.Twic;

import android.widget.SeekBar;
import android.widget.Spinner;

import java.io.UnsupportedEncodingException;

import ch.unige.Twic.EditTextCustom;

/**
 * Created by thomas on 2/27/15.
 */
public class TwicUrlBuilder implements TwicFields{

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

        String path = TWICURL;
        if (CodeNamesMap.getCodeNameLength() > 0) {
            String srclg = CodeNamesMap.getCodeFromName(spinSrc.getSelectedItem().toString());
            String tgtlg = CodeNamesMap.getCodeFromName(spinDest.getSelectedItem().toString());
            path += "&pos=" + a.getOffset() + "&srclg=" + srclg + "&tgtlg=" + tgtlg + "&text=" + encodedText;
        }
        return path;
    }


}
