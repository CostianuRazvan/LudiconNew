package larc.ludiconprod.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.squareup.picasso.Downloader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import larc.ludiconprod.Adapters.ChatAndFriends.FriendsAdapter;
import larc.ludiconprod.Adapters.CouponsActivity.CouponsAdapter;
import larc.ludiconprod.Adapters.CouponsActivity.MyCouponsAdapter;
import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.User;
import larc.ludiconprod.Utils.Coupon;
import larc.ludiconprod.Utils.CouponsUtils.CouponsPagerAdapter;
import larc.ludiconprod.Utils.Location.GPSTracker;
import larc.ludiconprod.Utils.Quest;
import larc.ludiconprod.Utils.ui.SlidingTabLayout;
import larc.ludiconprod.Utils.util.Sport;

import static larc.ludiconprod.Activities.ActivitiesActivity.deleteCachedInfo;

public class CouponsActivity extends Fragment implements Response.ErrorListener, Response.Listener<JSONObject> {

    private static CharSequence[] TITLES;

    private Context mContext;
    private int tabsNumber = 2;
    private View v;
    private CouponsPagerAdapter adapter;
    private ViewPager pager;
    private SlidingTabLayout tabs;
    private CouponsAdapter couponsAdapter;
    private MyCouponsAdapter myCouponsAdapter;

    public ArrayList<Quest> quests = new ArrayList<>();
    public ArrayList<Quest> myQuests = new ArrayList<>();

    boolean firstTimeCoupons = false;
    public int couponsPage = 0;
    public boolean firstPageCoupons = true;
    boolean addedSwipeCoupons = false;

    boolean firstTimeMyCoupons = false;
    public int myCouponsPage = 0;
    public boolean firstPageMyCoupons = true;
    boolean addedSwipeMyCoupons = false;
    private boolean noGps = false;
    FragmentActivity activity;

