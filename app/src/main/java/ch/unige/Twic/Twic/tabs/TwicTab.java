package ch.unige.Twic.Twic.tabs;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ch.unige.Twic.MainActivity;
import ch.unige.Twic.R;
import ch.unige.Twic.Twic.CodeNamesMap;
import ch.unige.Twic.Twic.Exceptions.TwicException;
import ch.unige.Twic.Twic.TranslationInfo;
import ch.unige.Twic.Twic.TwicUrlBuilder;
import ch.unige.Twic.Twic.TwicXmlParser;
import ch.unige.Twic.WebService;
import ch.unige.Twic.WebServiceObserver;

import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

public class TwicTab extends Fragment implements ManagableTab, WebServiceObserver {

    private ListView listTranslations, listCollocationSrc, listCollocationDst, listBaseForm;
    private ProgressBar progressBar;

    private void setWordList(ListView list, String[] words, String lang) {
        ArrayList<HashMap<String, String>> listItem = new ArrayList<>();

        if (words != null)
            for (String word : words) {
                if (!(word.isEmpty() || word.startsWith("*"))) {
                    HashMap<String, String> map;
                    map = new HashMap<>();
                    map.put("word", word);
                    map.put("lang", lang);
                    map.put("img", String.valueOf(R.drawable.speaker_icon));
                    listItem.add(map);
                }
            }

        if (listItem.size() == 0) {
            HashMap<String, String> map;
            map = new HashMap<>();
            map.put("word", "-");
            map.put("lang", lang);
            map.put("img", String.valueOf(R.drawable.empty_icon));
            listItem.add(map);
        }

        SimpleAdapter mSchedule = new SimpleAdapter (getActivity().getBaseContext(), listItem,
                R.layout.word_with_sound_item, new String[] {"word", "img"}, new int[] {R.id.word, R.id.speaker});
        list.setAdapter(mSchedule);
    }

    private String[] formatCollocations(String[] collocations) {
        String[] fCollocations = new String[collocations.length];
        for (int i = 0; i < collocations.length ; i++) {
            fCollocations[i] = collocations[i].replace("|", ", ");
        }
        return fCollocations;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.twic, container, false);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBarTwic);
        progressBar.setVisibility(View.INVISIBLE);

        AdapterView.OnItemClickListener wordListsOnClickListener = new AdapterView.OnItemClickListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                HashMap<String, String> map = (HashMap<String, String>) a.getItemAtPosition(position);

                String word = map.get("word");
                String lang = map.get("lang");

                if (lang != null) {
                    // Play sound
                    String url = TwicUrlBuilder.getSyntRequestUrl(word, lang);
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    try {
                        mediaPlayer.setDataSource(url);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.prepareAsync();

                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                        }
                    });
                    mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mp, int what, int extra) {
                            MainActivity.flash(R.string.soundError);
                            return false;
                        }
                    });
                }
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
        progressBar.setVisibility(View.VISIBLE);
        if(TranslationInfo.isIsInitialized())
            (new WebService(this)).execute(TwicUrlBuilder.getRequestUrl());
        else
            progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void updateResponse(String response) {
        String srcLang, dstLang;
        String[] baseForm, translation, collocationSource, collocationTarget;
        if(TranslationInfo.isIsInitialized()) {
            Map<String, String[]> parseData = TwicXmlParser.parseTwicResponse(response);

            TranslationInfo info = TranslationInfo.getInstance();

            srcLang = CodeNamesMap.getCodeFromName(info.getCodeLgSrc());
            dstLang = CodeNamesMap.getCodeFromName(info.getCodeLgDst());

            baseForm = parseData.get("baseForm");
            translation = parseData.get("translation");
            collocationSource = parseData.get("collocationSource");
            collocationTarget = parseData.get("collocationTarget");
        } else {
            srcLang = dstLang = null;
            baseForm = translation = collocationSource = collocationTarget = null;
        }

        setWordList(listBaseForm, baseForm, srcLang);
        setWordList(listTranslations, translation, dstLang);
        setWordList(listCollocationSrc, formatCollocations(collocationSource), srcLang);
        setWordList(listCollocationDst, formatCollocations(collocationTarget), dstLang);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
