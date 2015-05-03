package ch.unige.Twic.Twic;

import java.io.UnsupportedEncodingException;

public class TwicUrlBuilder implements TwicFields{

    public static String getRequestUrl(){
        TranslationInfo info = TranslationInfo.getInstance();
        TextAnalysis a = TextAnalyzer.analyse(new TextAnalysis(info.getText(), info.getPosition()));
        String encodedText = "";
        try {
            encodedText = java.net.URLEncoder.encode(a.getText(), "UTF-8");
        } catch (UnsupportedEncodingException e) { }

        String path = TWICURL;
        if (CodeNamesMap.getCodeNameLength() > 0) {
            String srclg = CodeNamesMap.getCodeFromName(info.getCodeLgSrc());
            String tgtlg = CodeNamesMap.getCodeFromName(info.getCodeLgDst());
            path += "&pos=" + a.getOffset() + "&srclg=" + srclg + "&tgtlg=" + tgtlg + "&text=" + encodedText;
        }
        return path;
    }
}