    public void getCoupons(String pageNumber) {
        /*if (pageNumber.equals("0")) {
            this.coupons.clear();
            ArrayList<Coupon> c = Persistance.getInstance().getCouponsCache(super.getActivity());
            this.coupons.addAll(c);
            this.couponsAdapter.notifyDataSetChanged();
        }*/

        HashMap<String, String> headers = new HashMap<>();
        //headers.put("Content-Type", "application/json");
        headers.put("authKey", Persistance.getInstance().getUserInfo(activity).authKey);
        String urlParams = "";

        /*
        //set urlParams
        double longitude = 0;
        double latitude = 0;
        GPSTracker gps = new GPSTracker(activity.getApplicationContext(), activity);
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            gps.stopUsingGPS();
            this.noGps = false;
        } else {
            this.noGps = true;
            this.prepareError("No location services available!");
            return;
        }

        urlParams += "userId=" + Persistance.getInstance().getUserInfo(activity).id;
        urlParams += "&page=" + pageNumber;
        urlParams += "&latitude=" + String.valueOf(latitude);
        urlParams += "&longitude=" + String.valueOf(longitude);
        urlParams += "&range=" + 10000;
        String userSport = "";
        ArrayList<Sport> sports = Persistance.getInstance().getUserInfo(activity).sports;
        for (int i = 0; i < sports.size(); ++i) {
            if (i < sports.size() - 1) {
                userSport = userSport + sports.get(i).code + ";";
            } else {
                userSport = userSport + sports.get(i).code;
            }
        }
        urlParams += "&sportList=" + userSport;
        */
        urlParams += "userId=" + Persistance.getInstance().getUserInfo(activity).id;

        HTTPResponseController.getInstance().getCoupons(urlParams, headers, this, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                onCouponsResponse(response);
            }
        }, this);
    }

    public void getMyCoupons(String pageNumber) {
        /*if (pageNumber.equals("0")) {
            this.myCoupons.clear();
            ArrayList<Coupon> c = Persistance.getInstance().getMyCouponsCache(super.getActivity());
            this.myCoupons.addAll(c);
            this.myCouponsAdapter.notifyDataSetChanged();
        }*/

        HashMap<String, String> headers = new HashMap<>();
        //headers.put("Content-Type", "application/json");
        headers.put("authKey", Persistance.getInstance().getUserInfo(activity).authKey);
        String urlParams = "";

        urlParams += "userId=" + Persistance.getInstance().getUserInfo(activity).id;
        //urlParams += "&page=" + pageNumber;

        HTTPResponseController.getInstance().getMyCoupons(urlParams, headers, this, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                onMyCouponsResponse(response);
            }
        }, this);
    }

    public void updateCouponsList() {
        RelativeLayout ll = (RelativeLayout) v.findViewById(R.id.noInternetLayout);
        ll.getLayoutParams().height = 0;
        ll.setLayoutParams(ll.getLayoutParams());
        if (this.noGps) {
            this.prepareError("No location services available!");
        }

        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.couponsSwapRefresh);

        this.couponsAdapter.notifyDataSetChanged();
        final ListView listView = (ListView) v.findViewById(R.id.couponsList);
        ImageView heartImage = (ImageView) v.findViewById(R.id.heartImageCoupons);
        final ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.progressCoupons);
        progressBar.setIndeterminate(true);
        progressBar.setAlpha(0f);

        TextView noCoupons = (TextView) v.findViewById(R.id.noCoupons);
        TextView discoverActivities = (TextView) v.findViewById(R.id.noCouponsText);
        Button noMyCouponsButton = (Button) v.findViewById(R.id.noCouponsButton);

        if (!this.firstTimeCoupons) {
            listView.setAdapter(this.couponsAdapter);
        }
        if (this.quests.size() == 0) {
            heartImage.setVisibility(View.VISIBLE);
            noCoupons.setVisibility(View.VISIBLE);
            discoverActivities.setVisibility(View.VISIBLE);
            noMyCouponsButton.setVisibility(View.VISIBLE);
            noMyCouponsButton.setEnabled(true);
            noMyCouponsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*Intent intent = new Intent(getActivity(), Main.class);
                    intent.putExtra("Tab", R.id.tab_activities);
                    startActivity(intent);*/
                    Main mainFr = (Main) activity;
                    mainFr.bottomBar.setDefaultTab(R.id.tab_activities);
                }
            });
        } else {
            heartImage.setVisibility(View.INVISIBLE);
            noCoupons.setVisibility(View.INVISIBLE);
            discoverActivities.setVisibility(View.INVISIBLE);
            noMyCouponsButton.setVisibility(View.INVISIBLE);
            noMyCouponsButton.setEnabled(false);
        }

        if (listView != null) {
            /*
            listView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(final View v, MotionEvent event) {
                    if (v != null && listView.getChildCount() > 0) {
                        if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                            if (listView.getLastVisiblePosition() == listView.getAdapter().getCount() - 1 && listView.getChildAt(listView.getChildCount() - 1).getBottom() <= listView.getHeight()) {
                                progressBar.setAlpha(1f);
                                ++couponsPage;
                                getCoupons(String.valueOf(couponsPage));
                            }
                        }
                    }
                    return false;
                }
            });
            */
        }
        if (!this.addedSwipeCoupons) {
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    quests.clear();
                    couponsPage = 0;
                    getCoupons("0");
                    firstPageCoupons = true;
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
            this.addedSwipeCoupons = true;
        }
        progressBar.setAlpha(0f);
        this.firstTimeCoupons = true;

        int last = listView.getLastVisiblePosition();
        int count = couponsAdapter.getCount();
        if (last + 1 < count) {
            listView.smoothScrollToPosition(last + 1);
        }
    }

    public void updateMyCouponsList() {
        RelativeLayout ll = (RelativeLayout) v.findViewById(R.id.noInternetLayout);
        ll.getLayoutParams().height = 0;
        ll.setLayoutParams(ll.getLayoutParams());
        if (this.noGps) {
            this.prepareError("No location services available!");
        }

        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.myCouponsSwapRefresh);

        this.myCouponsAdapter.notifyDataSetChanged();
        final ListView listView = (ListView) v.findViewById(R.id.myCouponsList);
        ImageView heartImage = (ImageView) v.findViewById(R.id.heartImageMyCoupons);
        final ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.progressMyCoupons);
        progressBar.setIndeterminate(true);
        progressBar.setAlpha(0f);

        TextView noCoupons = (TextView) v.findViewById(R.id.noMyCoupons);
        TextView discoverActivities = (TextView) v.findViewById(R.id.noMyCouponsText);
        Button noMyCouponsButton = (Button) v.findViewById(R.id.noMyCouponsButton);

        if (!this.firstTimeMyCoupons) {
            listView.setAdapter(this.myCouponsAdapter);
        }
        if (this.myQuests.size() == 0) {
            heartImage.setVisibility(View.VISIBLE);
            noCoupons.setVisibility(View.VISIBLE);
            discoverActivities.setVisibility(View.VISIBLE);
            noMyCouponsButton.setVisibility(View.VISIBLE);
            noMyCouponsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pager.setCurrentItem(0);
                }
            });
            noMyCouponsButton.setEnabled(true);
        } else {
            heartImage.setVisibility(View.INVISIBLE);
            noCoupons.setVisibility(View.INVISIBLE);
            discoverActivities.setVisibility(View.INVISIBLE);
            noMyCouponsButton.setVisibility(View.INVISIBLE);
            noMyCouponsButton.setEnabled(false);
        }

        if (listView != null) {
            /*
            listView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(final View v, MotionEvent event) {
                    if (v != null && listView.getChildCount() > 0) {
                        if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                            if (listView.getLastVisiblePosition() == listView.getAdapter().getCount() - 1 && listView.getChildAt(listView.getChildCount() - 1).getBottom() <= listView.getHeight()) {
                                progressBar.setAlpha(1f);
                                ++myCouponsPage;
                                getMyCoupons(String.valueOf(myCouponsPage));
                            }
                        }
                    }
                    return false;
                }
            });
            */
        }
        if (!this.addedSwipeMyCoupons) {
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    myQuests.clear();
                    myCouponsPage = 0;
                    getMyCoupons("0");
                    firstPageMyCoupons = true;
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
            this.addedSwipeMyCoupons = true;
        }
        progressBar.setAlpha(0f);
        this.firstTimeMyCoupons = true;

        int last = listView.getLastVisiblePosition();
        int count = myCouponsAdapter.getCount();
        if (last + 1 < count) {
            listView.smoothScrollToPosition(last + 1);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (error != null) {
            if (error.getMessage().contains("error")) {
                String json = trimMessage(error.getMessage(), "error");
                if (json != null) {
                    if (json.compareTo("User does not have enough points to redeem coupon.") == 0) {
                        Toast.makeText(super.getContext(), R.string.you_do_not_have_enough_ludicoins_to_redeem_the_coupon, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(super.getContext(), json, Toast.LENGTH_LONG).show();
                    }
                }
        } else {
            Toast.makeText(super.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
        if (error instanceof NetworkError) {
            this.prepareError(getResources().getString(R.string.no_internet_connection));
        } else {
            RelativeLayout ll = (RelativeLayout) v.findViewById(R.id.noInternetLayout);
            ll.getLayoutParams().height = 0;
            ll.setLayoutParams(ll.getLayoutParams());
        }
    }

    public String trimMessage(String json, String key) {
        String trimmedString = null;

        try {
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
            if(trimmedString.equalsIgnoreCase("Invalid Auth Key provided.")){
                deleteCachedInfo();
                Intent intent =new Intent(activity,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return trimmedString;
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

    private void onInternetRefresh() {
        quests.clear();
        couponsPage = 0;
        getCoupons("0");
        firstPageCoupons = true;
        SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.couponsSwapRefresh);
        mSwipeRefreshLayout.setRefreshing(false);

        myQuests.clear();
        myCouponsPage = 0;
        getMyCoupons("0");
        firstPageMyCoupons = true;
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.myCouponsSwapRefresh);
        mSwipeRefreshLayout.setRefreshing(false);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = inflater.getContext();
        v = inflater.inflate(R.layout.coupons_activity, container, false);
        while (activity == null) {
            activity = getActivity();
        }

        try {
            super.onCreate(savedInstanceState);

            TITLES = new CharSequence[]{getResources().getString(R.string.quests), getResources().getString(R.string.my_quests)};
            this.adapter = new CouponsPagerAdapter(this.getFragmentManager(), CouponsActivity.TITLES, this.tabsNumber, this);

            pager = (ViewPager) v.findViewById(R.id.couponsPager);
            pager.setAdapter(adapter);

            this.tabs = (SlidingTabLayout) v.findViewById(R.id.couponsTabs);
            this.tabs.setDistributeEvenly(false);

            this.tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    return getResources().getColor(R.color.tabsScrollColor);
                }
            });

            this.tabs.setViewPager(pager);

            this.couponsAdapter = new CouponsAdapter(this.quests, activity.getApplicationContext(), activity, getResources(), this);
            this.myCouponsAdapter = new MyCouponsAdapter(this.myQuests, activity.getApplicationContext(), activity, getResources(), this);

            getCoupons("0");
            getMyCoupons("0");

            this.couponsPage = 0;
            this.myCouponsPage = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }

    public void onCouponsResponse(JSONObject response) {
        if (super.getActivity() == null) {
            return;
        }

        try {
            JSONArray questsJson = response.getJSONArray("quests");

            if (this.couponsPage == 0) {
                this.quests.clear();
            }

            Quest quest;
            for (int i = 0; i < questsJson.length(); ++i) {
                JSONObject o = questsJson.getJSONObject(i);
                quest = new Quest(
                  o.getString("questID"), o.getString("title"), o.getString("description"),
                        o.getString("image"), (o.getString("expirydate") == null || o.getString("expirydate").compareTo("null") == 0) ? 0 : Long.parseLong(o.getString("expirydate")),
                        Integer.parseInt(o.getString("difficulty")), 0 /*Integer.parseInt(o.getString("participantsCount"))*/, Integer.parseInt(o.getString("points"))
                );

                this.quests.add(quest);
            }

            /*if (this.couponsPage == 0) {
                Persistance.getInstance().setCouponsCache(this.coupons, super.getActivity());
            }*/

            this.updateCouponsList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onMyCouponsResponse(JSONObject response) {
        if (super.getActivity() == null) {
            return;
        }

        try {
            JSONArray myQuests = response.getJSONArray("quests");

            if (this.myCouponsPage == 0) {
                this.myQuests.clear();
            }

            Quest quest;
            for (int i = 0; i < myQuests.length(); ++i) {
                JSONObject o = myQuests.getJSONObject(i);

                quest = new Quest(
                        o.getString("questID"), o.getString("title"), o.getString("description"),
                        o.getString("image"), (o.getString("expirydate") == null || o.getString("expirydate").compareTo("null") == 0) ? 0 : Long.parseLong(o.getString("expirydate")),
                        Integer.parseInt(o.getString("difficulty")), Integer.parseInt(o.getString("status")), Integer.parseInt(o.getString("currentprogress")), Integer.parseInt(o.getString("totalprogress")),
                        (o.getString("completiondate") == null || o.getString("completiondate").compareTo("null") == 0) ? 0 : Long.parseLong(o.getString("completiondate")) , Integer.parseInt(o.getString("points"))
                );

                this.myQuests.add(quest);
            }

            if (this.myCouponsPage == 0) {
                //Persistance.getInstance().setMyCouponsCache(this.myQuests, super.getActivity());
            }

            this.updateMyCouponsList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        if (super.getActivity() == null) {
            return;
        }

        try {
            if(response.get("status") != null && ((String)response.get("status")).compareTo("Success") == 0){
                Toast.makeText(activity, R.string.the_prize_is_yours, Toast.LENGTH_SHORT).show();

                // Reset the value for ludicoins
                 User userInfo = Persistance.getInstance().getUserInfo(getActivity());
                userInfo.ludicoins = (int)response.get("pointsLeft");
                Persistance.getInstance().setUserInfo(getActivity(), userInfo);
            }
            else {
                Toast.makeText(activity, "" + response, Toast.LENGTH_SHORT).show();
            }

            this.quests.clear();
            this.getCoupons("0");
            this.firstPageCoupons = true;
            this.couponsPage = 0;

            this.myQuests.clear();
            this.getMyCoupons("0");
            this.firstPageMyCoupons = true;
            this.myCouponsPage = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Response.Listener<JSONObject> onRequestSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                getCoupons("0");
                getMyCoupons("0");
                try {
                    Toast.makeText(mContext, jsonObject.getString("ok"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };
    }
}