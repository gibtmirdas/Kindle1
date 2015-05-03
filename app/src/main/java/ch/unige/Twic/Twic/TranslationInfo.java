package ch.unige.Twic.Twic;

public class TranslationInfo {
    private static TranslationInfo instance;
    private String text, codeLgSrc, codeLgDst;
    private int position;
    private static boolean isInitialized;

    private TranslationInfo() {
        isInitialized = false;
    }

    public void set(String text, int position, String codeLgSrc, String codeLgDst){
        this.text = text;
        this.codeLgSrc = codeLgSrc;
        this.codeLgDst = codeLgDst;
        this.position = position;
        this.isInitialized = true;
    }

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
}
