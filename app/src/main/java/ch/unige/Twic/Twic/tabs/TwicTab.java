package ch.unige.Twic.Twic.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ch.unige.Twic.MainActivity;
import ch.unige.Twic.R;
import ch.unige.Twic.Twic.Exceptions.TwicException;
import ch.unige.Twic.Twic.TranslationInfo;
import ch.unige.Twic.Twic.TwicUrlBuilder;
import ch.unige.Twic.Twic.TwicXmlParser;
import ch.unige.Twic.WebService;
import android.widget.SimpleAdapter;

public class TwicTab extends Fragment implements ManagableTab{

    private ListView listTranslations, listCollocationSrc, listCollocationDst, listBaseForm;

    private void setWordList(ListView list, String[] words) {
        ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();

        for (String word : words) {
            if (!(word.isEmpty() || word.startsWith("*"))) {
                HashMap<String, String> map;
                map = new HashMap<String, String>();
                map.put("word", word);
                listItem.add(map);
            }
        }

        SimpleAdapter mSchedule = new SimpleAdapter (getActivity().getBaseContext(), listItem,
                R.layout.word_with_sound_item, new String[] {"word"}, new int[] {R.id.word});
        list.setAdapter(mSchedule);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.twic, container, false);

        AdapterView.OnItemClickListener wordListsOnClickListener =  new AdapterView.OnItemClickListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                HashMap<String, String> map = (HashMap<String, String>) ((ListView)a).getItemAtPosition(position);

                // Play sound
                Log.e("IST_", "word: " + map.get("word"));

            }
        };


        listTranslations = (ListView) v.findViewById(R.id.listTranslations);
        listCollocationSrc = (ListView) v.findViewById(R.id.listCollocationSrc);
        listCollocationDst = (ListView) v.findViewById(R.id.listCollocationDst);
        listBaseForm = (ListView) v.findViewById(R.id.listBaseForm);

        listTranslations.setOnItemClickListener(wordListsOnClickListener);
        listCollocationSrc.setOnItemClickListener(wordListsOnClickListener);
        listCollocationDst.setOnItemClickListener(wordListsOnClickListener);
        listBaseForm.setOnItemClickListener(wordListsOnClickListener);

        try {
            MainActivity.cleanFlash();
            update();
        } catch (TwicException e) {
            e.printStackTrace();
            MainActivity.flash(e.getMessageId());
        }
        return v;
    }

    public void update() throws TwicException {
        if(TranslationInfo.isIsInitialized()) {
            String path = TwicUrlBuilder.getRequestUrl();
            String response = WebService.callUrl(path);
            Map<String, String[]> parseData = TwicXmlParser.parseTwicResponse(response);

            setWordList(listBaseForm, parseData.get("baseForm"));
            setWordList(listTranslations, parseData.get("translation"));
            setWordList(listCollocationSrc, parseData.get("collocationSource"));
            setWordList(listCollocationDst, parseData.get("collocationTarget"));
        }
    }
}
