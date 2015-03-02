package ch.unige.kindle1.listeners;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import ch.unige.kindle1.Twic.TextAnalysis;
import ch.unige.kindle1.Twic.TwicFields;
import ch.unige.kindle1.Twic.TwicXmlParser;
import ch.unige.kindle1.WebService;
import ch.unige.kindle1.Twic.TextAnalyzer;

/**
 * Created by thomas on 2/27/15.
 */
public class SendListener implements View.OnClickListener, TwicFields {
    private Button sendButton;
    private TextView responseView;

    public SendListener(Button sendButton, TextView responseView) {
        this.sendButton = sendButton;
        this.responseView = responseView;
    }

    @Override
    public void onClick(View v) {
        sendButton.setEnabled(false);
        Log.i("Button", "Clicked send button");

        TextAnalysis a = TextAnalyzer.analyse(new TextAnalysis(" Heute wird über die Personalengpässe in der Verfassung abgestimmt. Heute wird über die Personalengpässe in der Verfassung abgestimmt. Heute wird über die Personalengpässe in der Verfassung abgestimmt.", 159));
        String encodedText = "";
        try {
            encodedText = java.net.URLEncoder.encode(a.getText(), "UTF-8");
        } catch (UnsupportedEncodingException e) { }
        String baseUrl = "http://latlapps.unige.ch/Twicff?act=twic";
        String srclg = "de";
        String tgtlg = "fr";
        String path = baseUrl + "&pos=" + a.getOffset() + "&srclg=" + srclg + "&tgtlg=" + tgtlg + "&text=" + encodedText;

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
