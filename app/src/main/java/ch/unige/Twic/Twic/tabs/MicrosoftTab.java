package ch.unige.Twic.Twic.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import ch.unige.Twic.MainActivity;
import ch.unige.Twic.R;
import ch.unige.Twic.Twic.Exceptions.TwicException;
import ch.unige.Twic.Twic.TranslationInfo;
import ch.unige.Twic.Twic.TwicUrlBuilder;
import ch.unige.Twic.Twic.TwicXmlParser;
import ch.unige.Twic.WebService;
import ch.unige.Twic.WebServiceObserver;

public class MicrosoftTab extends Fragment implements ManagableTab, WebServiceObserver {

    private TextView msReponse;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.microsoft, container, false);
        msReponse = (TextView) v.findViewById(R.id.MsReponse);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBarMs);
        progressBar.setVisibility(View.INVISIBLE);
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
            String path = TwicUrlBuilder.getMsRequestUrl();
            (new WebService(this)).execute(path, "ms");
        }else
            progressBar.setVisibility(View.INVISIBLE);
    }

    public void updateResponse(String response) {
        response = TwicXmlParser.parseMsResponse(response);
        String text = "…Nothing to show…";
        if(response.length() > 0)
            text = response;
        msReponse.setText(text);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
