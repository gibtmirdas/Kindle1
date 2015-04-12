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

    String SERVERURL = "http://latlapps.unige.ch";
    String LANGUAGELISTURL = SERVERURL + "/Twicff?act=ll";
    String TWICURL = SERVERURL + "/Twicff?act=twic";
}
