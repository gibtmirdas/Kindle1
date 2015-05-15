package ch.unige.Twic.Twic;

import java.net.ServerSocket;

public class TranslationInfo {
    /**
     * Singleton instance of the TranslationInfo.
     */
    private static TranslationInfo instance;

    /**
     * Sentence to translate.
     */
    private String text;

    /**
     * Source language of the sentence.
     */
    private String codeLgSrc;

    /**
     * Destination language of the translation.
     */
    private String codeLgDst;

    /**
     * Position of the word to translate.
     */
    private int position;

    /**
     * Indicates whether the TranslationInfo was initialized previously or not.
     */
    private static boolean isInitialized;

    private TranslationInfo() {
        isInitialized = false;
    }

    /**
     * Set all TranslationInfo fields.
     * @param text      Sentence to translate
     * @param position  Position of the word to translate
     * @param codeLgSrc Source language of the sentence
     * @param codeLgDst Destination language of the translation
     */
    public void set(String text, int position, String codeLgSrc, String codeLgDst){
        this.text = text;
        this.codeLgSrc = codeLgSrc;
        this.codeLgDst = codeLgDst;
        this.position = position;
        TranslationInfo.isInitialized = true;
    }

    /**
     * Get the current instance of the TranslationInfo.
     * @return current TranslationInfo instance
     */
    public static TranslationInfo getInstance() {
        if(instance == null)
            instance = new TranslationInfo();
        return instance;
    }

    public String getText() {
        return text;
    }

    public String getCodeLgSrc() {
        return codeLgSrc;
    }

    public String getCodeLgDst() {
        return codeLgDst;
    }

    public int getPosition() {
        return position;
    }

    public static boolean isInitialized() {
        return isInitialized;
    }

    public void setCodeLgSrc(String codeLgSrc) {
        this.codeLgSrc = codeLgSrc;
    }

    public void setCodeLgDst(String codeLgDst) {
        this.codeLgDst = codeLgDst;
    }
}
