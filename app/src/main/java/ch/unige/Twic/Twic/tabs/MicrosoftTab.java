package ch.unige.Twic.Twic.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ch.unige.Twic.MainActivity;
import ch.unige.Twic.R;
import ch.unige.Twic.Twic.Exceptions.TwicException;
import ch.unige.Twic.WebServiceObserver;

public class MicrosoftTab extends Fragment implements ManagableTab, WebServiceObserver {

    private TextView MsReponse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.microsoft, container, false);
        MsReponse = (TextView) v.findViewById(R.id.MsReponse);
        try {
            MainActivity.cleanFlash();
            update();
        } catch (TwicException e) {
            e.printStackTrace();
            MainActivity.flash(e.getMessageId());
        }
        return v;
    }

    @Override
    public void update() throws TwicException {
//        if(TranslationInfo.isIsInitialized()) {
//            String path = TwicUrlBuilder.getMsRequestUrl();
//            String response = WebService.callMsUrl(path);
//            MsReponse.setText(response);
//        }
    }

    @Override
    public void updateResponse(String tag) {
        MsReponse.setText("HELLO FUCKER");
    }
}
