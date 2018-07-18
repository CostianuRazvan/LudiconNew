package larc.ludiconprod.Utils.MainPageUtils;

/**
 * Created by Andrei on 5/21/2016.
 */

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import larc.ludiconprod.Activities.BasicFragment;
import larc.ludiconprod.R;

public class Tab2 extends BasicFragment {

    TextView noActivitiesText;
    TextView pressPlusText;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab2, container, false);

        Typeface typeFace = Typeface.createFromAsset(super.getActivity().getAssets(), "fonts/Quicksand-Medium.ttf");

        noActivitiesText = (TextView) v.findViewById(R.id.noActivitiesTextFieldMyActivity);
        noActivitiesText.setTypeface(typeFace);
        pressPlusText = (TextView) v.findViewById(R.id.pressPlusButtonTextFieldMyActivity);
        pressPlusText.setTypeface(typeFace);

        translate();

        return v;
    }

    private void translate() {
        if (getLanguage().equalsIgnoreCase("ro")) {
            noActivitiesText.setText(R.string.ro_no_activities);
            pressPlusText.setText(R.string.ro_press_plus_button);
        } else {
            noActivitiesText.setText(R.string.en_no_activities);
            pressPlusText.setText(R.string.en_press_plus_button);
        }
    }
}
