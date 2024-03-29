package ch.unige.Twic.tabs;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Space;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ch.unige.Twic.MainActivity;
import ch.unige.Twic.R;
import ch.unige.Twic.core.TwicFields;
import ch.unige.Twic.exceptions.TwicException;
import ch.unige.Twic.language.PairsList;
import ch.unige.Twic.core.TranslationInfo;
import ch.unige.Twic.core.TwicUrlBuilder;
import ch.unige.Twic.core.TwicXmlParser;
import ch.unige.Twic.core.WebService;
import ch.unige.Twic.core.WebServiceObserver;

/**
 * {@code TwicTab} represent the fragment of the Its tab. When the user translate a sentence, when the Its tab is focused, it asynchronously call the webservice to translate the given sentence. When the webservice reply back, the fragment is updated with the response content.
 */
public class TwicTab extends Fragment implements ManageableTab, WebServiceObserver {

    private ListView listTranslations, listCollocationSrc, listCollocationDst, listBaseForm;
    private ProgressBar progressBar;

    /**
     * Set the size of a listView in order to display each items.
     * @param listView ListView to resize
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    /**
     * Fill a {@link android.widget.ListView} with a list of words and an associated language.
     * @param list {@link android.widget.ListView} to fill
     * @param words Array of words to add to the list
     * @param lang Language of the list
     */
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
        setListViewHeightBasedOnChildren(list);
    }

    /**
     * Return an empty array if {@code collocations} is null, otherwise format the content to display it fancier
     * @param collocations array of the collocations
     * @return formated array of the collocations
     */
    private String[] formatCollocations(String[] collocations) {
        String[] fCollocations;

        if (collocations != null) {
            fCollocations = new String[collocations.length];
            for (int i = 0; i < collocations.length; i++) {
                fCollocations[i] = collocations[i].replace("|", ", ");
            }
        } else {
            fCollocations = new String[0];
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
    public void update() throws TwicException {
        progressBar.setVisibility(View.VISIBLE);
        if(TranslationInfo.isInitialized()) {
            if (!TranslationInfo.getInstance().getText().equals("")) {
                (new WebService(this)).execute(TwicUrlBuilder.getTwicRequestUrl());
            } else {
                MainActivity.flash(R.string.emptyTextError);
            }
        }
        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * When the webservice reply back, it call this function to update the fragment content.
     * @param response response of the webservice
     */
    @Override
    public void updateResponse(String response) {
        String srcLang, dstLang;
        String[] baseForm, translation, collocationSource, collocationTarget;
        if(TranslationInfo.isInitialized()) {
            Map<String, String[]> parseData = TwicXmlParser.parseTwicResponse(response);

            String[] slg = parseData.get("sourceLanguage");
            String[] dlg = parseData.get("targetLanguage");

            srcLang = slg != null && slg.length > 0 ? slg[0] : TwicFields.AUTO;
            dstLang = dlg != null && dlg.length > 0 ? dlg[0] : TwicFields.AUTO;

            MainActivity.setSpinnerValue(PairsList.getIndexesForPair(srcLang, dstLang));

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
