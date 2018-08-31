package larc.ludiconprod.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import larc.ludiconprod.Adapters.MainActivity.AroundMeAdapter;
import larc.ludiconprod.Adapters.MainActivity.MyAdapter;
import larc.ludiconprod.Adapters.MainActivity.SimpleDividerItemDecoration;
import larc.ludiconprod.BottomBarHelper.BottomBarTab;
import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.Dialogs.ConfirmationDialog;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.Event;
import larc.ludiconprod.Utils.HappeningNowLocation;
import larc.ludiconprod.Utils.Location.GPSTracker;
import larc.ludiconprod.Utils.Location.LocationCheckState;
import larc.ludiconprod.Utils.Location.LocationInfo;
import larc.ludiconprod.Utils.MainPageUtils.ViewPagerAdapter;
import larc.ludiconprod.Utils.ui.SlidingTabLayout;
import larc.ludiconprod.Utils.util.Sponsors;
import larc.ludiconprod.Utils.util.Sport;
import me.anwarshahriar.calligrapher.Calligrapher;

import static android.content.Context.MODE_PRIVATE;
import static larc.ludiconprod.Activities.Main.bottomBar;

/**
 * Created by ancuta on 7/26/2017.
 */

public class ActivitiesActivity extends Fragment implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener,  Response.ErrorListener {

    ViewPager pager;
    Context mContext;
    public ViewPagerAdapter adapter;
    View v;
    SlidingTabLayout tabs;

    static public FragmentActivity activity;

    CharSequence Titles[];
    int Numboftabs = 2;

    static public int pageNumberAroundMe = 0;
    static public boolean getFirstPageMyActivity = true;

    static public AroundMeAdapter fradapter;
    static public MyAdapter myAdapter;
    static public ActivitiesActivity currentFragment;

    public static ArrayList<Event> aroundMeEventList = new ArrayList<Event>();
    static public ArrayList<Sponsors> sponsorsList = new ArrayList<>();
    public static final ArrayList<Event> myEventList = new ArrayList<Event>();

    ImageView heartImageAroundMe;
    TextView noActivitiesTextFieldAroundMe;
    TextView pressPlusButtonTextFieldAroundMe;
    ImageView heartImageMyActivity;
    TextView noActivitiesTextFieldMyActivity;
    TextView pressPlusButtonTextFieldMyActivity;

    static LinearLayoutManager layoutManagerAroundMe;
    static LinearLayoutManager layoutManagerMyActivities;

    ProgressBar progressBarMyEvents;
    ProgressBar progressBarAroundMe;

    Boolean isFirstTimeMyEvents = false;

    public static int NumberOfRefreshMyEvents = 0;
    public static int NumberOfRefreshAroundMe = 0;
    public static RecyclerView frlistView;
    public static RecyclerView mylistView;

    private SwipeRefreshLayout mSwipeRefreshLayout1;
    private SwipeRefreshLayout mSwipeRefreshLayout2;

  Boolean isGetingPage = false;

    static public double longitude = 0;
    static public double latitude = 0;

    static Boolean isOnActivityPage = false;
    static int buttonState = 0; //0:Not pressed 1:Check-in Performed

    LocationListener locationListener;
    private LocationRequest locationRequest;
    private FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;
    private GoogleApiClient googleApiClient;

    // HAPPENING NOW AREA
    RelativeLayout happeningNowLayout;
    static Button checkinButton;

    private boolean noGps = false;
    static public int startedEventDate = Integer.MAX_VALUE;
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    int nrElements = 4;
    ViewGroup.LayoutParams params;

    public static boolean fromSwipe = false;

    public ActivitiesActivity() {
        currentFragment = this;
        activity = getActivity();
    }

