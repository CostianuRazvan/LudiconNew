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
import android.support.v4.content.ContextCompat;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import larc.ludiconprod.Adapters.MainActivity.AroundMeAdapter;
import larc.ludiconprod.Adapters.MainActivity.DividerItemDecorator;
import larc.ludiconprod.Adapters.MainActivity.FullReviewsAdapter;
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
import larc.ludiconprod.Utils.Review;
import larc.ludiconprod.Utils.ui.SlidingTabLayout;
import larc.ludiconprod.Utils.util.ReviewBrief;
import larc.ludiconprod.Utils.util.Sport;
import me.anwarshahriar.calligrapher.Calligrapher;

import static android.view.View.inflate;
import static java.security.AccessController.getContext;
import static larc.ludiconprod.Activities.ActivityDetailsActivity.decodeBase64;
import static larc.ludiconprod.Activities.Main.bottomBar;


public class FullPageView extends Activity {

    Context mContext;
    View v;
    public ViewPagerAdapter adapter;

    static public Activity activity;

    static public int pageNumberReviews = 0;
    static public boolean getFirstPageReviews = true;

    static public FullReviewsAdapter reviewAdapter;
    static public FullPageView currentFragment;

    public static final ArrayList<ReviewBrief> reviewsList = new ArrayList<ReviewBrief>();

    Boolean isFirstTimeReviews = false;

    static LinearLayoutManager layoutManagerReviews;
    ProgressBar progressBarReviews;

    public static int NumberOfRefreshReviews = 0;
    public static RecyclerView reviewlistView;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    Boolean isGetingPage = false;
    static Boolean isOnActivityPage = false;

    int nrElements = 4;
    ViewGroup.LayoutParams params;

    public static boolean fromSwipe = false;


    public FullPageView() {
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

        if (reviewAdapter != null) reviewAdapter.notifyDataSetChanged();
    }

    RelativeLayout backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.full_page_view);
            LayoutInflater inflater = activity.getLayoutInflater();
            ViewGroup container = null;

            mContext = inflater.getContext();
            isOnActivityPage = true;
            reviewsList.clear();

            pageNumberReviews = 0;

            // v = inflater.inflate(R.layout.history, container, false);
            nrElements = 4;

            while (activity == null) {
                activity = this;
            }

            Calligrapher calligrapher = new Calligrapher(activity);
            calligrapher.setFont(activity, "fonts/Quicksand-Medium.ttf", true);

            reviewAdapter = new FullReviewsAdapter(reviewsList, activity.getApplicationContext(), activity, getResources(), currentFragment);


            getReviews("0");


            NumberOfRefreshReviews = 0;

            this.backButton=(RelativeLayout)findViewById(R.id.backButton) ;

            final Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Medium.ttf");
            final Typeface typeFaceBold = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.ttf");

            RelativeLayout toolbar = (RelativeLayout) findViewById(R.id.tool_bar_view);
            TextView title = (TextView) toolbar.findViewById(R.id.titleText);
            title.setTypeface(typeFace);
            TextView titleText = (TextView) findViewById(R.id.titleText);

            title.setTypeface(typeFace);

            titleText.setText(activity.getResources().getString(R.string.reviews1));
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

    public void updateListOfReviews(){

        // stop swiping on my events
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_reviews);

        reviewAdapter.notifyDataSetChanged();

        reviewlistView = (RecyclerView) findViewById(R.id.events_listViewReviews);
        reviewlistView.setNestedScrollingEnabled(false);
        progressBarReviews = (ProgressBar) findViewById(R.id.progressBarReviews);

        progressBarReviews = (ProgressBar) findViewById(R.id.progressBarReviews);
        progressBarReviews.setIndeterminate(true);
        progressBarReviews.setAlpha(0f);

        if (!isFirstTimeReviews) {
            layoutManagerReviews = new LinearLayoutManager(this);

            layoutManagerReviews.setOrientation(LinearLayoutManager.VERTICAL);
            reviewlistView.setLayoutManager(layoutManagerReviews);
            reviewlistView.addItemDecoration(new DividerItemDecorator(this));
            reviewlistView.setAdapter(reviewAdapter);
        }


        if (reviewlistView != null) {
            reviewlistView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (layoutManagerReviews.findLastCompletelyVisibleItemPosition() == reviewsList.size() - 1) {
                        progressBarReviews.setAlpha(1f);
                        getReviews(String.valueOf(NumberOfRefreshReviews));
                    }
                }
            });
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getReviews("0");
                getFirstPageReviews= true;
                mSwipeRefreshLayout.setRefreshing(false);
                NumberOfRefreshReviews = 0;
                fromSwipe = true;
            }
        });

        isFirstTimeReviews = true;

        int last = layoutManagerReviews.findLastCompletelyVisibleItemPosition();
        int count = reviewAdapter.getItemCount();
        if (last + 1 < count && !fromSwipe) {
            layoutManagerReviews.smoothScrollToPosition(reviewlistView, null, last + 1);
        }
        fromSwipe = false;
    }


    public void getReviews(String pageNumber) {
        FullPageView.getFirstPageReviews = pageNumber.equals("0");

        HashMap<String, String> params = new HashMap<>();
        HashMap<String, String> headers = new HashMap<>();
        HashMap<String, String> urlParams = new HashMap<>();
        headers.put("authKey", Persistance.getInstance().getUserInfo(activity).authKey);


        //set urlParams
        urlParams.put("userId", getIntent().getStringExtra("userId"));
        urlParams.put("pageNumber", pageNumber);

        //get Past Events
        HTTPResponseController.getInstance().getReviews(params, headers, activity, urlParams, null);
    }

}
