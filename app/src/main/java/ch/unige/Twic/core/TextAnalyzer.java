package ch.unige.Twic.core;

/**
 * Provide a way to reduce the input text size for TWiC queries.
 * Based on TWiC Chrome extension method.
 */
public class TextAnalyzer {

    /**
     * Ask Yves Scherrer, he know what this thing does.
     */
    private static TextAnalysis stripText(TextAnalysis analysis) {
        String text = analysis.getText();
        int offset = analysis.getOffset();
        String before = text.substring(0, offset);
        String after = text.substring(offset, text.length());

        before = before.replaceFirst("^\\s+", "").replaceAll("\\s+", " ");

        if (before.equals(" ")) {
            offset = 0;
            before = "";
        } else {
            offset = before.length();
        }

        after = after.replaceAll("\\s+", " ");

        return new TextAnalysis(before + after, offset);
    }

    /**
     * Ask Yves Scherrer, he know what this thing does.
     */
    private static TextAnalysis splitText(TextAnalysis analysis) {
        if(analysis.getText().length() == 0 )
            return analysis;
        String text = analysis.getText();
        int offset = analysis.getOffset();
        if (offset == text.length())
            offset--;
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
                    right = text.substring(offset, i + 1);
                    break;
                }
            }
        }

        if (right.isEmpty()) {
            right = text.substring(offset, text.length());
        }

        return new TextAnalysis(left + right, newOffset);
    }


    /**
     * Try to reduce the size of the text to translate for TWiC queries.
     * @param analysis text to analyze
     * @return result of the text analysis.
     */
    public static TextAnalysis analyse(TextAnalysis analysis) {
        analysis = TextAnalyzer.stripText(analysis);
        analysis = TextAnalyzer.splitText(analysis);
        analysis = TextAnalyzer.stripText(analysis);
        return analysis;
    }

}