    public Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }


    public LocationCheckState checkedLocation(Event currentEvent) {
        try {
            if (!googleApiClient.isConnected()) {
                googleApiClient.connect();
            }
            Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                Location eventLocation = new Location(LocationManager.GPS_PROVIDER);
                eventLocation.setLatitude(currentEvent.latitude);
                eventLocation.setLongitude(currentEvent.longitude);

                System.out.println(location.distanceTo(eventLocation) + " distanta");

                float distanceToEvent = location.distanceTo(eventLocation);
                if (distanceToEvent < 1000) {
                    return LocationCheckState.LOCATION_OK;
                }

                return LocationCheckState.LOCATION_NOT_IN_RANGE;
            } else {
                return LocationCheckState.LOCATION_COULD_NOT_BE_DETERMINED;
            }
        }
        catch (IllegalStateException ex) {
            // Other specific stuff to do
            return LocationCheckState.LOCATION_COULD_NOT_BE_DETERMINED;
        }
        catch (Exception ex){
            return LocationCheckState.LOCATION_COULD_NOT_BE_DETERMINED;
        }

    }

    public void savePoints() {

        System.out.println(" save points called");

        Event currentEvent = Persistance.getInstance().getHappeningNow(getActivity());
        HappeningNowLocation happeningNowLocation = Persistance.getInstance().getLocation(activity);

        if(happeningNowLocation != null) {
            happeningNowLocation.endDate = String.valueOf(System.currentTimeMillis() / 1000);

            if (currentEvent == null) {
                return;
            }

            HashMap<String, String> params = new HashMap<String, String>();
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("authKey", Persistance.getInstance().getUserInfo(activity).authKey);

            params.put("userId", Persistance.getInstance().getUserInfo(activity).id);
            params.put("eventId", currentEvent.id);

            if (happeningNowLocation.startDate != null) {
                params.put("startedAt", happeningNowLocation.startDate);
            } else {
                params.put("startedAt", happeningNowLocation.endDate);
            }
            params.put("endedAt", happeningNowLocation.endDate);

            for (int i = 0; i < happeningNowLocation.locationList.size(); i++) {
                params.put("locations[" + i + "][latitude]", String.valueOf(happeningNowLocation.locationList.get(i).latitude));
                params.put("locations[" + i + "][longitude]", String.valueOf(happeningNowLocation.locationList.get(i).longitude));
            }

            HTTPResponseController.getInstance().savePoints(params, headers, activity, this);
            Persistance.getInstance().setHappeningNow(null, activity);
            Persistance.getInstance().setEventToReview(activity, currentEvent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (fradapter != null) fradapter.notifyDataSetChanged();
        if (myAdapter != null) myAdapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            FacebookSdk.sdkInitialize(getActivity());

            googleApiClient = new GoogleApiClient.Builder(getContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            googleApiClient.connect();

            locationRequest = new LocationRequest();
            locationRequest.setInterval(60 * 1000);
            locationRequest.setFastestInterval(10 * 1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            //locationRequest.setSmallestDisplacement(minimumDistanceBetweenUpdates);

            mContext = inflater.getContext();
            isOnActivityPage = true;
            aroundMeEventList.clear();

            pageNumberAroundMe = 0;

            v = inflater.inflate(R.layout.activities_acitivity, container, false);
            nrElements = 4;

            // Laur - VERY STRANGE
            while (activity == null) {
                activity = getActivity();
            }

            Calligrapher calligrapher = new Calligrapher(activity);
            calligrapher.setFont(activity, "fonts/Quicksand-Medium.ttf", true);

            // HIDE HAPPENING NOW FOR THE BEGINNING
            happeningNowLayout = (RelativeLayout) v.findViewById(R.id.generalHappeningNowLayout);
            ViewGroup.LayoutParams params = happeningNowLayout.getLayoutParams();
            params.height = 0;
            happeningNowLayout.setLayoutParams(params);

            View vAroundMe = inflater.inflate(R.layout.tab1, container, false);
            heartImageAroundMe = (ImageView) vAroundMe.findViewById(R.id.heartImageAroundMe);
            progressBarAroundMe = (ProgressBar) vAroundMe.findViewById(R.id.progressBarAroundMe);
            noActivitiesTextFieldAroundMe = (TextView) vAroundMe.findViewById(R.id.noActivitiesTextFieldAroundMe);
            pressPlusButtonTextFieldAroundMe = (TextView) vAroundMe.findViewById(R.id.pressPlusButtonTextFieldAroundMe);

            heartImageAroundMe.setVisibility(View.INVISIBLE);
            noActivitiesTextFieldAroundMe.setVisibility(View.INVISIBLE);
            pressPlusButtonTextFieldAroundMe.setVisibility(View.INVISIBLE);

            // Set number of unseen chats in the bottom bar
            int NumberOfUnseen = Persistance.getInstance().getUnseenChats(activity).size();
            BottomBarTab nearby = bottomBar.getTabWithId(R.id.tab_friends);
            nearby.setBadgeCount(NumberOfUnseen);

            updateActiveToken(activity);

            // Creating ViewPager Adapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs
            Titles = new CharSequence[]{getResources().getString(R.string.around_me),getResources().getString(R.string.my_activities)};
            adapter = new ViewPagerAdapter(activity.getSupportFragmentManager(), Titles, Numboftabs);

            // Assigning ViewPager View and setting the adapter
            pager = (ViewPager) v.findViewById(R.id.pager);
            pager.setAdapter(adapter);

            // Assiging the Sliding Tab Layout View
            tabs = (SlidingTabLayout) v.findViewById(R.id.tabs);
            tabs.setDistributeEvenly(false); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

            // Setting Custom Color for the Scroll bar indicator of the Tab View
            tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    return getResources().getColor(R.color.tabsScrollColor);
                }
            });

            // Setting the ViewPager For the SlidingTabsLayout
            tabs.setViewPager(pager);

            myAdapter = new MyAdapter(myEventList, sponsorsList, activity.getApplicationContext(), activity, getResources(), currentFragment);
            fradapter = new AroundMeAdapter(aroundMeEventList, sponsorsList, activity.getApplicationContext(), activity, getResources(), currentFragment);

            // Happening now started -- More than 3 Hours have passed since Start => Then Stop it
            HappeningNowLocation happeningNowLocation = Persistance.getInstance().getLocation(getActivity());
            if(happeningNowLocation != null){
                String startUnixDateString = happeningNowLocation.startDate;
                long startUnixDate = 0;

                if(startUnixDateString != null){
                    startUnixDate = Long.parseLong(startUnixDateString);
                }

                long currentUnixDate = System.currentTimeMillis() / 1000L;
                long difference = currentUnixDate - startUnixDate;

                // If more than 3 hours
                long acceptableDifference = 3 * 60 * 60;
                if(difference > acceptableDifference) {
                    // Stop happening now
                    savePoints();

                    buttonState = 0;

                    // Clear out everything related to past Happening Now
                    Persistance.getInstance().setHappeningNow(null, activity);
                    Persistance.getInstance().setLocation(activity, null);
                }
            }

            getAroundMeEvents("0", latitude, longitude);
            getMyEvents("0");

            GPSTracker gps = new GPSTracker(activity.getApplicationContext(), activity);
            if (!gps.canGetLocation()) {
                this.noGps = true;
                this.prepareError(getResources().getString(R.string.no_location_services_available));
            }

            NumberOfRefreshMyEvents = 0;
            NumberOfRefreshAroundMe = 0;

            // Modal for Facebook Share
            final ShareButton shareButton;
            FacebookSdk.sdkInitialize(activity);
            callbackManager = CallbackManager.Factory.create();
            shareDialog = new ShareDialog(activity);
            shareButton = (ShareButton) v.findViewById(R.id.share_btn);
            String facebookId = Persistance.getInstance().getUserInfo(super.getActivity()).facebookId;
            if (facebookId.equals("")) {
                shareButton.getLayoutParams().height = 0;
            }
            else {
                ShareLinkContent contentLink = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse("https://developers.facebook.com"))
                        .build();

                shareButton.setShareContent(contentLink);
                shareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ShareDialog.canShow(ShareLinkContent.class)) {
                            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                    .setContentTitle("Activity on Ludicon")
                                    .setContentDescription(
                                            "I will attend an event in Ludicon ! Let's go and play ! ")
                                    .setContentUrl(Uri.parse("https://www.ludicon.ro/"))
                                    .build();

                            shareDialog.show(linkContent, ShareDialog.Mode.AUTOMATIC);
                        }
                    }
                });

                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Toast.makeText(activity, R.string.this_post_was_shared + " ", Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
                    }

                    @Override
                    public void onCancel() { }

                    @Override
                    public void onError(FacebookException error) { }
                });
            }

            SharedPreferences shared = activity.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
            String channel = (shared.getString("language", ""));
            if (!Locale.getDefault().getDisplayLanguage().substring(0,2).toLowerCase().equals(channel)){
                String language = Locale.getDefault().getDisplayLanguage().substring(0,2).toLowerCase();
                SharedPreferences prefs = activity.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("language", language);
                editor.commit();

                HashMap<String, String> params1 = new HashMap<>();
                params1.put("userId", Persistance.getInstance().getUserInfo(activity).id);
                params1.put("language", language);
                HashMap<String, String> headers = new HashMap<>();
                headers.put("authKey", Persistance.getInstance().getUserInfo(activity).authKey);
                HTTPResponseController.getInstance().updateUser(params1, headers, activity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }

    //Set ActiveToken in Firebase node for notifications
    private void updateActiveToken(FragmentActivity activity) {
        final DatabaseReference userNode = FirebaseDatabase.getInstance().getReference().child("users").child(Persistance.getInstance().getUserInfo(activity).id);
        SharedPreferences sharedPreferences = activity.getSharedPreferences("regId", 0);
        String activeToken = sharedPreferences.getString("regId", "0");
        if (!activeToken.equalsIgnoreCase("0")) {
            userNode.child("activeToken").setValue(activeToken);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void updateListOfEventsAroundMe() {
        try {
            isGetingPage = false;

            if (this.noGps) {
                this.prepareError(getResources().getString(R.string.no_location_services_available));
            }

            // stop swiping
            if (layoutManagerAroundMe == null) {
                layoutManagerAroundMe = new LinearLayoutManager(activity);
                layoutManagerAroundMe.setOrientation(LinearLayoutManager.VERTICAL);
            }

            if(fradapter == null){
                fradapter = new AroundMeAdapter(aroundMeEventList, sponsorsList, activity.getApplicationContext(), activity, getResources(), currentFragment);
            }

            fradapter.notifyDataSetChanged();

            adapter.tab1.updateListOfEv(aroundMeEventList);

            if (frlistView == null) {
                frlistView = (RecyclerView) v.findViewById(R.id.events_listView2);

                if(frlistView != null) {
                    frlistView.setLayoutManager(layoutManagerAroundMe);
                    frlistView.setAdapter(fradapter);
                }
                progressBarAroundMe.setIndeterminate(true);
                progressBarAroundMe.setAlpha(0f);
            }

            heartImageAroundMe = (ImageView) v.findViewById(R.id.heartImageAroundMe);
            progressBarAroundMe = (ProgressBar) v.findViewById(R.id.progressBarAroundMe);
            noActivitiesTextFieldAroundMe = (TextView) v.findViewById(R.id.noActivitiesTextFieldAroundMe);
            pressPlusButtonTextFieldAroundMe = (TextView) v.findViewById(R.id.pressPlusButtonTextFieldAroundMe);

            FloatingActionButton createNewActivityFloatingButtonAroundMe = (FloatingActionButton) v.findViewById(R.id.floatingButton2);
            if(createNewActivityFloatingButtonAroundMe != null) {
                createNewActivityFloatingButtonAroundMe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, CreateNewActivity.class);
                        startActivity(intent);
                    }
                });
            }

            // Display the placeholder message
            if (aroundMeEventList.size() == 0) {
                heartImageAroundMe.setVisibility(View.VISIBLE);
                noActivitiesTextFieldAroundMe.setVisibility(View.VISIBLE);
                pressPlusButtonTextFieldAroundMe.setVisibility(View.VISIBLE);
            } else {
                heartImageAroundMe.setVisibility(View.INVISIBLE);
                noActivitiesTextFieldAroundMe.setVisibility(View.INVISIBLE);
                pressPlusButtonTextFieldAroundMe.setVisibility(View.INVISIBLE);
            }

            if (frlistView != null) {
                frlistView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (layoutManagerAroundMe.findLastCompletelyVisibleItemPosition() == aroundMeEventList.size() - 1) {
                            progressBarAroundMe.setAlpha(1f);

                            getAroundMeEvents(String.valueOf(NumberOfRefreshAroundMe), latitude, longitude);
                        }
                    }
                });
            }


            mSwipeRefreshLayout2 = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh2);

            if(mSwipeRefreshLayout2 != null) {
                mSwipeRefreshLayout2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getAroundMeEvents("0", latitude, longitude);

                        // For triggering happening now
                        //getMyEvents("0");
                        checkIsHappeningNow();

                        pageNumberAroundMe = 0;
                        mSwipeRefreshLayout2.setRefreshing(false);
                        NumberOfRefreshAroundMe = 0;
                        nrElements = 4;
                    }
                });
            }

            progressBarAroundMe.setAlpha(0f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void updateListOfMyEvents() {
        if (this.noGps) {
            this.prepareError(getResources().getString(R.string.no_location_services_available));
        }

        // stop swiping on my events
        mSwipeRefreshLayout1 = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh1);

        myAdapter.notifyDataSetChanged();

        mylistView = (RecyclerView) v.findViewById(R.id.events_listView1);
        heartImageMyActivity = (ImageView) v.findViewById(R.id.heartImageMyActivity);
        noActivitiesTextFieldMyActivity = (TextView) v.findViewById(R.id.noActivitiesTextFieldMyActivity);
        pressPlusButtonTextFieldMyActivity = (TextView) v.findViewById(R.id.pressPlusButtonTextFieldMyActivity);
        progressBarMyEvents = (ProgressBar) v.findViewById(R.id.progressBarMyEvents);

        progressBarMyEvents = (ProgressBar) v.findViewById(R.id.progressBarMyEvents);
        progressBarMyEvents.setIndeterminate(true);
        progressBarMyEvents.setAlpha(0f);

        if (!isFirstTimeMyEvents) {
            layoutManagerMyActivities = new LinearLayoutManager(getContext());

            layoutManagerMyActivities.setOrientation(LinearLayoutManager.VERTICAL);
            mylistView.setLayoutManager(layoutManagerMyActivities);
            mylistView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
            mylistView.setAdapter(myAdapter);
        }

        final FloatingActionButton createNewActivityFloatingButtonMyActivity = (FloatingActionButton) v.findViewById(R.id.floatingButton1);
        createNewActivityFloatingButtonMyActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, CreateNewActivity.class);
                startActivity(intent);
            }
        });


        if (myEventList.size() == 0) {
            heartImageMyActivity.setVisibility(View.VISIBLE);
            noActivitiesTextFieldMyActivity.setVisibility(View.VISIBLE);
            pressPlusButtonTextFieldMyActivity.setVisibility(View.VISIBLE);
        } else {
            heartImageMyActivity.setVisibility(View.INVISIBLE);
            noActivitiesTextFieldMyActivity.setVisibility(View.INVISIBLE);
            pressPlusButtonTextFieldMyActivity.setVisibility(View.INVISIBLE);
        }

        if (mylistView != null) {
            mylistView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (layoutManagerMyActivities.findLastCompletelyVisibleItemPosition() == myEventList.size() - 1) {
                        progressBarMyEvents.setAlpha(1f);
                        getMyEvents(String.valueOf(NumberOfRefreshMyEvents));
                    }
                }
            });
        }

       mSwipeRefreshLayout1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getMyEvents("0");
                    getFirstPageMyActivity = true;
                    mSwipeRefreshLayout1.setRefreshing(false);
                    NumberOfRefreshMyEvents = 0;
                    fromSwipe = true;
                }
            });

        isFirstTimeMyEvents = true;

        int last = layoutManagerMyActivities.findLastCompletelyVisibleItemPosition();
        int count = myAdapter.getItemCount();
        if (last + 1 < count && !fromSwipe) {
            layoutManagerMyActivities.smoothScrollToPosition(mylistView, null, last + 1);
        }
        fromSwipe = false;

        checkIsHappeningNow();
    }

    private void checkIsHappeningNow(){

        Event happeningEvent = Persistance.getInstance().getHappeningNow(activity);
        boolean HappeningNowStartedAlready = (happeningEvent != null);

        // Check if we should display Happening now
        if(myEventList.size() != 0 || HappeningNowStartedAlready){

            Event upcomingEvent = null;

            if(HappeningNowStartedAlready){
                checkinButton = (Button) v.findViewById(R.id.checkinHN);
                checkinButton.setText("CHECK-OUT");

                buttonState = 1;

                upcomingEvent = happeningEvent;
            }
            else {
                // Get the closest to current date event
                upcomingEvent = myEventList.get(0);
            }
            // Check the date
            long dateUpcomingEvent = upcomingEvent.eventDateTimeStamp;
            long currentTime = System.currentTimeMillis() / 1000L;

            long acceptableTimeBetweenNextEvent = 30 * 60;

            long differenceBetween = Math.abs(dateUpcomingEvent - currentTime);

            if (differenceBetween < acceptableTimeBetweenNextEvent || HappeningNowStartedAlready) {
                happeningNowLayout = (RelativeLayout) v.findViewById(R.id.generalHappeningNowLayout);
                params = happeningNowLayout.getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                happeningNowLayout.setLayoutParams(params);

                setHappeningNowLayoutData(upcomingEvent);
            }
        }
    }

    private void setHappeningNowLayoutData(final Event upcomingEvent) {
        //set happening now field
        TextView weWillplay = (TextView) v.findViewById(R.id.HNPlay);
        TextView sportField = (TextView) v.findViewById(R.id.HNPlayWhat);
        TextView ludicoins = (TextView) v.findViewById(R.id.ludicoinsHN);
        TextView points = (TextView) v.findViewById(R.id.pointsHN);
        TextView location = (TextView) v.findViewById(R.id.locationHN);
        ImageView imageViewBackground = (ImageView) v.findViewById(R.id.imageViewBackground);
        CircleImageView friends0 = (CircleImageView) v.findViewById(R.id.friends0HN);
        CircleImageView friends1 = (CircleImageView) v.findViewById(R.id.friends1HN);
        CircleImageView friends2 = (CircleImageView) v.findViewById(R.id.friends2HN);
        TextView allFriends = (TextView) v.findViewById(R.id.friendsNumberHN);
        checkinButton = (Button) v.findViewById(R.id.checkinHN);

        Sport sport = new Sport(upcomingEvent.sportCode);
        String weWillPlayString = "";
        String sportName = "";

        if (upcomingEvent.sportCode.equalsIgnoreCase("JOG") || upcomingEvent.sportCode.equalsIgnoreCase("GYM") || upcomingEvent.sportCode.equalsIgnoreCase("CYC")) {
            weWillPlayString = "Will go to ";
            sportName = sport.sportName;
        } else {
            if (upcomingEvent.sportCode.equalsIgnoreCase("OTH")) {
                weWillPlayString = "Will play ";
                sportName = upcomingEvent.otherSportName;
            } else {
                weWillPlayString = "Will play ";
                sportName = sport.sportName;
            }
        }
        sportName = sportName.substring(0, 1).toUpperCase() + sportName.substring(1);

        weWillplay.setText(weWillPlayString);
        sportField.setText(sportName);
        ludicoins.setText("+ " + String.valueOf(upcomingEvent.ludicoins));
        points.setText("+ " + String.valueOf(upcomingEvent.points));
        location.setText(upcomingEvent.placeName);

        switch (upcomingEvent.sportCode) {
            case "FOT":
                imageViewBackground.setBackgroundResource(R.drawable.bg_sport_football);
                break;
            case "BAS":
                imageViewBackground.setBackgroundResource(R.drawable.bg_sport_basketball);
                break;
            case "VOL":
                imageViewBackground.setBackgroundResource(R.drawable.bg_sport_volleyball);
                break;
            case "JOG":
                imageViewBackground.setBackgroundResource(R.drawable.bg_sport_jogging);
                break;
            case "GYM":
                imageViewBackground.setBackgroundResource(R.drawable.bg_sport_gym);
                break;
            case "CYC":
                imageViewBackground.setBackgroundResource(R.drawable.bg_sport_cycling);
                break;
            case "TEN":
                imageViewBackground.setBackgroundResource(R.drawable.bg_sport_tennis);
                break;
            case "PIN":
                imageViewBackground.setBackgroundResource(R.drawable.bg_sport_pingpong);
                break;
            case "SQU":
                imageViewBackground.setBackgroundResource(R.drawable.bg_sport_squash);
                break;
            case "OTH":
                imageViewBackground.setBackgroundResource(R.drawable.bg_sport_others);
                break;
        }

        if (upcomingEvent.numberOfParticipants - 1 >= 1) {
            friends0.setVisibility(View.VISIBLE);
        }
        if (upcomingEvent.numberOfParticipants - 1 >= 2) {
            friends1.setVisibility(View.VISIBLE);
        }
        if (upcomingEvent.numberOfParticipants - 1 >= 3) {
            friends2.setVisibility(View.VISIBLE);
        }
        if (upcomingEvent.numberOfParticipants - 1 >= 4) {
            allFriends.setVisibility(View.VISIBLE);
            allFriends.setText("+" + String.valueOf(upcomingEvent.numberOfParticipants - 4));

        }
        for (int i = 0; i < upcomingEvent.participansProfilePicture.size(); i++) {
            if (!upcomingEvent.participansProfilePicture.get(i).equals("") && i == 0) {
                Bitmap bitmap = decodeBase64(upcomingEvent.participansProfilePicture.get(i));
                friends0.setImageBitmap(bitmap);
            } else
            if (!upcomingEvent.participansProfilePicture.get(i).equals("") && i == 1) {
                Bitmap bitmap = decodeBase64(upcomingEvent.participansProfilePicture.get(i));
                friends1.setImageBitmap(bitmap);
            } else
            if (!upcomingEvent.participansProfilePicture.get(i).equals("") && i == 2) {
                Bitmap bitmap = decodeBase64(upcomingEvent.participansProfilePicture.get(i));
                friends2.setImageBitmap(bitmap);
            }
        }

        happeningNowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> params = new HashMap<String, String>();
                HashMap<String, String> headers = new HashMap<String, String>();
                HashMap<String, String> urlParams = new HashMap<String, String>();
                headers.put("authKey", Persistance.getInstance().getUserInfo(activity).authKey);

                //set urlParams
                urlParams.put("eventId", upcomingEvent.id);
                urlParams.put("userId", Persistance.getInstance().getUserInfo(activity).id);
                HTTPResponseController.getInstance().getEventDetails(params, headers, activity, urlParams);
            }
        });

        checkinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Button not pressed yet -> user press "CHECK-IN"
                if (buttonState == 0) {
                    // Check if user is < 1 km away from the place
                    LocationCheckState locationState = checkedLocation(upcomingEvent);

                    if (locationState == LocationCheckState.LOCATION_OK) {

                        // Yes it is, let's make the check-in for him
                        HashMap<String, String> params = new HashMap<String, String>();
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("authKey", Persistance.getInstance().getUserInfo(activity).authKey);
                        params.put("eventId", upcomingEvent.id);
                        HTTPResponseController.getInstance().checkin(params, headers, activity);

                        buttonState = 1;

                        checkinButton.setText("CHECK-OUT");
                        HappeningNowLocation happeningNowLocation = new HappeningNowLocation();
                        happeningNowLocation.startDate = String.valueOf(System.currentTimeMillis() / 1000);

                        Persistance.getInstance().setHappeningNow(upcomingEvent, activity);
                        Persistance.getInstance().setLocation(activity, happeningNowLocation);

                        requestLocationUpdates();

                    } else
                        if (locationState == LocationCheckState.LOCATION_NOT_IN_RANGE){
                            // No, he is not, don't let him start the event
                            Toast.makeText(activity, getResources().getString(R.string.go_to_event_location_to_start_sweating_on_points), Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(activity, getResources().getString(R.string.we_cant_take_your_location), Toast.LENGTH_LONG).show();
                        }
                }
                // Button is pressed, time is counted -> user press "CHECK-OUT"
                else if (buttonState == 1) {

                    // Confirmation dialog for stop the event
                    final Typeface typeFace = Typeface.createFromAsset(getActivity().getAssets(),
                            "fonts/Quicksand-Medium.ttf");
                    final Typeface typeFaceBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quicksand-Bold.ttf");
                    final ConfirmationDialog confirmationDialog = new ConfirmationDialog(getActivity());
                    confirmationDialog.show();
                    confirmationDialog.title.setText(R.string.confirm);
                    confirmationDialog.title.setTypeface(typeFaceBold);
                    confirmationDialog.message.setText(R.string.are_you_sure_you_want_to_stop_sweating_on_points);
                    confirmationDialog.message.setTypeface(typeFace);
                    confirmationDialog.confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                // User confirms he wants to stop the event
                                checkinButton.setText(R.string.check_in);

                                savePoints();

                                buttonState = 0;

                                ViewGroup.LayoutParams params = happeningNowLayout.getLayoutParams();
                                params.height = 0;
                                happeningNowLayout.setLayoutParams(params);

                                // Clean up
                                if (googleApiClient.isConnected()) {
                                    googleApiClient.disconnect();
                                }
                                //LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, (LocationListener) activity);

                                confirmationDialog.dismiss();
                                Persistance.getInstance().setHappeningNow(null, activity);
                                Persistance.getInstance().setLocation(activity, null);
                            }
                            catch (Exception ex){   ex.printStackTrace();}
                        }
                    });
                    confirmationDialog.dismiss.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            confirmationDialog.dismiss();
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            getAroundMeEvents("0", latitude, longitude);

            // Should get the location again
            HappeningNowLocation happeningNowLocation = Persistance.getInstance().getLocation(activity);
            if(happeningNowLocation != null) {
                requestLocationUpdates();
            }
        }
        catch (NullPointerException e) { }
        catch (IllegalStateException ex) { }
    }


    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("Connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        System.out.println("Connection failed");
    }


    @Override
    public void onLocationChanged(Location location) {
        System.out.println(location.getLatitude() + " new api");

         HappeningNowLocation happeningNowLocation = Persistance.getInstance().getLocation(activity);
        if(happeningNowLocation != null) {
            happeningNowLocation.locationList.add(new LocationInfo(location.getLatitude(), location.getLongitude()));

            Persistance.getInstance().setLocation(activity, happeningNowLocation);
        }
    }

    private void requestLocationUpdates() {
        if(!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }

        locationListener = this;

        if(locationRequest == null){
            locationRequest = new LocationRequest();
            locationRequest.setInterval(60 * 1000);
            locationRequest.setFastestInterval(10 * 1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    private void onInternetRefresh() {
        getFirstPageMyActivity = true;
        NumberOfRefreshMyEvents = 0;
        pageNumberAroundMe = 0;
        NumberOfRefreshAroundMe = 0;

        getMyEvents("0");
        getAroundMeEvents("0", latitude, longitude);

        SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh1);
        mSwipeRefreshLayout.setRefreshing(false);

        SwipeRefreshLayout mSwipeRefreshLayout2 = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh2);
        mSwipeRefreshLayout2.setRefreshing(false);
    }

    private void prepareError(String message) {
        v.findViewById(R.id.internetRefresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setOnClickListener(null);
                onInternetRefresh();
            }
        });
        RelativeLayout ll = (RelativeLayout) v.findViewById(R.id.noInternetLayout);

        TextView noConnection = (TextView) ll.findViewById(R.id.noConnectionText);
        noConnection.setText(message);

        final float scale = getContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (56 * scale + 0.5f);
        ll.getLayoutParams().height = pixels;
        ll.setLayoutParams(ll.getLayoutParams());
    }

    private void hideError() {
        RelativeLayout ll = (RelativeLayout) v.findViewById(R.id.noInternetLayout);
        ll.getLayoutParams().height = 0;
        ll.setLayoutParams(ll.getLayoutParams());
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        try {
            if (error.getMessage().contains("error")) {
                String json = trimMessage(error.getMessage(), "error");
                if (json != null) {
                    System.out.println(error.getMessage());
                    Toast.makeText(super.getContext(), json, Toast.LENGTH_LONG).show();
                }
            } else {
                System.out.println(error.getMessage());
                Toast.makeText(super.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }

            Log.d("Response", error.toString());
            if (error instanceof NetworkError) {
                this.prepareError(getResources().getString(R.string.no_internet_connection));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    public String trimMessage(String json, String key) {
        String trimmedString = "";

        try {
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
            if (trimmedString.equalsIgnoreCase("Invalid Auth Key provided.")) {
                deleteCachedInfo();
                Intent intent = new Intent(activity, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }

        return trimmedString;
    }

    public static void deleteCachedInfo() {
        DatabaseReference userNode = FirebaseDatabase.getInstance().getReference().child("users").child(Persistance.getInstance().getUserInfo(activity).id);
        userNode.child("activeToken").setValue(false);

        Persistance.getInstance().deleteUserProfileInfo(activity);

        SharedPreferences preferences = activity.getSharedPreferences("ProfileImage", 0);
        preferences.edit().remove("ProfileImage").apply();

        Persistance.getInstance().setHappeningNow(null, activity);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    // Call the API for getting the events around the user
    public void getAroundMeEvents(String pageNumber, Double latitude, Double longitude) {
        HashMap<String, String> params = new HashMap<String, String>();
        HashMap<String, String> headers = new HashMap<String, String>();
        HashMap<String, String> urlParams = new HashMap<String, String>();
        headers.put("authKey", Persistance.getInstance().getUserInfo(activity).authKey);

        pageNumberAroundMe =  Integer.parseInt(pageNumber);

        GPSTracker gps = new GPSTracker(activity.getApplicationContext(), activity);
        if (!gps.canGetLocation()) {
            this.noGps = true;
            this.prepareError(getResources().getString(R.string.no_location_services_available));

            //return;
        }
        else{
            hideError();
            this.noGps = false;

            // After location services turned off, wake them alive
            if(latitude == 0) {
                if (!googleApiClient.isConnected()) {
                    googleApiClient.connect();
                }

                Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
        }

        //set urlParams
        urlParams.put("userId", Persistance.getInstance().getUserInfo(activity).id);
        urlParams.put("pageNumber", pageNumber);
        urlParams.put("userLatitude", String.valueOf(latitude));
        urlParams.put("userLongitude", String.valueOf(longitude));
        urlParams.put("userRange", "" + Persistance.getInstance().getUserInfo(activity).range);

        String userSport = "";
        for (int i = 0; i < Persistance.getInstance().getUserInfo(activity).sports.size(); i++) {
            if (i < Persistance.getInstance().getUserInfo(activity).sports.size() - 1) {
                userSport = userSport + Persistance.getInstance().getUserInfo(activity).sports.get(i).code + ";";
            } else {
                userSport = userSport + Persistance.getInstance().getUserInfo(activity).sports.get(i).code;
            }
        }
        urlParams.put("userSports", userSport);

        //get Around Me Event
        HTTPResponseController.getInstance().getAroundMeEvent(params, headers, activity, urlParams, this);
    }

    // Call the API for getting user events
    public void getMyEvents(String pageNumber) {
        ActivitiesActivity.getFirstPageMyActivity = pageNumber.equals("0");

        HashMap<String, String> params = new HashMap<>();
        HashMap<String, String> headers = new HashMap<>();
        HashMap<String, String> urlParams = new HashMap<>();
        headers.put("authKey", Persistance.getInstance().getUserInfo(activity).authKey);

        //set urlParams
        urlParams.put("userId", Persistance.getInstance().getUserInfo(activity).id);
        urlParams.put("pageNumber", pageNumber);

        //get My Events
        HTTPResponseController.getInstance().getMyEvent(params, headers, activity, urlParams, this);
    }

}
