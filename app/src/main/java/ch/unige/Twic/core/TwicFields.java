package ch.unige.Twic.core;

/**
 * TWiC constant URL and response field names.
 */
public interface TwicFields {
    /**
     * TWiC query response fields.
     */
    String[] FIELDS = new String[] {
            "sourceLanguage",
            "targetLanguage",
            "selectedWord",
            "baseForm",
            "collocationSource",
            "collocationTarget",
            "translation"
    };

    /**
     * Its query response fields.
     */
    String[] FIELDSITS = new String[] {
            "sourceLanguage",
            "targetLanguage",
            "sentenceTranslation",
    };

    /**
     * Base URL of the TWiC server.
     */
    String SERVERURL = "http://latlapps.unige.ch";

    /**
     * Twic URLs
     */
    String LANGUAGELISTURL = SERVERURL + "/Twicff?act=ll";
    String TWICURL = SERVERURL + "/Twicff?act=twic";

    /**
     * Microsoft URLs
     */
    String MICROSOFTACCESSADDRESS = "https://datamarket.accesscontrol.windows.net/v2/OAuth2-13";
    String MICROSOFTACCESSSECRET = "GWJw5uch0RFlymQLh4N/qfKKmPNryE7Oh5BJaVayK24=";
    String MICROSOFTTRANSLATEADDRESS = "http://api.microsofttranslator.com/V2/Ajax.svc/Translate?";

    /**
     * ITS URL
     */
    String ITSRL = SERVERURL + "/Twicff?act=sentence";

    /**
     * SYNT URL
     */
    String SYNTURL = SERVERURL + "/Synt?";

    /**
     * Auto codeName
     */
    String AUTO = "Auto";
}
