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

    public static String getMsRequestUrl(){
//        microsoftTranslateAddress + "appId=" + encodeURIComponent("Bearer " + accessToken) + "&from=" + srclg + "&to=" + tgtlg + "&contentType=text/plain&text=" + encodeURIComponent(selection);
//        String path = MICROSOFTTRANSLATEADDRESS + java.net.URLEncoder.encode("Bearer "+, "UTF-8")
        return "";
    }

    public static String getItsRequestUrl(){
        return "";
    }
}
