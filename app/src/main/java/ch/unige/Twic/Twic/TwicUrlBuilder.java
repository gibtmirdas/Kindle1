package ch.unige.Twic.Twic;

import android.util.Log;

import java.io.UnsupportedEncodingException;

public class TwicUrlBuilder implements TwicFields{

    public static String getRequestUrl(){
        TranslationInfo info = TranslationInfo.getInstance();
        TextAnalysis a = TextAnalyzer.analyse(new TextAnalysis(info.getText(), info.getPosition()));
        String encodedText = "";
        try {
            encodedText = java.net.URLEncoder.encode(a.getText(), "UTF-8");
        } catch (UnsupportedEncodingException ignored) { }

        String path = TWICURL;
        if (CodeNamesMap.getCodeNameLength() > 0) {
            String srclg = CodeNamesMap.getCodeFromName(info.getCodeLgSrc());
            String tgtlg = CodeNamesMap.getCodeFromName(info.getCodeLgDst());
            path += "&pos=" + a.getOffset() + "&srclg=" + srclg + "&tgtlg=" + tgtlg + "&text=" + encodedText;
            Log.e("TwicAnal", "position: "+info.getPosition()+ ", offset: " + a.getOffset());
        }
        return path;
    }

    public static String getMsRequestUrl(){
        TranslationInfo info = TranslationInfo.getInstance();
        String path = MICROSOFTTRANSLATEADDRESS;

        if (CodeNamesMap.getCodeNameLength() > 0) {
            String srclg = CodeNamesMap.getCodeFromName(info.getCodeLgSrc());
            String tgtlg = CodeNamesMap.getCodeFromName(info.getCodeLgDst());
            try {
                path += "from=" + srclg + "&to=" + tgtlg + "&contentType=text/plain&text=" + java.net.URLEncoder.encode(info.getText(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    public static String getItsRequestUrl(){
        TranslationInfo info = TranslationInfo.getInstance();
        String path = ITSRL;

        if (CodeNamesMap.getCodeNameLength() > 0) {
            String srclg = CodeNamesMap.getCodeFromName(info.getCodeLgSrc());
            String tgtlg = CodeNamesMap.getCodeFromName(info.getCodeLgDst());
            try {
                path += "&srclg=" + srclg + "&tgtlg=" + tgtlg + "&text=" + java.net.URLEncoder.encode(info.getText(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    public static String getSyntRequestUrl(String word, String lang) {
        return SYNTURL + "lg="+ lang +"&in=\"" + word + "\"";
    }
}
