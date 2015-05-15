package ch.unige.Twic.Twic.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Map;

import ch.unige.Twic.MainActivity;
import ch.unige.Twic.R;
import ch.unige.Twic.Twic.Exceptions.TwicException;
import ch.unige.Twic.Twic.TranslationInfo;
import ch.unige.Twic.Twic.TwicUrlBuilder;
import ch.unige.Twic.Twic.TwicXmlParser;
import ch.unige.Twic.WebService;
import ch.unige.Twic.WebServiceObserver;

public class ItsTab extends Fragment implements ManagableTab, WebServiceObserver{

    TextView itsResponse;
    WebService webService;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        webService = new WebService(this);
        View v = inflater.inflate(R.layout.its, container, false);
        itsResponse = (TextView) v.findViewById(R.id.itsReponse);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBarIts);
        progressBar.setVisibility(View.INVISIBLE);

        // Update
        try {
            update();
        } catch (TwicException e) {
            e.printStackTrace();
            MainActivity.flash(e.getMessageId());
        }
        return v;
    }

    @Override
    public void update() throws TwicException {
        progressBar.setVisibility(View.VISIBLE);
        if(TranslationInfo.isInitialized()) {
            String path = TwicUrlBuilder.getItsRequestUrl();
            (new WebService(this)).execute(path);
        }else
            progressBar.setVisibility(View.INVISIBLE);
    }

    public void updateResponse(String response) {
        Map<String, String[]> parseData = TwicXmlParser.parseItsResponse(response);
        String text = "…Nothing to show…";
        if(parseData.containsKey("sentenceTranslation"))
            text = parseData.get("sentenceTranslation")[0];
        itsResponse.setText(text);

        progressBar.setVisibility(View.INVISIBLE);
    }
}
