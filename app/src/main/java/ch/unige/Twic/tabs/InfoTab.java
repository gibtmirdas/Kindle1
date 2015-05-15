package ch.unige.Twic.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.unige.Twic.R;

/**
 * {@code InfoTab} represent the fragment of the Information tab. As it is a static tab, this class only inflate the fragment with the correct layout (info in {@link ch.unige.Twic.R.layout})
 */
public class InfoTab extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.info, container, false);
    }
}