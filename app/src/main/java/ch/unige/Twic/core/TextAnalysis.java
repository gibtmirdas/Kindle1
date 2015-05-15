package ch.unige.Twic.core;

/**
 * Store the text and the index of the word to translate
 */
public class TextAnalysis {
    /**
     * Text to translate
     */
    private String text;
    /**
     * Position of the word in the text to translate
     */
    private int offset;

    public TextAnalysis(String text, int offset) {
        this.text = text;
        this.offset = offset;
    }

    public String getText() { return text; }
    public int getOffset() { return offset; }
}