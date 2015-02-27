package ch.unige.kindle1.listeners;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Map;

import ch.unige.kindle1.Twic.TwicFields;
import ch.unige.kindle1.Twic.TwicXmlParser;
import ch.unige.kindle1.WebService;

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
        WebService webService = new WebService();

        String response = webService.getResponse();
        sendButton.setEnabled(true);
        Map<String, String[]> parseData = TwicXmlParser.parseData(response);
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
