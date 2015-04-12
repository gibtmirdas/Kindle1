package ch.unige.Twic.Twic;

/**
 * Created by Dwii on 02.03.15.
 */
public class TextAnalysis {
    private String text;
    private int offset;

    public TextAnalysis(String text, int offset) {
        this.text = text;
        this.offset = offset;
    }

    public String getText() { return text; }
    public int getOffset() { return offset; }
}
