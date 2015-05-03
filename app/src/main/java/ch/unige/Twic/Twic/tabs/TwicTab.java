package ch.unige.Twic.Twic.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Map;

import ch.unige.Twic.R;
import ch.unige.Twic.Twic.Exceptions.TwicException;
import ch.unige.Twic.Twic.TwicUrlBuilder;
import ch.unige.Twic.Twic.TwicXmlParser;
import ch.unige.Twic.WebService;

public class TwicTab extends Fragment implements ManagableTab {

    private TextView baseForm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.twic, container, false);
        baseForm = (TextView) v.findViewById(R.id.baseForm);
        return v;
    }

    @Override
    public void update() throws TwicException {
        String path = TwicUrlBuilder.getRequestUrl();
        String response = null;
        response = WebService.callUrl(path);
        Map<String, String[]> parseData = TwicXmlParser.parseTwicResponse(response);
        baseForm.setText(parseData.get("baseForm")[0]);
    }
}
