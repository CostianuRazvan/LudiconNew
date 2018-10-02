package larc.ludiconprod.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.Dialogs.ConfirmationDialog;
import larc.ludiconprod.R;
import larc.ludiconprod.User;
import larc.ludiconprod.UserProfile;
import larc.ludiconprod.Utils.util.ReviewBrief;
import larc.ludiconprod.Utils.util.Sport;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;


public class UserProfileActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {

    private User user = new User();
    private final HashMap<String, Integer> youPoints = new HashMap<>();
    private final HashMap<String, Integer> foePoints = new HashMap<>();
    private String userName;
    private String userImage;
    public String firstName;
    RelativeLayout socialReviews;
    LinearLayout reviewLayout;
    LinearLayout buttonLayout;
    Button allReviews;
    LinearLayout rateLayout;
    Button addFriend;
    Button chat;
    ImageView blockingIV;
    RelativeLayout blocking;
    public String ChatId;
    public int isBlocked;

    ArrayList<String> userReviewName = new ArrayList<>();
    ArrayList<String> userReviewDate = new ArrayList<>();
    ArrayList<String> userReview = new ArrayList<>();
    ArrayList<Double> userSocialRate = new ArrayList<>();

    int layout_id_start = 789;

    @SuppressLint("ResourceType")
    public void addReview(){

        final Typeface typeFace = Typeface.createFromAsset(super.getAssets(), "fonts/Quicksand-Medium.ttf");

        for (int i = 0; i < userReview.size(); i++){

            RelativeLayout layout = new RelativeLayout(this);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layout.setLayoutParams(layoutParams);
            layout.setId(layout_id_start + i);

            if (i != 0) {
                layoutParams.addRule(RelativeLayout.BELOW, layout.getId() - 1);
            }

            RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(500, ViewGroup.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            TextView name = new TextView(this);
            name.setId(1);
            name.setText(userReviewName.get(i).toString());
            name.setTextSize(16);
            name.setTextColor(Color.parseColor("#d4498b"));
            name.setPadding(30,20,0,0);
            name.setTypeface(typeFace);

            TextView review = new TextView(this);
            params2.addRule(RelativeLayout.BELOW, name.getId());
            review.setId(2);
            review.setText("\"" + userReview.get(i).toString() + "\"");
            review.setTextColor(Color.parseColor("#0c3855"));
            review.setTypeface(typeFace);
            review.setTextSize(13);
            review.setPadding(60,5,0,0);

            TextView date = new TextView(this);
            params3.addRule(RelativeLayout.RIGHT_OF, name.getId());
            date.setId(3);
            date.setText(userReviewDate.get(i).toString());
            date.setTextColor(Color.parseColor("#acb8c1"));
            date.setTextSize(14);
            date.setPadding(0,25,30,0);
            date.setGravity(Gravity.RIGHT);
            date.setTypeface(typeFace);

            RelativeLayout stars = new RelativeLayout(this);
            params4.addRule(RelativeLayout.RIGHT_OF, review.getId());
            params4.addRule(RelativeLayout.BELOW, date.getId());
            stars.setId(4);
            stars.setPadding(0,15,30,0);
            stars.setGravity(Gravity.RIGHT);

            RelativeLayout.LayoutParams paramsStar1 = new RelativeLayout.LayoutParams(60, 60);
            RelativeLayout.LayoutParams paramsStar2 = new RelativeLayout.LayoutParams(60, 60);
            RelativeLayout.LayoutParams paramsStar3 = new RelativeLayout.LayoutParams(60, 60);
            RelativeLayout.LayoutParams paramsStar4 = new RelativeLayout.LayoutParams(60, 60);
            RelativeLayout.LayoutParams paramsStar5 = new RelativeLayout.LayoutParams(60, 60);

            final ImageView star1 = new ImageView(this);
            star1.setId(5);
            star1.setImageResource(R.drawable.icon_star_full);
            star1.setPadding(0,0,0,0);
            final ImageView star2 = new ImageView(this);
            star2.setId(6);
            star2.setImageResource(R.drawable.icon_star_full);
            star2.setPadding(5,0,0,0);
            paramsStar2.addRule(RelativeLayout.RIGHT_OF, star1.getId());
            final ImageView star3 = new ImageView(this);
            star3.setId(7);
            star3.setImageResource(R.drawable.icon_star_full);
            star3.setPadding(5,0,0,0);
            paramsStar3.addRule(RelativeLayout.RIGHT_OF, star2.getId());
            final ImageView star4 = new ImageView(this);
            star4.setId(8);
            star4.setImageResource(R.drawable.icon_star_full);
            star4.setPadding(5,0,0,0);
            paramsStar4.addRule(RelativeLayout.RIGHT_OF, star3.getId());
            final ImageView star5 = new ImageView(this);
            star5.setId(9);
            star5.setImageResource(R.drawable.icon_star_full);
            star5.setPadding(5,0,0,0);
            paramsStar5.addRule(RelativeLayout.RIGHT_OF, star4.getId());

            if (userSocialRate.get(i) == 1){
                star1.setImageResource(R.drawable.icon_star_full);
                star2.setImageResource(R.drawable.icon_star_line);
                star3.setImageResource(R.drawable.icon_star_line);
                star4.setImageResource(R.drawable.icon_star_line);
                star5.setImageResource(R.drawable.icon_star_line);
            }else if (userSocialRate.get(i) > 1 && userSocialRate.get(i) <= 1.5){
                star1.setImageResource(R.drawable.icon_star_full);
                star2.setImageResource(R.drawable.icon_star_half);
                star3.setImageResource(R.drawable.icon_star_line);
                star4.setImageResource(R.drawable.icon_star_line);
                star5.setImageResource(R.drawable.icon_star_line);
            }else if (userSocialRate.get(i) >1.5 && userSocialRate.get(i) <= 2){
                star1.setImageResource(R.drawable.icon_star_full);
                star2.setImageResource(R.drawable.icon_star_full);
                star3.setImageResource(R.drawable.icon_star_line);
                star4.setImageResource(R.drawable.icon_star_line);
                star5.setImageResource(R.drawable.icon_star_line);
            }else if (userSocialRate.get(i) > 2 && userSocialRate.get(i) <= 2.5){
                star1.setImageResource(R.drawable.icon_star_full);
                star2.setImageResource(R.drawable.icon_star_full);
                star3.setImageResource(R.drawable.icon_star_half);
                star4.setImageResource(R.drawable.icon_star_line);
                star5.setImageResource(R.drawable.icon_star_line);
            }else if (userSocialRate.get(i) > 2.5 && userSocialRate.get(i) <= 3){
                star1.setImageResource(R.drawable.icon_star_full);
                star2.setImageResource(R.drawable.icon_star_full);
                star3.setImageResource(R.drawable.icon_star_full);
                star4.setImageResource(R.drawable.icon_star_line);
                star5.setImageResource(R.drawable.icon_star_line);
            }else if (userSocialRate.get(i) > 3 && userSocialRate.get(i) <= 3.5){
                star1.setImageResource(R.drawable.icon_star_full);
                star2.setImageResource(R.drawable.icon_star_full);
                star3.setImageResource(R.drawable.icon_star_full);
                star4.setImageResource(R.drawable.icon_star_half);
                star5.setImageResource(R.drawable.icon_star_line);
            }else if (userSocialRate.get(i) > 3.5 && userSocialRate.get(i) <= 4){
                star1.setImageResource(R.drawable.icon_star_full);
                star2.setImageResource(R.drawable.icon_star_full);
                star3.setImageResource(R.drawable.icon_star_full);
                star4.setImageResource(R.drawable.icon_star_full);
                star5.setImageResource(R.drawable.icon_star_line);
            }else if (userSocialRate.get(i) > 4 && userSocialRate.get(i) <= 4.5){
                star1.setImageResource(R.drawable.icon_star_full);
                star2.setImageResource(R.drawable.icon_star_full);
                star3.setImageResource(R.drawable.icon_star_full);
                star4.setImageResource(R.drawable.icon_star_full);
                star5.setImageResource(R.drawable.icon_star_half);
            }else if (userSocialRate.get(i) >4.5 && userSocialRate.get(i) <= 5){
                star1.setImageResource(R.drawable.icon_star_full);
                star2.setImageResource(R.drawable.icon_star_full);
                star3.setImageResource(R.drawable.icon_star_full);
                star4.setImageResource(R.drawable.icon_star_full);
                star5.setImageResource(R.drawable.icon_star_full);
            }

            stars.addView(star1,paramsStar1);
            stars.addView(star2,paramsStar2);
            stars.addView(star3,paramsStar3);
            stars.addView(star4,paramsStar4);
            stars.addView(star5,paramsStar5);


            layout.addView(name, params1);
            layout.addView(review, params2);
            layout.addView(date, params3);
            layout.addView(stars, params4);

            socialReviews.addView(layout);

        }
    }

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.hide();

            super.setContentView(R.layout.user_profile_activity);

            View backButton = findViewById(R.id.backButton);
            TextView titleText = (TextView) findViewById(R.id.titleText);
            titleText.setText(R.string.player_profile);

            final Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Medium.ttf");
            titleText.setTypeface(typeFace);

            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

             addFriend = (Button) super.findViewById(R.id.profileFriend);
             chat = (Button) super.findViewById(R.id.profileChat);
             blockingIV = (ImageView) this.findViewById(R.id.blockingIV);
             blocking = (RelativeLayout) this.findViewById(R.id.blocking);

            chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(UserProfileActivity.this, ChatActivity.class);
                    intent.putExtra("otherParticipantName", userName);
                    ArrayList<String> myList = new ArrayList<String>();
                    myList.add(userImage);
                    intent.putExtra("otherParticipantImage", myList);
                    intent.putExtra("chatId", "isNot");
                    intent.putExtra("groupChat", 0);
                    intent.putExtra("UserId", getIntent().getStringExtra("UserId"));
                    ArrayList<String> userIdList = new ArrayList<String>();
                    userIdList.add(getIntent().getStringExtra("UserId"));
                    intent.putExtra("otherParticipantId", userIdList);
                    UserProfileActivity.this.startActivity(intent);
                    //finish();
                }
            });


            Typeface typeFaceBold = Typeface.createFromAsset(super.getAssets(), "fonts/Quicksand-Bold.ttf");

            titleText.setTypeface(typeFace);
            ((TextView) findViewById(R.id.profileName)).setTypeface(typeFace);
            ((Button) findViewById(R.id.profileChat)).setTypeface(typeFaceBold);
            ((Button) findViewById(R.id.profileFriend)).setTypeface(typeFaceBold);
            ((TextView) findViewById(R.id.profileLevel)).setTypeface(typeFace);
            ((TextView) findViewById(R.id.profileLevelText)).setTypeface(typeFace);
            ((TextView) findViewById(R.id.profilePoints)).setTypeface(typeFace);
            ((TextView) findViewById(R.id.profilePointsText)).setTypeface(typeFace);
            ((TextView) findViewById(R.id.profilePosition)).setTypeface(typeFace);
            ((TextView) findViewById(R.id.profilePositionText)).setTypeface(typeFace);

            ((TextView) findViewById(R.id.profilePracticeSportsLabel)).setTypeface(typeFaceBold);
            ((TextView) findViewById(R.id.profilePracticeSportsCountLabel)).setTypeface(typeFace);

            ((TextView) findViewById(R.id.profileBadgesText)).setTypeface(typeFaceBold);
            ((TextView) findViewById(R.id.profileBadgesCountLabel)).setTypeface(typeFace);

            ((TextView) findViewById(R.id.versusLabel)).setTypeface(typeFaceBold);
            ((TextView) findViewById(R.id.youLabel)).setTypeface(typeFace);
            ((TextView) findViewById(R.id.foeLabel)).setTypeface(typeFace);
            ((TextView) findViewById(R.id.profileYouPoints)).setTypeface(typeFace);
            ((TextView) findViewById(R.id.profileFoePoints)).setTypeface(typeFace);

            ((TextView) findViewById(R.id.vsLabel)).setTypeface(typeFaceBold);
            ((TextView) findViewById(R.id.socialRateUser)).setTypeface(typeFace);
            ((TextView) findViewById(R.id.countSocialRateUser)).setTypeface(typeFace);
            socialReviews = (RelativeLayout) findViewById(R.id.socialReviews);
            buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);
            reviewLayout = (LinearLayout) findViewById(R.id.reviewLayout);
            allReviews = (Button) findViewById(R.id.allReviews);
            rateLayout = (LinearLayout) findViewById(R.id.rateLayout);

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
        HTTPResponseController.getInstance().getUserProfile(params, headers, id, this, this, this);
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        try {
            User u = this.user;

            u.email = jsonObject.getString("email");
            u.firstName = jsonObject.getString("firstName");
            firstName = u.firstName;
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
            u.profileImage = jsonObject.getString("profileImage");

            JSONArray sports = jsonObject.getJSONArray("sports");
            u.sports.clear();
            for (int i = 0; i < sports.length(); ++i) {
                u.sports.add(new Sport(sports.getString(i)));
            }

            JSONArray socialReviews = jsonObject.getJSONArray("socialReviews");
            u.socialReviews.clear();
            for (int i = 0; i < socialReviews.length(); ++i) {
                u.socialReviews.add(new ReviewBrief(socialReviews.getJSONObject(i)));
            }

            u.socialRate = jsonObject.getString("socialRate");
            u.countSocialRate = Integer.parseInt(jsonObject.getString("countSocialRate"));

            boolean friend = jsonObject.getBoolean("isFriend");

            this.youPoints.clear();
            this.foePoints.clear();
            try {
                JSONObject headtohead = jsonObject.getJSONObject("headtohead");
                JSONArray names = headtohead.names();
                for (int i = 0; i < names.length(); ++i) {
                    String spn = names.getString(i);
                    this.youPoints.put(spn, Integer.parseInt(headtohead.getJSONObject(spn).getString("user")));
                    this.foePoints.put(spn, Integer.parseInt(headtohead.getJSONObject(spn).getString("versus")));
                }
            } catch (JSONException e) {
            }

            this.printInfo(friend);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void printInfo(boolean friend) {
        try {
            RelativeLayout ll = (RelativeLayout) findViewById(R.id.noInternetLayout);
            ll.getLayoutParams().height = 0;
            ll.setLayoutParams(ll.getLayoutParams());

            final User u = this.user;

            TextView sportsCount = (TextView) findViewById(R.id.profilePracticeSportsCountLabel);
            ImageView image = (ImageView) findViewById(R.id.profileImage);
            userImage = u.profileImage;
            if (u.profileImage != null && !u.profileImage.isEmpty()) {
                Bitmap im = IntroActivity.decodeBase64(u.profileImage);
                image.setImageBitmap(im);
            }

            TextView name = (TextView) findViewById(R.id.profileName);
            TextView level = (TextView) findViewById(R.id.profileLevel);
            TextView points = (TextView) findViewById(R.id.profilePoints);
            TextView position = (TextView) findViewById(R.id.profilePosition);
            TextView socialRate = (TextView) findViewById(R.id.socialRateUser);
            TextView countSocialRate = (TextView) findViewById(R.id.countSocialRateUser);

            name.setText(u.firstName + " " + u.lastName);
            userName = u.firstName + " " + u.lastName + ",";
            level.setText("" + u.level);
            points.setText("" + u.points);
            position.setText("" + u.position);
            if (u.socialRate.equalsIgnoreCase("null")){
                rateLayout.setVisibility(View.GONE);
            }else{
                rateLayout.setVisibility(View.VISIBLE);
                DecimalFormat df = new DecimalFormat("#.00");
                socialRate.setText(df.format(Double.valueOf(u.socialRate)));
            }
            countSocialRate.setText(getResources().getString(R.string.based_on) + " " + u.countSocialRate + " " + getResources().getString(R.string.reviews));

            final ArrayList<String> sportCodes = new ArrayList<>();
            for (Sport s : u.sports) {
                sportCodes.add(s.code);
            }

            LinearLayout sportsLayout = (LinearLayout) findViewById(R.id.profileSports);
            ImageView sportImage;
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
                friendButton.setText(R.string.unfollow);
                friendButton.setOnClickListener(new View.OnClickListener() {
                    final Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Medium.ttf");
                    final Typeface typeFaceBold = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.ttf");

                    @Override
                    public void onClick(View view) {
                        final ConfirmationDialog confirmationDialog = new ConfirmationDialog(UserProfileActivity.this);
                        confirmationDialog.show();
                        confirmationDialog.title.setText(R.string.confirm);
                        confirmationDialog.title.setTypeface(typeFaceBold);
                        confirmationDialog.message.setText(getResources().getString(R.string.are_you_sure_you_want_to_unfollow) + " " + firstName + "?");
                        confirmationDialog.message.setTypeface(typeFace);
                        confirmationDialog.confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                removeFriend();
                                confirmationDialog.dismiss();
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

            ImageView youImg = (ImageView) findViewById(R.id.profileYouImage);
            ImageView versusImg = (ImageView) findViewById(R.id.profileFoeImage);

            TextView foeLabel = (TextView) findViewById(R.id.foeLabel);
            String firstName = u.firstName;
            String[] splited = firstName.split("\\s+");
            foeLabel.setText(splited[0]);
            TextView youLabel = (TextView) findViewById(R.id.youLabel);
            User you = Persistance.getInstance().getUserInfo(this);
            String firstNameYou = you.firstName;
            String[] splitedYou = firstNameYou.split("\\s+");
            youLabel.setText(splitedYou[0]);


            if (you.profileImage != null && !you.profileImage.isEmpty()) {
                Bitmap im = IntroActivity.decodeBase64(you.profileImage);
                youImg.setImageBitmap(im);
            }
            if (u.profileImage != null && !u.profileImage.isEmpty()) {
                Bitmap im = IntroActivity.decodeBase64(u.profileImage);
                versusImg.setImageBitmap(im);
            }

            TextView youPoints = (TextView) findViewById(R.id.profileYouPoints);
            TextView foePoints = (TextView) findViewById(R.id.profileFoePoints);
            Integer yp = this.youPoints.get(Sport.GENERAL);
            Integer fp = this.foePoints.get(Sport.GENERAL);
            if (yp == null) {
                yp = 0;
            }
            if (fp == null) {
                fp = 0;
            }
            youPoints.setText("" + yp);
            foePoints.setText("" + fp);
            this.youPoints.remove(Sport.GENERAL);
            this.foePoints.remove(Sport.GENERAL);

            LinearLayout versusLayout = (LinearLayout) findViewById(R.id.profileVresus);
            versusLayout.removeAllViews();

            sportCodes.clear();
            sportCodes.addAll(this.youPoints.keySet());
            for (int i = 0; i < sportCodes.size(); ++i) {
                String sc = sportCodes.get(i);


                yp = this.youPoints.get(sc);
                fp = this.foePoints.get(sc);
                int tot = yp + fp;

                LayoutInflater.from(this).inflate(R.layout.versus_card, versusLayout);
                View c = versusLayout.getChildAt(i);

                sportImage = (ImageView) c.findViewById(R.id.image);
                TextView t = (TextView) c.findViewById(R.id.youText);

                t.setText("" + yp);
                t = (TextView) c.findViewById(R.id.foeText);
                t.setText("" + fp);

                if (tot > 0) {
                    ProgressBar p = (ProgressBar) c.findViewById(R.id.you);
                    p.setProgress(yp * 100 / tot);
                    p = (ProgressBar) c.findViewById(R.id.foe);
                    p.setProgress(fp * 100 / tot);
                } else {
                    ProgressBar p = (ProgressBar) c.findViewById(R.id.you);
                    p.setProgress(0);
                    p = (ProgressBar) c.findViewById(R.id.foe);
                    p.setProgress(0);
                }

                sportImage.setImageResource(MyProfileActivity.findSportImageResource(sc));
            }

            userReviewName.clear();
            userReviewDate.clear();
            userReview.clear();
            userSocialRate.clear();
            for (ReviewBrief rb : u.socialReviews){
                userReviewName.add(rb.userName);
                userReviewDate.add(rb.date);
                userReview.add(rb.reviewPreview);
                userSocialRate.add(Double.valueOf(rb.socialRate));
            }

            addReview();

            if (userReview.size() == 0){
                reviewLayout.setVisibility(View.GONE);
                buttonLayout.setVisibility(View.GONE);
            }

            allReviews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UserProfileActivity.this, FullPageView.class);
                    intent.putExtra("userId", user.id);
                    startActivity(intent);
                }
            });

            View tv = findViewById(R.id.profileContent);
            tv.setAlpha(1);
            tv = findViewById(R.id.profileProgressBar);
            tv.setAlpha(0);

            DatabaseReference myNode = FirebaseDatabase.getInstance().getReference().child("users").child(Persistance.getInstance().getUserInfo(UserProfileActivity.this).id).child("talkbuddies");
            myNode.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot myChatParticipants) {
                    String chatId;
                    if(myChatParticipants.hasChild(user.id)) {
                        chatId = myChatParticipants.child(user.id).getValue().toString();
                        ChatId = chatId;

                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("users").child(Persistance.getInstance().getUserInfo(UserProfileActivity.this).id).child("chats").child(ChatId);
                        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if (snapshot.hasChild("blk")) {
                                    if (snapshot.child("blk").getValue().toString().equals("1")) {
                                        isBlocked = 1;
                                    }else{
                                        isBlocked = 0;
                                    }
                                } else {
                                    isBlocked = 0;
                                }

                                ViewGroup.LayoutParams params = blocking.getLayoutParams();
                                params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, getResources().getDisplayMetrics());
                                params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, getResources().getDisplayMetrics());
                                blocking.setLayoutParams(params);

                                if (isBlocked == 0) {
                                    chat.setVisibility(View.VISIBLE);
                                    addFriend.setVisibility(View.VISIBLE);

                                    blockingIV.setImageResource(R.drawable.ic_unblock);

                                }
                                if (isBlocked == 1) {
                                    chat.setVisibility(View.GONE);
                                    addFriend.setVisibility(View.GONE);
                                    blockingIV.setImageResource(R.drawable.ic_block);
                                }

                                blockingIV.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(UserProfileActivity.this, BlockUserPopup.class);
                                        intent.putExtra("userId", user.id);
                                        intent.putExtra("isBlocked", isBlocked);
                                        intent.putExtra("isUserBlock", "true");
                                        startActivity(intent);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }else{
                        ViewGroup.LayoutParams params = blocking.getLayoutParams();
                        params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, getResources().getDisplayMetrics());
                        params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, getResources().getDisplayMetrics());
                        blocking.setLayoutParams(params);
                        blocking.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(UserProfileActivity.this, BlockUserPopup.class);
                                intent.putExtra("userId", user.id);
                                intent.putExtra("isBlocked", isBlocked);
                                intent.putExtra("otherParticipantName", userName);
                                intent.putExtra("otherParticipantImage", userImage);
                                intent.putExtra("isUserBlock", "true");
                                startActivity(intent);
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onFriendResponse(JSONObject response, boolean action) {
        String name = this.user.firstName + " " + this.user.lastName;
        if (action) {
            Toast.makeText(this, getResources().getString(R.string.you_are_now_following) + " " + name + "!", Toast.LENGTH_SHORT).show();
            this.friendAdded();
        } else {
            Toast.makeText(this, getResources().getString(R.string.you_are_no_following) + " " + name + "!", Toast.LENGTH_SHORT).show();
            this.friendRemoved();
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
        HTTPResponseController.getInstance().friendRequest(params, headers, this, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                onFriendResponse(response, true);
            }
        }, this);
    }

    public void removeFriend() {
        User u = Persistance.getInstance().getUserInfo(this);
        HashMap<String, String> params = new HashMap<>();
        params.put("userId", u.id);
        params.put("userToRequestId", this.user.id);
        params.put("action", "0");
        HashMap<String, String> headers = new HashMap<>();
        headers.put("authKey", u.authKey);
        HTTPResponseController.getInstance().friendRequest(params, headers, this, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                onFriendResponse(response, false);
            }
        }, this);
    }


    public void friendAdded() {
        final Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Medium.ttf");
        final Typeface typeFaceBold = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.ttf");
        Button friendButton = (Button) super.findViewById(R.id.profileFriend);
        friendButton.setText(R.string.unfollow);
        friendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final ConfirmationDialog confirmationDialog = new ConfirmationDialog(UserProfileActivity.this);
                confirmationDialog.show();
                confirmationDialog.title.setText(R.string.confirm);
                confirmationDialog.title.setTypeface(typeFaceBold);
                confirmationDialog.message.setText((getResources().getString(R.string.are_you_sure_you_want_to_unfollow)) + " " + firstName + "?");
                confirmationDialog.message.setTypeface(typeFace);
                confirmationDialog.confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removeFriend();
                        confirmationDialog.dismiss();
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
    }

    public void friendRemoved() {
        Button friendButton = (Button) super.findViewById(R.id.profileFriend);
        friendButton.setText(R.string.follow);
        friendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFriend();
            }
        });
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        //Toast.makeText(super.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
        if (error instanceof NetworkError) {
            findViewById(R.id.internetRefresh).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setOnClickListener(null);
                    onInternetRefresh();
                }
            });
            RelativeLayout ll = (RelativeLayout) findViewById(R.id.noInternetLayout);
            final float scale = super.getResources().getDisplayMetrics().density;
            int pixels = (int) (56 * scale + 0.5f);
            ll.getLayoutParams().height = pixels;
            ll.setLayoutParams(ll.getLayoutParams());
            findViewById(R.id.profileProgressBar).setAlpha(0);
        }
    }

    public void onInternetRefresh() {
        findViewById(R.id.internetRefresh).setOnClickListener(null);
        requestInfo();
    }
}
