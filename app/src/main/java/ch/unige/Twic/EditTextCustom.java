package ch.unige.Twic;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.SeekBar;

/**
 * Created by thomas on 3/1/15.
 */
public class EditTextCustom extends EditText {
    private SeekBar wordPositionSlider;
    public EditTextCustom(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public EditTextCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextCustom(Context context) {
        super(context);
    }

    public void setWordPositionSlider(SeekBar wordPositionSlider){
        this.wordPositionSlider = wordPositionSlider;
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        if(wordPositionSlider != null)
            wordPositionSlider.setProgress(selStart);
    }
}
