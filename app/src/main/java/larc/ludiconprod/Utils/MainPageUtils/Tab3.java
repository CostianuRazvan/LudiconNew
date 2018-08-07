package larc.ludiconprod.Utils.MainPageUtils;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import larc.ludiconprod.Activities.BasicFragment;
import larc.ludiconprod.Adapters.MainActivity.PastEventsAdapter;
import larc.ludiconprod.Adapters.MainActivity.SimpleDividerItemDecoration;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.Event;
import larc.ludiconprod.Utils.util.Sponsors;

public class Tab3 extends BasicFragment {

    View v;
    ProgressBar progressBarPastEvents;
    ImageView heartImage;
    TextView noActivitiesText;
    TextView pressPlusText;
    public PastEventsAdapter pastAdapter;
    public RecyclerView pastEventsView;
    public ArrayList<Event> pastEventsEventList = new ArrayList<Event>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.tab3, container, false);

        pastEventsEventList = Persistance.getInstance().getMyPastActivities(getActivity());
        ArrayList<Sponsors> sponsorsList = new ArrayList<>();
        //sponsorsList=Persistance.getInstance().getSponsors(getActivity());
        pastAdapter = new PastEventsAdapter(pastEventsEventList, sponsorsList, getActivity().getApplicationContext(), getActivity(), getResources());
        updateListOfEventsAroundMe();
        Typeface typeFace = Typeface.createFromAsset(super.getActivity().getAssets(), "fonts/Quicksand-Medium.ttf");
        noActivitiesText.setTypeface(typeFace);
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

    public void updateListOfEventsAroundMe() {
        pastAdapter.notifyDataSetChanged();
        pastEventsView = (RecyclerView) v.findViewById(R.id.events_listView3);
        pastEventsView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        heartImage = (ImageView) v.findViewById(R.id.heartImagePastEvents);
        progressBarPastEvents = (ProgressBar) v.findViewById(R.id.progressBarPastEvents);
        noActivitiesText = (TextView) v.findViewById(R.id.noActivitiesTextFieldPastEvents);
        pressPlusText = (TextView) v.findViewById(R.id.pressPlusButtonTextFieldPastEvents);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        pastEventsView.setLayoutManager(layoutManager);
        pastEventsView.setAdapter(pastAdapter);
        heartImage.setVisibility(View.INVISIBLE);
        noActivitiesText.setVisibility(View.INVISIBLE);
        pressPlusText.setVisibility(View.INVISIBLE);

    }
}
