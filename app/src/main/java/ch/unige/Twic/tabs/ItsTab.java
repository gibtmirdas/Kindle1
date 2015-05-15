package ch.unige.Twic.tabs;

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
import ch.unige.Twic.exceptions.TwicException;
import ch.unige.Twic.core.TranslationInfo;
import ch.unige.Twic.core.TwicUrlBuilder;
import ch.unige.Twic.core.TwicXmlParser;
import ch.unige.Twic.core.WebService;
import ch.unige.Twic.core.WebServiceObserver;

/**
 * {@code ItsTab} represent the fragment of the Its tab. When the user translate a sentence, when the Its tab is focused, it asynchronously call the webservice to translate the given sentence. When the webservice reply back, the fragment is updated with the response content.
 */
public class ItsTab extends Fragment implements ManageableTab, WebServiceObserver{

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

    /**
     * When the tab is created, it try to call the webservice to fill its content
     * @throws TwicException
     */
    @Override
    public void update() throws TwicException {
        progressBar.setVisibility(View.VISIBLE);
        if(TranslationInfo.isInitialized()) {
            String path = TwicUrlBuilder.getItsRequestUrl();
            (new WebService(this)).execute(path);
        }else
            progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * When the webservice reply back, it call this function to update the fragment content.
     * @param response response of the webservice
     */
    public void updateResponse(String response) {
        Map<String, String[]> parseData = TwicXmlParser.parseItsResponse(response);
        String text = "…Nothing to show…";
        if(parseData.containsKey("sentenceTranslation"))
            text = parseData.get("sentenceTranslation")[0];
        itsResponse.setText(text);

        progressBar.setVisibility(View.INVISIBLE);
    }
}
