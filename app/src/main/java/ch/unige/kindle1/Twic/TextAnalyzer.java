package ch.unige.kindle1.Twic;

import android.util.Log;

/**
 * Created by Dwii on 02.03.15.
 */
public class TextAnalyzer {

    private static TextAnalysis stripText(TextAnalysis analysis) {
        String text = analysis.getText();
        int offset = analysis.getOffset();
        String before = text.substring(0, offset);
        String after = text.substring(offset, text.length());

        before = before.replaceFirst("^\\s+", "").replaceAll("\\s+", " ");
        offset = before.length();

        if (before.equals(" ")) {
            offset = 0;
            before = "";
        } else {
            offset = before.length();
        }

        after = after.replaceAll("\\s+", " ");

        return new TextAnalysis(before + after, offset);
    }

    private static TextAnalysis splitText(TextAnalysis analysis) {
        String text = analysis.getText();
        int offset = analysis.getOffset();
        String left = "";
        boolean dotFounded = false;
        int newOffset = -1;

        for (int i = offset; i >= 0; i--) {
            if (text.charAt(i) == '.') {
                if (!dotFounded) {
                    dotFounded = true;
                } else {
                    left = text.substring(i + 1, offset);
                    newOffset = left.length();
                    break;
                }
            }
        }

        if (left.isEmpty()) {
            left = text.substring(0, offset);
            newOffset = offset;
        }

        String right = "";
        dotFounded = false;
        for (int i = offset; i < text.length(); i++) {
            if (text.charAt(i) == '.') {
                if (!dotFounded) {
                    dotFounded = true;
                } else {
                    left = text.substring(offset, i + 1);
                    break;
                }
            }
        }

        if (right.isEmpty()) {
            right = text.substring(offset, text.length());
        }

        return new TextAnalysis(left + right, newOffset);
    }


    public static TextAnalysis analyse(TextAnalysis analysis) {
        analysis = TextAnalyzer.stripText(analysis);
        analysis = TextAnalyzer.splitText(analysis);
        analysis = TextAnalyzer.stripText(analysis);
        return analysis;
    }

}
