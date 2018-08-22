package larc.ludiconprod.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.Dialogs.ConfirmationDialog;
import larc.ludiconprod.R;
import larc.ludiconprod.User;
import larc.ludiconprod.UserProfile;
import larc.ludiconprod.Utils.EventBrief;
import larc.ludiconprod.Utils.MyProfileUtils.Bar;
import larc.ludiconprod.Utils.MyProfileUtils.Iterable;
import larc.ludiconprod.Utils.MyProfileUtils.TopGraph;
import larc.ludiconprod.Utils.util.Sport;

/**
 * Created by alex_ on 10.08.2017.
 */

public class MyProfileActivity extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener, Runnable {

    protected Context mContext;
    protected View v;
    protected ImageView settings;
    static public FragmentActivity activity;
    static boolean isActive = false;
    ConfirmationDialog confirmationDialog;
    LinearLayout lastEvents;
    RelativeLayout history;
    Button BtnShowFullHistory;

    ArrayList<String> lastEvName = new ArrayList<>();
    ArrayList<String> eventDate = new ArrayList<>();
    ArrayList<String> participantsCount = new ArrayList<>();

    int layout_id_start = 789;

    @SuppressLint("ResourceType")
    public void add_linLayout(){
        for (int i = 0; i< lastEvName.size(); i++) {

            LinearLayout linLayout = new LinearLayout(this.getContext());
            linLayout.setOrientation(LinearLayout.HORIZONTAL);
            linLayout.setPadding(0, 10, 0, 0);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            linLayout.setLayoutParams(params);
            linLayout.setId(layout_id_start + i);

            if (i != 0) {
                params.addRule(RelativeLayout.BELOW, linLayout.getId() - 1);
            }

            ImageView img = new ImageView(this.getContext());
            if (lastEvName.get(i).equals(getResources().getString(R.string.football))) {
                img.setImageResource(R.drawable.ic_sport_football);
            } else if (lastEvName.get(i).equals(getResources().getString(R.string.basketball))) {
                img.setImageResource(R.drawable.ic_sport_basketball);
            } else if (lastEvName.get(i).equals(getResources().getString(R.string.cycling))) {
                img.setImageResource(R.drawable.ic_sport_cycling);
            } else if (lastEvName.get(i).equals(getResources().getString(R.string.gym))) {
                img.setImageResource(R.drawable.ic_sport_gym);
            } else if (lastEvName.get(i).equals(getResources().getString(R.string.jogging))) {
                img.setImageResource(R.drawable.ic_sport_jogging);
            } else if (lastEvName.get(i).equals(getResources().getString(R.string.ping_pong))) {
                img.setImageResource(R.drawable.ic_sport_pingpong);
            } else if (lastEvName.get(i).equals(getResources().getString(R.string.squash))) {
                img.setImageResource(R.drawable.ic_sport_squash);
            } else if (lastEvName.get(i).equals(getResources().getString(R.string.tennis))) {
                img.setImageResource(R.drawable.ic_sport_tennis);
            } else if (lastEvName.get(i).equals(getResources().getString(R.string.volleyball))) {
                img.setImageResource(R.drawable.ic_sport_voleyball);
            } else {
                img.setImageResource(R.drawable.ic_sport_others);
            }

            RelativeLayout.LayoutParams imgParams = new RelativeLayout.LayoutParams(60, 60);
            img.setLayoutParams(imgParams);
            linLayout.addView(img);

            final TextView et = new TextView(this.getContext());
            String name = lastEvName.get(i);
            String date = eventDate.get(i);
            String count = participantsCount.get(i);

            if (count.equals("1")) {
                et.setText(date + " " + name + " " + getResources().getString(R.string.with_one_other) + ".");
            } else {
                if (Locale.getDefault().getLanguage().startsWith("en")) {
                    et.setText(date + " " + name + " " + getResources().getString(R.string.with) + " " + count + " " + getResources().getString(R.string.others1) + ".");
                } else if (Locale.getDefault().getLanguage().startsWith("ro")) {
                    et.setText(date + " " + name + " " + getResources().getString(R.string.with_others) + " " + count + ".");
                }else if (Locale.getDefault().getLanguage().startsWith("fr")) {
                    et.setText(date + " " + name + " " + getResources().getString(R.string.with_others) + " " + count + ".");
                }
            }

            String historyEntry = et.getText().toString();
            SpannableStringBuilder spanTxt = new SpannableStringBuilder( et.getText());
            int indexSecondSpace = historyEntry.indexOf(' ', historyEntry.indexOf(' ') + 1 );
            int indexThirdSpace = historyEntry.indexOf(' ', indexSecondSpace + 1 );

            spanTxt.setSpan(new ForegroundColorSpan(Color.parseColor("#d4498b")), 0, indexSecondSpace, 0);
            spanTxt.setSpan(new ForegroundColorSpan(Color.parseColor("#1573ba")), indexSecondSpace, indexThirdSpace, 0);
            spanTxt.setSpan(new ForegroundColorSpan(Color.parseColor("#acb8c1")), indexThirdSpace, spanTxt.length(), 0);
            et.setText(spanTxt);

            et.setPadding(20, 0, 0, 0);
            linLayout.addView(et);

            history.addView(linLayout);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = inflater.getContext();

        v = inflater.inflate(R.layout.my_profile_activity, container, false);
        while (activity == null) {
            activity = getActivity();
        }

        try {
            super.onCreate(savedInstanceState);

            TextView profileTitle = (TextView) v.findViewById(R.id.profileTitle);
            final Typeface typeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quicksand-Medium.ttf");
            final Typeface typeFaceBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quicksand-Bold" +
                    ".ttf");
            profileTitle.setTypeface(typeFace);
            profileTitle.setTextColor(getResources().getColor(R.color.darkblue));

            Button logout = (Button) v.findViewById(R.id.profileLogout);
            this.settings = (ImageView) v.findViewById(R.id.settings);

            v.findViewById(R.id.ludicoinsLayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, BalanceActivity.class);
                    startActivity(intent);
                }
            });



            ((TextView) v.findViewById(R.id.profileTitle)).setTypeface(typeFace);
            ((TextView) v.findViewById(R.id.profileLudicoins)).setTypeface(typeFace);
            ((TextView) v.findViewById(R.id.profileToNextLevel)).setTypeface(typeFace);
            ((TextView) v.findViewById(R.id.profileToNextLevelText)).setTypeface(typeFace);
            ((TextView) v.findViewById(R.id.profileToNextLevelText)).setTypeface(typeFace);
            ((TextView) v.findViewById(R.id.profileLevel)).setTypeface(typeFace);
            ((TextView) v.findViewById(R.id.profileLevelText)).setTypeface(typeFace);
            ((TextView) v.findViewById(R.id.profilePoints)).setTypeface(typeFace);
            ((TextView) v.findViewById(R.id.profilePointsText)).setTypeface(typeFace);
            ((TextView) v.findViewById(R.id.profilePosition)).setTypeface(typeFace);
            ((TextView) v.findViewById(R.id.profilePositionText)).setTypeface(typeFace);

            ((TextView) v.findViewById(R.id.profilePracticeSportsLabel)).setTypeface(typeFaceBold);
            ((TextView) v.findViewById(R.id.profilePracticeSportsCountLabel)).setTypeface(typeFace);
            ((TextView) v.findViewById(R.id.versusLabel)).setTypeface(typeFaceBold);

            ((TextView) v.findViewById(R.id.profileTotalEventsLabel)).setTypeface(typeFace);
            ((TextView) v.findViewById(R.id.profileTotalPointsLabel)).setTypeface(typeFace);
            ((TextView) v.findViewById(R.id.profileTotalEvents)).setTypeface(typeFace);
            ((TextView) v.findViewById(R.id.profileTotalPoints)).setTypeface(typeFace);
            ((TextView) v.findViewById(R.id.ProfileHistoryLabel)).setTypeface(typeFaceBold);
            lastEvents = (LinearLayout) v.findViewById(R.id.RVProfileHistory);
            history = (RelativeLayout) v.findViewById(R.id.history);
            BtnShowFullHistory = (Button) v.findViewById(R.id.BtnShowFullHistory);

            UserProfile up = Persistance.getInstance().getProfileInfo(super.getActivity());
            if (up != null) {
                this.printInfo(up);
            }

            LinearLayout monthsLayout = (LinearLayout) v.findViewById(R.id.months);
            int size = monthsLayout.getChildCount();
            for (int i = 0; i < size; ++i) {
                TextView tv = (TextView) monthsLayout.getChildAt(i);
                tv.setTypeface(typeFace);
            }

            ((Button) v.findViewById(R.id.profileLogout)).setTypeface(typeFaceBold);

            logout.setOnClickListener(new View.OnClickListener() {
                final Typeface typeFace = Typeface.createFromAsset(activity.getAssets(), "fonts/Quicksand-Medium.ttf");
                final Typeface typeFaceBold = Typeface.createFromAsset(activity.getAssets(), "fonts/Quicksand-Bold.ttf");

                @Override
                public void onClick(View view) {
                    confirmationDialog = new ConfirmationDialog(getActivity());
                    confirmationDialog.show();
                    confirmationDialog.title.setText(R.string.confirm);
                    confirmationDialog.title.setTypeface(typeFaceBold);
                    confirmationDialog.message.setText(R.string.are_you_sure_you_want_to_logout);
                    confirmationDialog.message.setTypeface(typeFace);
                    confirmationDialog.confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DatabaseReference userNode = FirebaseDatabase.getInstance().getReference().child("users").child(Persistance.getInstance().getUserInfo(activity).id);
                                userNode.child("activeToken").setValue(false);
                                Persistance.getInstance().deleteUserProfileInfo(activity);
                                Log.v("logout", "am dat logout");
                                SharedPreferences preferences = activity.getSharedPreferences("ProfileImage", 0);
                                preferences.edit().remove("ProfileImage").apply();
                                activity.finish();
                                Intent intent = new Intent(mContext, IntroActivity.class);
                                startActivity(intent);
                                confirmationDialog.dismiss();
                                Persistance.getInstance().setHappeningNow(null, getActivity());
                            }
                        });

                        confirmationDialog.dismiss.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                confirmationDialog.dismiss();
                            }
                        });
                    }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*RelativeLayout rl = (RelativeLayout) v.findViewById(R.id.profileGraphs);
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/



        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (animation == null || !animation.isAlive()) {
            animation = new Thread(MyProfileActivity.this);
            animation.start();
        }

        this.requestInfo();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.runningTime = 0;
    }
    @Override
    public void onStart() {
        super.onStart();
        isActive = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        isActive = false;
    }

    private void requestInfo() {
        UserProfile up = Persistance.getInstance().getProfileInfo(super.getActivity());
        if (up == null) {
            View tv = v.findViewById(R.id.profileContent);
            tv.setAlpha(0);
            tv = v.findViewById(R.id.profileProgressBar);
            tv.setAlpha(1);
        }

        User u = Persistance.getInstance().getUserInfo(super.getActivity());

        UserProfile set = new UserProfile();

        set.authKey = u.authKey;
        set.id = u.id;

        Persistance.getInstance().setProfileInfo(super.getActivity(), set);

        HashMap<String, String> params = new HashMap<>();
        HashMap<String, String> headers = new HashMap<>();
        headers.put("authKey", u.authKey);
        HTTPResponseController.getInstance().getUserProfile(params, headers, u.id, activity, this, this);
    }

    private void printGraphInfo(UserProfile u) {
        LinearLayout monthsLayout = (LinearLayout) v.findViewById(R.id.months);
        int size = monthsLayout.getChildCount();
        ArrayList<String> monthsStrings = new ArrayList<>(u.eventsM.keySet());
        UserProfile.sortMonths(monthsStrings);
        TopGraph tg = (TopGraph) v.findViewById(R.id.topGraph);
        String month;
        int max = 0;
        int event;
        for (int i = 0; i < size && i < monthsStrings.size(); ++i) {
            TextView tv = (TextView) monthsLayout.getChildAt(i);
            month = monthsStrings.get(i);
            tv.setText(month);

            event = u.eventsM.get(month);
            tg.setText(i, "" + u.eventsM.get(month));
            if (max < event) {
                max = event;
            }
        }

        for (int i = 0; i < size && i < monthsStrings.size(); ++i) {
            month = monthsStrings.get(i);
            event = u.eventsM.get(month);
            if (max == 0) {
                tg.setProgress(i, 0);
            } else {
                tg.setProgress(i, event * 100 / max);
            }
        }

        LinearLayout barsLayout = (LinearLayout) v.findViewById(R.id.barGraph);
        size = barsLayout.getChildCount();
        ArrayList<Integer> points = new ArrayList<>(u.pointsM.values());
        Bar bar;
        max = 0;
        ArrayList<Bar> bars = new ArrayList<>();
        for (int i = 0; i < size && i < monthsStrings.size(); ++i) {
            month = monthsStrings.get(i);
            int p = u.pointsM.get(month);
            RelativeLayout rl = (RelativeLayout) barsLayout.getChildAt(i);
            bar = (Bar) rl.getChildAt(0);
            bar.setValue(p);
            if (max < p) {
                max = p;
            }
            bars.add(bar);
        }

        for (int i = 0; i < size && i < monthsStrings.size(); ++i) {
            month = monthsStrings.get(i);
            int p = u.pointsM.get(month);
            bar = bars.get(i);
            if (max == 0) {
                bar.setProgress(0);
                continue;
            }
            bar.setProgress(p * 100 / max);
        }

        TextView pointsText = (TextView) v.findViewById(R.id.profileTotalPoints);
        TextView eventsText = (TextView) v.findViewById(R.id.profileTotalEvents);

        pointsText.setText("" + u.points);
        eventsText.setText("" + u.countEventsAttended);

        /*tg.setText(0, "37");
        tg.setText(1, "56");
        tg.setText(2, "20");
        tg.setText(3, "24");
        tg.setText(4, "14");
        tg.setText(5, "48");
        tg.setText(6, "16");
        tg.setText(7, "22");

        tg.setProgress(0, 70);
        tg.setProgress(1, 100);
        tg.setProgress(2, 60);
        tg.setProgress(3, 65);
        tg.setProgress(4, 30);
        tg.setProgress(5, 80);
        tg.setProgress(6, 35);
        tg.setProgress(7, 50);*/
    }
    public void printInfo(UserProfile u) {
        try {
            RelativeLayout ll = (RelativeLayout) v.findViewById(R.id.noInternetLayout);
            ll.getLayoutParams().height = 0;
            ll.setLayoutParams(ll.getLayoutParams());

            TextView toNextLevel = (TextView) v.findViewById(R.id.profileToNextLevel);
            toNextLevel.setText("" + u.pointsToNextLevel);
            TextView sportsCount = (TextView) v.findViewById(R.id.profilePracticeSportsCountLabel);

            ImageView image = (ImageView) v.findViewById(R.id.profileImage);
            if (u.profileImage != null && !u.profileImage.isEmpty()) {
                Bitmap im = IntroActivity.decodeBase64(u.profileImage);
                image.setImageBitmap(im);
            }

            TextView name = (TextView) v.findViewById(R.id.profileName);
            TextView level = (TextView) v.findViewById(R.id.profileLevel);
            TextView points = (TextView) v.findViewById(R.id.profilePoints);
            TextView position = (TextView) v.findViewById(R.id.profilePosition);
            TextView ludiconis = (TextView) v.findViewById(R.id.profileLudicoins);

            name.setText(u.firstName + " " + u.lastName);
            level.setText("" + u.level);
            points.setText("" + u.points);
            position.setText("" + u.position);
            ludiconis.setText("" + u.ludicoins);

            ProgressBar levelBar = (ProgressBar) v.findViewById(R.id.profileLevelBar);
            levelBar.setProgress(u.points * levelBar.getMax() / u.pointsOfNextLevel);

            final ArrayList<String> sportCodes = new ArrayList<>();
            for (Sport s : u.sports) {
                sportCodes.add(s.code);
            }


            lastEvName.clear();
            eventDate.clear();
            participantsCount.clear();
            for (EventBrief ev : u.lastEvents) {
                lastEvName.add(ev.sportName_method(ev.sportName));
                eventDate.add(ev.date);
                participantsCount.add(ev.participantsCount);
            }

            add_linLayout();

            BtnShowFullHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, FullHistory.class);
                    startActivity(intent);
                }
            });

            LinearLayout sportsLayout = (LinearLayout) v.findViewById(R.id.profileSports);
            ImageView sportImage;
            Class t = R.drawable.class;
            ArrayList<String> allSportCodes = new ArrayList<>(Sport.getSportMap().keySet());
            Collections.sort(allSportCodes, new Comparator<String>() {
                @Override
                public int compare(String s, String t1) {
                    if (sportCodes.contains(s) && !sportCodes.contains(t1)) {
                        return -1;
                    }
                    return s.compareTo(t1);
                }
            });

            sportsCount.setText(sportCodes.size() + "/" + allSportCodes.size());
            Resources r = mContext.getResources();
            int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, r.getDisplayMetrics());

            sportsLayout.removeAllViews();
            for (int i = 0; i < allSportCodes.size(); ++i) {
                String sc = allSportCodes.get(i);
                sportImage = new ImageView(getContext());

                sportImage.setImageResource(this.findSportImageResource(sc));
                if (!sportCodes.contains(sc)) {
                    sportImage.setAlpha(0.4f);
                }
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                if (i == allSportCodes.size() - 1) {
                    lp.setMargins(0, 0, 0, 0);
                } else {
                    lp.setMargins(0, 0, px, 0);
                }

                sportsLayout.addView(sportImage, lp);
            }

            this.printGraphInfo(u);

            View tv = v.findViewById(R.id.profileContent);
            tv.setAlpha(1);
            tv = v.findViewById(R.id.profileProgressBar);
            tv.setAlpha(0);

            this.settings.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, EditProfileActivity.class);
                    startActivity(intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int findSportImageResource(String sportCode) {
        switch (sportCode) {
        case "BAS":
            return R.drawable.ic_sport_basketball_large;
        case "CYC":
            return R.drawable.ic_sport_cycling_large;
        case "FOT":
            return R.drawable.ic_sport_football_large;
        case "GYM":
            return R.drawable.ic_sport_gym_large;
        case "JOG":
            return R.drawable.ic_sport_jogging_large;
        case "PIN":
            return R.drawable.ic_sport_pingpong_large;
        case "SQU":
            return R.drawable.ic_sport_squash_large;
        case "TEN":
            return R.drawable.ic_sport_tennis_large;
        case "VOL":
            return R.drawable.ic_sport_voleyball_large;
        default:
            return R.drawable.ic_sport_others_large;
            //return R.drawable.ic_sport_darts_large;
        }
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        if (super.getActivity() == null) {
            return;
        }

        try {
            UserProfile u = Persistance.getInstance().getProfileInfo(activity);

            u.email = jsonObject.getString("email");
            u.firstName = jsonObject.getString("firstName");
            u.lastName = jsonObject.getString("lastName");
            u.gender = jsonObject.getString("gender");
            u.ludicoins = Integer.parseInt(jsonObject.getString("ludicoins"));
            u.countEventsAttended = Integer.parseInt(jsonObject.getString("countEventsAttended"));
            u.level = Integer.parseInt(jsonObject.getString("level"));
            u.points = Integer.parseInt(jsonObject.getString("points"));
            u.pointsToNextLevel = Integer.parseInt(jsonObject.getString("pointsToNextLevel"));
            u.pointsOfNextLevel = Integer.parseInt(jsonObject.getString("pointsOfNextLevel"));
            u.position = Integer.parseInt(jsonObject.getString("position"));
            u.range = jsonObject.getString("range");
            u.age = Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt(jsonObject.getString("yearBorn"));
            u.profileImage = jsonObject.getString("profileImage");
            u.facebookId = Persistance.getInstance().getUserInfo(super.getActivity()).facebookId;

            JSONArray sports = jsonObject.getJSONArray("sports");
            u.sports.clear();
            for (int i = 0; i < sports.length(); ++i) {
                u.sports.add(new Sport(sports.getString(i)));
            }

            JSONObject stat = jsonObject.getJSONObject("statistics");
            u.eventsM.clear();
            u.pointsM.clear();
            Iterator<String> months = stat.keys();
            while (months.hasNext()) {
                String m = months.next();
                int e = Integer.parseInt(stat.getJSONObject(m).getString("events"));
                int p = Integer.parseInt(stat.getJSONObject(m).getString("points"));
                u.eventsM.put(m, e);
                u.pointsM.put(m, p);
            }

            JSONArray lastEvents = jsonObject.getJSONArray("lastEvents");
            u.lastEvents.clear();
            for (int i = 0; i < lastEvents.length(); ++i) {
                u.lastEvents.add(new EventBrief(lastEvents.getJSONObject(i)));
            }

            Persistance.getInstance().setProfileInfo(super.getActivity(), u);

            User t = Persistance.getInstance().getUserInfo(activity);
            t.age = Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt(jsonObject.getString("yearBorn"));
            Persistance.getInstance().setUserInfo(activity, t);

            MyProfileActivity mpa = (MyProfileActivity) this;
            Persistance.getInstance().setProfileInfo(activity, u);
            mpa.printInfo(u);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        //Toast.makeText(super.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
        if (error instanceof NetworkError) {
            v.findViewById(R.id.internetRefresh).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setOnClickListener(null);
                    onInternetRefresh();
                }
            });
            RelativeLayout ll = (RelativeLayout) v.findViewById(R.id.noInternetLayout);
            final float scale = getContext().getResources().getDisplayMetrics().density;
            int pixels = (int) (56 * scale + 0.5f);
            ll.getLayoutParams().height = pixels;
            ll.setLayoutParams(ll.getLayoutParams());
            v.findViewById(R.id.profileProgressBar).setAlpha(0);
        }
    }

    public void onInternetRefresh() {
        v.findViewById(R.id.internetRefresh).setOnClickListener(null);
        requestInfo();
    }

    private final ArrayList<Iterable> animations = new ArrayList<>();
    private float runningTime;
    private Thread animation;
    @Override
    public void run() {
        runningTime = 2;
        final View graphs = v.findViewById(R.id.profileGraphs);
        this.animations.clear();

        {
            LinearLayout barsLayout = (LinearLayout) graphs.findViewById(R.id.barGraph);
            int size = barsLayout.getChildCount();
            Bar bar;
            for (int i = 0; i < size; ++i) {
                RelativeLayout rl = (RelativeLayout) barsLayout.getChildAt(i);
                bar = (Bar) rl.getChildAt(0);
                this.animations.add(bar);
            }
        }
        {
            TopGraph tg = (TopGraph) graphs.findViewById(R.id.topGraph);
            this.animations.add(tg);
        }

        try {
            ScrollView scrollView = (ScrollView) v.findViewById(R.id.profileContent);
            Rect scrollBounds = new Rect();
            while (true) {
                scrollView.getHitRect(scrollBounds);
                if (graphs.getLocalVisibleRect(scrollBounds)) {
                    break;
                }
                Thread.sleep(500);
            }
        } catch (InterruptedException ex) {
        }

        Runnable inv = new Runnable() {
            @Override
            public void run() {
                graphs.invalidate();
            }
        };
        long last = System.currentTimeMillis();
        long now;
        float tpf;
        Activity ac;
        try {
            while (this.runningTime > 0) {
                Thread.sleep(10);
                now = System.currentTimeMillis();
                tpf = (float) (now - last) / 1000f;
                last = now;
                for (Iterable i : this.animations) {
                    i.iterate(tpf);
                }
                ac = super.getActivity();
                if (ac == null) {
                    this.runningTime = 0;
                    return;
                }
                ac.runOnUiThread(inv);
                this.runningTime -= tpf;
            }
        } catch (InterruptedException ex) {
        }

        this.runningTime = 0;
    }
}
