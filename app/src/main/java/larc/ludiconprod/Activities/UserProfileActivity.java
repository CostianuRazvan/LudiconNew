package larc.ludiconprod.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.User;
import larc.ludiconprod.Utils.MyProfileUtils.EditViewPagerAdapter;
import larc.ludiconprod.Utils.ui.SlidingTabLayout;
import larc.ludiconprod.Utils.util.Sport;

public class UserProfileActivity extends AppCompatActivity implements Response.Listener<JSONObject> {

    private User user = new User();

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.hide();

            super.setContentView(R.layout.user_profile_activity);

            ImageButton backButton=(ImageButton) findViewById(R.id.backButton);
            backButton.setBackgroundResource(R.drawable.ic_nav_up);
            TextView titleText=(TextView) findViewById(R.id.titleText);
            titleText.setText("Player profile");

            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        this.requestInfo();
    }

    private void requestInfo() {
        View tv = findViewById(R.id.profileContent);
        tv.setAlpha(0);
        tv = findViewById(R.id.profileProgressBar);
        tv.setAlpha(1);

        User u = Persistance.getInstance().getUserInfo(this);
        String id = super.getIntent().getStringExtra("UserId");
        this.user.id = id;

        HashMap<String, String> params = new HashMap<>();
        HashMap<String, String> headers = new HashMap<>();
        headers.put("authKey", u.authKey);
        HTTPResponseController.getInstance().getUserProfile(params, headers, id, this, this);
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        try {
            User u = this.user;

            u.email = jsonObject.getString("email");
            u.firstName = jsonObject.getString("firstName");
            u.lastName = jsonObject.getString("lastName");
            u.gender = jsonObject.getString("gender");
            u.ludicoins = Integer.parseInt(jsonObject.getString("ludicoins"));
            u.level = Integer.parseInt(jsonObject.getString("level"));
            u.points = Integer.parseInt(jsonObject.getString("points"));
            u.pointsToNextLevel = Integer.parseInt(jsonObject.getString("pointsToNextLevel"));
            u.pointsOfNextLevel = Integer.parseInt(jsonObject.getString("pointsOfNextLevel"));
            u.position = Integer.parseInt(jsonObject.getString("position"));
            u.range = jsonObject.getString("range");
            u.profileImage = jsonObject.getString("profileImage");


            JSONArray sports = jsonObject.getJSONArray("sports");
            u.sports.clear();
            for (int i = 0; i < sports.length(); ++i) {
                u.sports.add(new Sport(sports.getString(i)));
            }

            boolean friend = true;
            try {
                jsonObject.getBoolean("isFriend");
            } catch (JSONException e) {
                friend = false;
            }

            this.printInfo(friend);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void printInfo(boolean friend) {
        try {
            User u = this.user;
            /*TextView toNextLevel = (TextView) findViewById(R.id.profileToNextLevel);
            toNextLevel.setText("" + u.pointsToNextLevel);*/
            TextView sportsCount = (TextView) findViewById(R.id.profilePracticeSportsCountLabel);
            ImageView image = (ImageView) findViewById(R.id.profileImage);
            if (u.profileImage != null && !u.profileImage.isEmpty()) {
                Bitmap im = IntroActivity.decodeBase64(u.profileImage);
                image.setImageBitmap(im);
            }

            TextView name = (TextView) findViewById(R.id.profileName);
            TextView level = (TextView) findViewById(R.id.profileLevel);
            TextView points = (TextView) findViewById(R.id.profilePoints);
            TextView position = (TextView) findViewById(R.id.profilePosition);

            name.setText(u.firstName + " " + u.lastName);
            level.setText("" + u.level);
            points.setText("" + u.points);
            position.setText("" + u.position);

            /*ProgressBar levelBar = (ProgressBar) findViewById(R.id.profileLevelBar);
            levelBar.setProgress(u.points * levelBar.getMax() / u.pointsOfNextLevel );*/

            final ArrayList<String> sportCodes = new ArrayList<>();
            for (Sport s : u.sports) {
                sportCodes.add(s.code);
            }

            LinearLayout sportsLayout = (LinearLayout) findViewById(R.id.profileSports);
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

            Button friendButton = (Button) super.findViewById(R.id.profileFriend);
            if (friend) {
                friendButton.setText("REMOVE FRIEND");
                friendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removeFriend();
                    }
                });
            } else {
                friendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addFriend();
                    }
                });
            }

            sportsCount.setText(sportCodes.size() + "/" + allSportCodes.size());
            Resources r = this.getResources();
            int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, r.getDisplayMetrics());

            sportsLayout.removeAllViews();
            for (int i = 0; i < allSportCodes.size(); ++i) {
                String sc = allSportCodes.get(i);
                sportImage = new ImageView(this);

                sportImage.setImageResource(MyProfileActivity.findSportImageResource(sc));
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

            View tv = findViewById(R.id.profileContent);
            tv.setAlpha(1);
            tv = findViewById(R.id.profileProgressBar);
            tv.setAlpha(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addFriend() {
        User u = Persistance.getInstance().getUserInfo(this);
        HashMap<String, String> params = new HashMap<>();
        params.put("userId", u.id);
        params.put("userToRequestId", this.user.id);
        params.put("action", "1");
        HashMap<String, String> headers = new HashMap<>();
        headers.put("authKey", u.authKey);
        HTTPResponseController.getInstance().friendRequest(params, headers, this);
    }

    public void removeFriend() {
        User u = Persistance.getInstance().getUserInfo(this);
        HashMap<String, String> params = new HashMap<>();
        params.put("userId", u.id);
        params.put("userToRequestId", this.user.id);
        params.put("action", "0");
        HashMap<String, String> headers = new HashMap<>();
        headers.put("authKey", u.authKey);
        HTTPResponseController.getInstance().friendRequest(params, headers, this);
    }

    public void friendAdded() {
        Button friendButton = (Button) super.findViewById(R.id.profileFriend);
        friendButton.setText("REMOVE FRIEND");
        friendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFriend();
            }
        });
    }

    public void friendRemoved() {
        Button friendButton = (Button) super.findViewById(R.id.profileFriend);
        friendButton.setText("ADD FRIEND");
        friendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFriend();
            }
        });
    }
}