package larc.ludiconprod.Activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.JsonObject;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import larc.ludiconprod.Adapters.MainActivity.AroundMeAdapter;
import larc.ludiconprod.Adapters.MainActivity.MyAdapter;
import larc.ludiconprod.Adapters.MainActivity.MyPastEventsAdapter;
import larc.ludiconprod.Adapters.MainActivity.SimpleDividerItemDecoration;
import larc.ludiconprod.BottomBarHelper.BottomBarTab;
import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.Event;
import larc.ludiconprod.Utils.EventBrief;
import larc.ludiconprod.Utils.EventDetails;
import larc.ludiconprod.Utils.General;
import larc.ludiconprod.Utils.HappeningNowLocation;
import larc.ludiconprod.Utils.Location.GPSTracker;
import larc.ludiconprod.Utils.MainPageUtils.ViewPagerAdapter;
import larc.ludiconprod.Utils.ui.SlidingTabLayout;
import larc.ludiconprod.Utils.util.Sport;
import me.anwarshahriar.calligrapher.Calligrapher;

import static android.view.View.inflate;
import static java.security.AccessController.getContext;
import static larc.ludiconprod.Activities.ActivityDetailsActivity.decodeBase64;
import static larc.ludiconprod.Activities.Main.bottomBar;


public class FullHistory extends Activity {

    Context mContext;
    View v;
    public ViewPagerAdapter adapter;

    static public Activity activity;

    static public int pageNumberPastEvents = 0;
    static public boolean getFirstPagePastEvents = true;

    static public MyPastEventsAdapter pastAdapter;
    static public FullHistory currentFragment;

    public static final ArrayList<EventBrief> pastEventList = new ArrayList<EventBrief>();

    Boolean isFirstTimePastEvents = false;

    static LinearLayoutManager layoutManagerPastEvents;
    ProgressBar progressBarPastEvents;

    public static int NumberOfRefreshPastEvents = 0;
    public static RecyclerView pastlistView;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    Boolean isGetingPage = false;
    static Boolean isOnActivityPage = false;

    int nrElements = 4;
    ViewGroup.LayoutParams params;

    public static boolean fromSwipe = false;

    RelativeLayout backButton;
   /* RelativeLayout toolbar;
    TextView title;
    TextView titleText;*/

    public FullHistory() {
        currentFragment = this;
        activity = this;
    }

    public Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (pastAdapter != null) pastAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        try {
        super.onCreate(savedInstanceState);

            setContentView(R.layout.history);
            LayoutInflater inflater = activity.getLayoutInflater();
            ViewGroup container = null;

            mContext = inflater.getContext();
            isOnActivityPage = true;
            pastEventList.clear();

            pageNumberPastEvents = 0;

           // v = inflater.inflate(R.layout.history, container, false);
            nrElements = 4;

            while (activity == null) {
                activity = this;
            }

            Calligrapher calligrapher = new Calligrapher(activity);
            calligrapher.setFont(activity, "fonts/Quicksand-Medium.ttf", true);

            pastAdapter = new MyPastEventsAdapter(pastEventList, activity.getApplicationContext(), activity, getResources(), currentFragment);


            getPastEvents("0");


            NumberOfRefreshPastEvents = 0;
/*
            View vPastEvents = getLayoutInflater().inflate(R.layout.tab_past_events, container , false);
            progressBarPastEvents = (ProgressBar) vPastEvents.findViewById(R.id.progressBarPastEvents);*/

            this.backButton=(RelativeLayout)findViewById(R.id.backButton) ;

            final Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Medium.ttf");
            final Typeface typeFaceBold = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.ttf");

            RelativeLayout toolbar = (RelativeLayout) findViewById(R.id.tool_bar_history);
            TextView title = (TextView) toolbar.findViewById(R.id.titleText);
            title.setTypeface(typeFace);
            TextView titleText = (TextView) findViewById(R.id.titleText);

            title.setTypeface(typeFace);

            titleText.setText(activity.getResources().getString(R.string.past_activities));
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.finish();
                }
            });

            currentFragment = this;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateListOfPastEvents(){

        // stop swiping on my events
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_past_events);

        pastAdapter.notifyDataSetChanged();

        pastlistView = (RecyclerView) findViewById(R.id.events_listViewPastEvents);
        progressBarPastEvents = (ProgressBar) findViewById(R.id.progressBarPastEvents);

        progressBarPastEvents = (ProgressBar) findViewById(R.id.progressBarPastEvents);
        progressBarPastEvents.setIndeterminate(true);
        progressBarPastEvents.setAlpha(0f);

        if (!isFirstTimePastEvents) {
            layoutManagerPastEvents = new LinearLayoutManager(this);

            layoutManagerPastEvents.setOrientation(LinearLayoutManager.VERTICAL);
            pastlistView.setLayoutManager(layoutManagerPastEvents);
            pastlistView.addItemDecoration(new SimpleDividerItemDecoration(this));
            pastlistView.setAdapter(pastAdapter);
        }


        if (pastlistView != null) {
            pastlistView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (layoutManagerPastEvents.findLastCompletelyVisibleItemPosition() == pastEventList.size() - 1) {
                        progressBarPastEvents.setAlpha(1f);
                        getPastEvents(String.valueOf(NumberOfRefreshPastEvents));
                    }
                }
            });
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPastEvents("0");
                getFirstPagePastEvents= true;
                mSwipeRefreshLayout.setRefreshing(false);
                NumberOfRefreshPastEvents = 0;
                fromSwipe = true;
            }
        });

        isFirstTimePastEvents = true;

        int last = layoutManagerPastEvents.findLastCompletelyVisibleItemPosition();
        int count = pastAdapter.getItemCount();
        if (last + 1 < count && !fromSwipe) {
            layoutManagerPastEvents.smoothScrollToPosition(pastlistView, null, last + 1);
        }
        fromSwipe = false;
    }


    public void getPastEvents(String pageNumber) {
        FullHistory.getFirstPagePastEvents = pageNumber.equals("0");

        HashMap<String, String> params = new HashMap<>();
        HashMap<String, String> headers = new HashMap<>();
        HashMap<String, String> urlParams = new HashMap<>();
        headers.put("authKey", Persistance.getInstance().getUserInfo(activity).authKey);

       // pageNumberPastEvents =  Integer.parseInt(pageNumber);

        //set urlParams
        urlParams.put("userId", Persistance.getInstance().getUserInfo(activity).id);
        urlParams.put("pageNumber", pageNumber);

        //get Past Events
        HTTPResponseController.getInstance().getPastEvents(params, headers, activity, urlParams, null);
    }

}
