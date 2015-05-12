package ch.unige.Twic.Twic;

/**
 * Created by thomas on 2/27/15.
 */
public interface TwicFields {
    String[] FIELDS = new String[] {
            "sourceLanguage",
            "targetLanguage",
            "selectedWord",
            "baseForm",
            "collocationSource",
            "collocationTarget",
            "translation"
    };

    String[] FIELDSITS = new String[] {
            "sourceLanguage",
            "targetLanguage",
            "sentenceTranslation",
    };

    String SERVERURL = "http://latlapps.unige.ch";

    // Twic
    String LANGUAGELISTURL = SERVERURL + "/Twicff?act=ll";
    String TWICURL = SERVERURL + "/Twicff?act=twic";

    // Microsoft
    String MICROSOFTACCESSADDRESS = "https://datamarket.accesscontrol.windows.net/v2/OAuth2-13";
    String MICROSOFTACCESSSECRET = "GWJw5uch0RFlymQLh4N/qfKKmPNryE7Oh5BJaVayK24=";
    String MICROSOFTTRANSLATEADDRESS = "http://api.microsofttranslator.com/V2/Ajax.svc/Translate?";

    // ITS
    String ITSRL = SERVERURL + "/Twicff?act=sentence";

    // SYNT
    String SYNTURL = SERVERURL + "/Synt?";

    // Auto codeName
    String AUTO = "Auto";
}
