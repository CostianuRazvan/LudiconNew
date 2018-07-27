package larc.ludiconprod.Utils.MainPageUtils;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import larc.ludiconprod.Adapters.MainActivity.AroundMeAdapter;
import larc.ludiconprod.Adapters.MainActivity.SimpleDividerItemDecoration;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.Event;
import larc.ludiconprod.Utils.util.Sponsors;

/**
 * Created by hp1 on 21-01-2015.
 */
public class Tab1 extends Fragment {
    View v;
    ImageView heartImageAroundMe;
    TextView noActivitiesTextFieldAroundMe;
    TextView pressPlusButtonTextFieldAroundMe;
    ProgressBar progressBarAroundMe;
    public AroundMeAdapter fradapter;
    public RecyclerView frlistView;
    public ArrayList<Event> aroundMeEventList = new ArrayList<Event>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.tab1, container, false);

        aroundMeEventList = Persistance.getInstance().getAroundMeActivities(getActivity());
        ArrayList<Sponsors> sponsorsList=new ArrayList<>();
        //sponsorsList=Persistance.getInstance().getSponsors(getActivity());
        fradapter = new AroundMeAdapter(aroundMeEventList,sponsorsList, getActivity().getApplicationContext(), getActivity(), getResources());
        configureLayout();
        Typeface typeFace = Typeface.createFromAsset(super.getActivity().getAssets(), "fonts/Quicksand-Medium.ttf");
        noActivitiesTextFieldAroundMe.setTypeface(typeFace);
        pressPlusButtonTextFieldAroundMe.setTypeface(typeFace);

        return v;
    }

    public void configureLayout() {
        fradapter.notifyDataSetChanged();
        frlistView = (RecyclerView) v.findViewById(R.id.events_listView2);
        frlistView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        heartImageAroundMe = (ImageView) v.findViewById(R.id.heartImageAroundMe);
        progressBarAroundMe = (ProgressBar) v.findViewById(R.id.progressBarAroundMe);
        noActivitiesTextFieldAroundMe = (TextView) v.findViewById(R.id.noActivitiesTextFieldAroundMe);
        pressPlusButtonTextFieldAroundMe = (TextView) v.findViewById(R.id.pressPlusButtonTextFieldAroundMe);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        frlistView.setLayoutManager(layoutManager);
        frlistView.setAdapter(fradapter);
        heartImageAroundMe.setVisibility(View.INVISIBLE);
        noActivitiesTextFieldAroundMe.setVisibility(View.INVISIBLE);
        pressPlusButtonTextFieldAroundMe.setVisibility(View.INVISIBLE);

    }

    public void updateListOfEv(ArrayList<Event> events){
        fradapter.setListOfEvents(events);
        fradapter.notifyDataSetChanged();
    }
}