package ch.unige.Twic.Twic.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ch.unige.Twic.R;

public class Tab2Fragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View V = inflater.inflate(R.layout.tab2_view, container, false);

        TextView t = (TextView) V.findViewById(R.id.view2);
        t.setText("CHANGED !!!!");


        return V;
    }
}
