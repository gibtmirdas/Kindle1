package ch.unige.kindle1.listeners;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import ch.unige.kindle1.EditTextCustom;
import ch.unige.kindle1.R;
import ch.unige.kindle1.Twic.TextAnalysis;
import ch.unige.kindle1.Twic.TwicFields;
import ch.unige.kindle1.Twic.TwicUrlBuilder;
import ch.unige.kindle1.Twic.TwicXmlParser;
import ch.unige.kindle1.WebService;
import ch.unige.kindle1.Twic.TextAnalyzer;

/**
 * Created by thomas on 2/27/15.
 */
public class SendListener implements View.OnClickListener, TwicFields {
    private TwicUrlBuilder urlBuilder;
    private Button sendButton;
    private TextView responseView;

    public SendListener(View rootView) {
        this.sendButton = (Button) rootView.findViewById(R.id.sendButton);
        this.responseView = (TextView) rootView.findViewById(R.id.responseView);
        urlBuilder = new TwicUrlBuilder((EditTextCustom) rootView.findViewById(R.id.inputSentenceField),(Spinner)rootView.findViewById(R.id.spinSrc),(Spinner)rootView.findViewById(R.id.spinDest),(SeekBar)rootView.findViewById(R.id.wordPositionSlider));
    }

    @Override
    public void onClick(View v) {
        sendButton.setEnabled(false);
        //String sentence = ((EditText)v.findViewById(R.id.inputSentenceField)).getText().toString();
        String path = urlBuilder.getRequestUrl();
        String response = WebService.callUrl(path);
        sendButton.setEnabled(true);
        Map<String, String[]> parseData = TwicXmlParser.parseTwicResponse(response);
        responseView.setText(formatResponse(parseData));

    }


    private String formatResponse(Map<String, String[]> parseData){
        String formatedResponse = "";
        for(String nodeName: FIELDS){
            formatedResponse += nodeName+" => " + convertArrayToString(parseData.get(nodeName))+"\n";
        }
        return formatedResponse;
    }

    private String convertArrayToString(String[] array){
        if(array.length == 0)
            return "<empty>";
        String ret = "";
        for(String str:array)
            ret += str +" --- ";
        return ret.substring(0,ret.length() - " --- ".length());
    }
}
