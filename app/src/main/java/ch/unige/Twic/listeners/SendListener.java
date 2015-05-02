package ch.unige.Twic.listeners;

import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Map;

import ch.unige.Twic.EditTextCustom;
import ch.unige.Twic.MainActivity;
import ch.unige.Twic.R;
import ch.unige.Twic.Twic.Exceptions.TwicException;
import ch.unige.Twic.Twic.TwicFields;
import ch.unige.Twic.Twic.TwicUrlBuilder;
import ch.unige.Twic.Twic.TwicXmlParser;
import ch.unige.Twic.WebService;

/**
 * Created by thomas on 2/27/15.
 */
public class SendListener implements View.OnClickListener, TwicFields {
    private TwicUrlBuilder urlBuilder;
    private Button sendButton;
    private TextView responseView, flashView;

    public SendListener(MainActivity rootView) {
        this.sendButton = (Button) rootView.findViewById(R.id.sendButton);
        this.flashView = (TextView) rootView.findViewById(R.id.flashView);
        this.responseView = (TextView) rootView.findViewById(R.id.responseView);
        urlBuilder = new TwicUrlBuilder((EditTextCustom) rootView.findViewById(R.id.inputSentenceField),(Spinner)rootView.findViewById(R.id.spinSrc),(Spinner)rootView.findViewById(R.id.spinDest),(SeekBar)rootView.findViewById(R.id.wordPositionSlider));
    }

    @Override
    public void onClick(View v) {
        sendButton.setEnabled(false);
        //String sentence = ((EditText)v.findViewById(R.id.inputSentenceField)).getText().toString();
        String path = urlBuilder.getRequestUrl();
        String response = null;
        try {
            response = WebService.callUrl(path);
            Map<String, String[]> parseData = TwicXmlParser.parseTwicResponse(response);
            responseView.setText(formatResponse(parseData));
            flashView.setText("");
        } catch (TwicException e) {
            flashView.setText(R.string.serverError);
        }
        sendButton.setEnabled(true);
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
