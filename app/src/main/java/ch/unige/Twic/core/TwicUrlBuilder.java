package ch.unige.Twic.core;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import ch.unige.Twic.MainActivity;
import ch.unige.Twic.R;
import ch.unige.Twic.exceptions.TwicException;
import ch.unige.Twic.language.CodeNamesMap;
import ch.unige.Twic.language.PairsList;

/**
 * Build URL to query the TWiC server.
 */
public class TwicUrlBuilder implements TwicFields{

    /**
     * Build a TWiC url to translate the current word.
     * @return url to query a TWiC translation.
     */
    public static String getTwicRequestUrl(){
        TranslationInfo info = TranslationInfo.getInstance();
        TextAnalysis a = TextAnalyzer.analyse(new TextAnalysis(info.getText(), info.getPosition()));
        String encodedText = "";
        try {
            encodedText = java.net.URLEncoder.encode(a.getText(), "UTF-8");
        } catch (UnsupportedEncodingException ignored) { }

        String path = TWICURL;
        if (!CodeNamesMap.isEmpty()) {
            String srclg = CodeNamesMap.getCodeFromName(info.getCodeLgSrc());
            String tgtlg = CodeNamesMap.getCodeFromName(info.getCodeLgDst());
            path += "&pos=" + a.getOffset() +
                    "&srclg=" + srclg +
                    "&tgtlg=" + tgtlg +
                    "&text=" + encodedText;
        }
        return path;
    }

    /**
     * Build a Microsoft translate url to translate the current sentence.
     * @return url to query a Microsoft translation.
     */
    public static String getMsRequestUrl(){
        TranslationInfo info = TranslationInfo.getInstance();
        String path = MICROSOFTTRANSLATEADDRESS;

        if (!CodeNamesMap.isEmpty()) {
            String[] lg = convertAutoLgToTwicDefault(new String[]{
                    CodeNamesMap.getCodeFromName(info.getCodeLgSrc()),
                    CodeNamesMap.getCodeFromName(info.getCodeLgDst())});



            try {
                path += "from=" + lg[0] +
                        "&to=" + lg[1] +
                        "&contentType=text/plain&text=" + java.net.URLEncoder.encode(info.getText(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    /**
     * Build an Its translate url to translate the current sentence.
     * @return url to query an Its translation.
     */
    public static String getItsRequestUrl(){
        TranslationInfo info = TranslationInfo.getInstance();
        String path = ITSRL;
        if (!CodeNamesMap.isEmpty()) {
            String[] lg = convertAutoLgToTwicDefault(new String[]{
                    CodeNamesMap.getCodeFromName(info.getCodeLgSrc()),
                    CodeNamesMap.getCodeFromName(info.getCodeLgDst())});

            if (lg[0].equals(TwicFields.AUTO) || lg[1].equals(TwicFields.AUTO) )
                return null;

            try {
                path += "&srclg=" + lg[0] +
                        "&tgtlg=" + lg[1] +
                        "&text=" + java.net.URLEncoder.encode(info.getText(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    /**
     * Build a Synt url to get the sound for a specified word and language.
     * @param word word to synthesize
     * @param lang language of the synthesized word
     * @return url to query a word synthesis.
     */
    public static String getSyntRequestUrl(String word, String lang) {
        return SYNTURL + "lg="+ lang +"&in=\"" + word + "\"";
    }

    /**
     * Query the TWiC server to retreive the current sentence languages if they're in Auto mode.
     * @param lg translation languages (might be AUTO)
     * @return translation languages (AUTO are converted to actual languages)
     */
    private static String[] convertAutoLgToTwicDefault(String[] lg){
        if(!lg[0].equals(AUTO) && !lg[1].equals(AUTO))
            return lg;
        try {
            // Make a dummy call to get twic src and tgt language
            Map<String, String[]> parseData = TwicXmlParser.parseTwicResponse(WebService.callUrl(getTwicRequestUrl()));

            // Update info with the response
            TranslationInfo.getInstance().setCodeLgSrc(parseData.get("sourceLanguage")[0]);
            TranslationInfo.getInstance().setCodeLgDst(parseData.get("targetLanguage")[0]);
            lg[0] = TranslationInfo.getInstance().getCodeLgSrc();
            lg[1] = TranslationInfo.getInstance().getCodeLgDst();
            MainActivity.setSpinnerValue(PairsList.getIndexesForPair(lg[0], lg[1]));
        } catch (TwicException e) {
            MainActivity.flash(R.string.shareError);
        }
        return lg;
    }
}