package larc.ludiconprod.Controller;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.content.IntentCompat;
import android.util.Base64;
import android.util.JsonReader;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import larc.ludiconprod.Activities.ActivitiesActivity;
import larc.ludiconprod.Activities.ActivityDetailsActivity;
import larc.ludiconprod.Activities.BalanceActivity;
import larc.ludiconprod.Activities.ChatActivity;
import larc.ludiconprod.Activities.ChatAndFriendsActivity;
import larc.ludiconprod.Activities.CouponsActivity;
import larc.ludiconprod.Activities.CreateNewActivity;
import larc.ludiconprod.Activities.FullHistory;
import larc.ludiconprod.Activities.GMapsActivity;
import larc.ludiconprod.Activities.IntroActivity;
import larc.ludiconprod.Activities.InviteFriendsActivity;
import larc.ludiconprod.Activities.LoginActivity;
import larc.ludiconprod.Activities.Main;
import larc.ludiconprod.Activities.PopDownloadEnrollmentData;
import larc.ludiconprod.Activities.ProfileDetailsActivity;
import larc.ludiconprod.Activities.RegisterActivity;
import larc.ludiconprod.Activities.ResetPasswordFinalActivity;
import larc.ludiconprod.Dialogs.PointsReceivedDialog;
import larc.ludiconprod.R;
import larc.ludiconprod.User;
import larc.ludiconprod.Utils.Event;
import larc.ludiconprod.Utils.EventBrief;
import larc.ludiconprod.Utils.EventDetails;
import larc.ludiconprod.Utils.Friend;
import larc.ludiconprod.Utils.HappeningNowLocation;
import larc.ludiconprod.Utils.util.AuthorizedLocation;
import larc.ludiconprod.Utils.util.Sponsors;
import larc.ludiconprod.Utils.util.Sport;

import static larc.ludiconprod.Activities.ActivitiesActivity.aroundMeEventList;
import static larc.ludiconprod.Activities.ActivitiesActivity.deleteCachedInfo;
import static larc.ludiconprod.Activities.ActivitiesActivity.fradapter;
import static larc.ludiconprod.Activities.ActivitiesActivity.pageNumberAroundMe;
import static larc.ludiconprod.Activities.ActivitiesActivity.getFirstPageMyActivity;
import static larc.ludiconprod.Activities.ActivitiesActivity.myAdapter;
import static larc.ludiconprod.Activities.ActivitiesActivity.myEventList;
import static larc.ludiconprod.Activities.ActivitiesActivity.sponsorsList;
import static larc.ludiconprod.Activities.ActivitiesActivity.startedEventDate;
import static larc.ludiconprod.Activities.FullHistory.getFirstPagePastEvents;
import static larc.ludiconprod.Activities.FullHistory.pageNumberPastEvents;
import static larc.ludiconprod.Activities.FullHistory.pastEventList;

/**
 * Created by ancuta on 7/12/2017.
 */

public class HTTPResponseController {

    String prodServer = "https://ludicon.app/";
    public static final String firebaseRefference = "https://ludicon-chat-cf900.firebaseio.com/";
    public static final String API_KEY = "b0a83e90-4ee7-49b7-9200-fdc5af8c2d33";

    private static HTTPResponseController instance = null;

    protected HTTPResponseController() {
    }

    public static HTTPResponseController getInstance() {
        if (instance == null) {
            instance = new HTTPResponseController();
        }
        return instance;
    }

    public JSONObject json = null;
    Activity activity;
    String password;
    String email;
    String eventid;
    boolean deleteAnotherUser = false;
    String userId;
    int position;
    boolean flag = false;
    Activity oldActivity;
    Boolean isEdit = false;


    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public void animateProfileImage(Boolean isMain) {
        String image;
        if (isMain) {
            image = Persistance.getInstance().getUserInfo(activity).profileImage;
        } else {
            SharedPreferences sharedPreferences = activity.getSharedPreferences("ProfileImage", 0);
            image = sharedPreferences.getString("ProfileImage", "0");
        }

        if (image != null && !image.equals("0")) {
            Bitmap bitmap = decodeBase64(image);
            IntroActivity.profileImage.setImageBitmap(bitmap);
            IntroActivity.profileImage.setAlpha(0.3f);
            IntroActivity.profileImage.animate().alpha(1f).setDuration(1000);
        }


    }

    private Response.Listener<JSONObject> createRequestSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (activity == null) {
                    LoginManager.getInstance().logOut();
                    Intent intent = new Intent(activity, IntroActivity.class);
                    activity.startActivity(intent);

                    return;
                }

                if (activity.getLocalClassName().toString().equals("Activities.LoginActivity") || activity.getLocalClassName().toString().equals("Activities.IntroActivity")) {
                    try {
                        json = jsonObject;
                        ArrayList<String> listOfSports = new ArrayList<String>();
                        for (int i = 0; i < jsonObject.getJSONObject("user").getJSONArray("sports").length(); i++) {
                            listOfSports.add(jsonObject.getJSONObject("user").getJSONArray("sports").get(i).toString());
                        }
                        ArrayList<Sport> sports = new ArrayList<Sport>();
                        for (int i = 0; i < listOfSports.size(); i++) {
                            Sport sport = new Sport(listOfSports.get(i));
                            sports.add(sport);
                        }
                        System.out.println(jsonObject.getJSONObject("user").getString("firstName"));
                        User user = new User(jsonObject.getString("authKey"), jsonObject.getJSONObject("user").getString("id"),
                                jsonObject.getJSONObject("user").getString("firstName"), jsonObject.getJSONObject("user").getString("gender"),
                                jsonObject.getJSONObject("user").getString("facebookId"), jsonObject.getJSONObject("user").getString("lastName"),
                                jsonObject.getJSONObject("user").getInt("ludicoins"), jsonObject.getJSONObject("user").getInt("level"),
                                jsonObject.getJSONObject("user").getString("profileImage"), jsonObject.getJSONObject("user").getString("range"),
                                sports, email, password);
                        Persistance.getInstance().setUserInfo(activity, user);


                        if (user.range.equals("0")) {
                                Intent intent = new Intent(activity, ProfileDetailsActivity.class);
                                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                                activity.startActivity(intent);
                                activity.finish();
                        } else {
                            new CountDownTimer(1100, 100) {
                                @Override
                                public void onTick(long l) {
                                    animateProfileImage(true);
                                }

                                @Override
                                public void onFinish() {
                                    Intent intent = new Intent(activity, Main.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    //intent.putExtra("FirstTime", activity.getIntent().getBooleanExtra("FirstTime", false));
                                    activity.startActivity(intent);
                                    activity.finish();
                                }
                            }.start();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                        LoginManager.getInstance().logOut();
                        Intent intent = new Intent(activity, IntroActivity.class);
                        activity.startActivity(intent);
                    }
                } else
                    if (activity.getLocalClassName().toString().equals("Activities.RegisterActivity")) {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(activity, ResetPasswordFinalActivity.class);
                                intent.putExtra("from", "register");
                                activity.startActivity(intent);
                            }
                        }, 1000);
                    } else
                        if (activity.getLocalClassName().toString().equals("Activities.SportDetailsActivity")) {
                            Persistance.getInstance().setIsUserFirstTime(activity, true);

                            Intent intent = new Intent(activity, Main.class);
                            //intent.putExtra("FirstTime", true);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.finishAffinity();
                            activity.startActivity(intent);
                        }
            }
        };
    }

    private Response.Listener<JSONObject> createEventSuccesListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (activity.getLocalClassName().toString().equals("Activities.InviteFriendsActivity") || isEdit) {
                    HashMap<String, String> params = new HashMap<String, String>();
                    HashMap<String, String> headers = new HashMap<String, String>();
                    HashMap<String, String> urlParams = new HashMap<String, String>();
                    headers.put("authKey", Persistance.getInstance().getUserInfo(activity).authKey);

                    //set urlParams
                    activity.finish();

                    urlParams.put("eventId", eventid);
                    urlParams.put("userId", Persistance.getInstance().getUserInfo(activity).id);
                    HTTPResponseController.getInstance().getEventDetails(params, headers, activity, urlParams);


                } else {
                    Intent intent = new Intent(activity, Main.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                    activity.finish();
                }
            }
        };
    }

    private Response.Listener<JSONObject> exportEnrollDataSuccesListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    Toast.makeText(activity, "Enrollment data has been sent to email." , Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
    }

    private Response.Listener<JSONObject> reviewEventSuccesListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
    }

    private Response.Listener<JSONObject> reviewLocationSuccesListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
    }

    private Response.Listener<JSONObject> leaveEventSuccesListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (!deleteAnotherUser) {
                    Toast.makeText(activity, R.string.you_leave_the_event_successfully, Toast.LENGTH_SHORT).show();

                    HashMap<String, String> params = new HashMap<String, String>();
                    HashMap<String, String> headers = new HashMap<String, String>();
                    HashMap<String, String> urlParams = new HashMap<String, String>();
                    headers.put("authKey", Persistance.getInstance().getUserInfo(activity).authKey);

                    //set urlParams

                    urlParams.put("eventId", eventid);
                    urlParams.put("userId", Persistance.getInstance().getUserInfo(activity).id);
                    flag = true;
                    HTTPResponseController.getInstance().getEventDetails(params, headers, activity, urlParams);

                    oldActivity = activity;

                    myEventList.clear();
                    ActivitiesActivity.currentFragment.getMyEvents("0");

                    Intent intent = new Intent(activity, Main.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                } else {
                    Toast.makeText(activity, R.string.you_successfully_removed_the_user_from_event, Toast.LENGTH_SHORT).show();
                    HashMap<String, String> params = new HashMap<String, String>();
                    HashMap<String, String> headers = new HashMap<String, String>();
                    HashMap<String, String> urlParams = new HashMap<String, String>();
                    headers.put("authKey", Persistance.getInstance().getUserInfo(activity).authKey);

                    //set urlParams

                    urlParams.put("eventId", eventid);
                    urlParams.put("userId", userId);
                    HTTPResponseController.getInstance().getEventDetails(params, headers, activity, urlParams);
                    activity.finish();
                }

            }
        };
    }

    private Response.Listener<JSONObject> kickUserSuccesListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Toast.makeText(activity, R.string.you_successfully_removed_the_user_from_event, Toast.LENGTH_SHORT).show();
                for (int i = InviteFriendsActivity.participantList.size() - 1; i >= 0; i--) {
                    if (InviteFriendsActivity.participantList.get(i).userID.equals(userId)) {
                        InviteFriendsActivity.participantList.remove(i);
                    }
                }
                InviteFriendsActivity.inviteFriendsAdapter.notifyDataSetChanged();


            }
        };
    }

    private Response.Listener<JSONObject> removeOfflineSuccesListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Toast.makeText(activity, R.string.you_successfully_removed_the_offline_friend_from_event, Toast.LENGTH_SHORT).show();
                InviteFriendsActivity.participantList.remove(position);
                InviteFriendsActivity.inviteFriendsAdapter.notifyDataSetChanged();


            }
        };
    }

    private Response.Listener<JSONObject> cancelEventSuccesListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Toast.makeText(activity, R.string.you_cancel_the_event_successfully, Toast.LENGTH_SHORT).show();

                HashMap<String, String> params = new HashMap<String, String>();
                HashMap<String, String> headers = new HashMap<String, String>();
                HashMap<String, String> urlParams = new HashMap<String, String>();
                headers.put("authKey", Persistance.getInstance().getUserInfo(activity).authKey);

                //set urlParams

                urlParams.put("eventId", eventid);
                urlParams.put("userId", Persistance.getInstance().getUserInfo(activity).id);
                flag = true;
                //HTTPResponseController.getInstance().getEventDetails(params, headers, activity, urlParams);

                oldActivity = activity;

                myEventList.clear();
                ActivitiesActivity.currentFragment.getMyEvents("0");

                Intent intent = new Intent(activity, Main.class);
                /*intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/
                //activity.startActivity(intent);
                oldActivity.finish();

            }
        };
    }

    private Response.Listener<JSONObject> createAroundMeEventSuccesListener() {
        return new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (pageNumberAroundMe == 0) {
                    aroundMeEventList.clear();
                    sponsorsList.clear();
                }
                try {
                    for (int i = 0; i < jsonObject.getJSONArray("aroundMe").length(); i++) {
                        Event event = new Event();
                        event.id = jsonObject.getJSONArray("aroundMe").getJSONObject(i).getString("id");
                        int date = jsonObject.getJSONArray("aroundMe").getJSONObject(i).getInt("eventDate");
                        java.util.Date date1 = new java.util.Date((long) date * 1000);
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        String displayDate = formatter.format(date1);
                        event.eventDate = displayDate;
                        event.placeName = jsonObject.getJSONArray("aroundMe").getJSONObject(i).getString("placeName");
                        event.sportCode = jsonObject.getJSONArray("aroundMe").getJSONObject(i).getString("sportName");
                        if (event.sportCode.equalsIgnoreCase("OTH")) {
                            event.otherSportName = jsonObject.getJSONArray("aroundMe").getJSONObject(i).getString
                                    ("otherSportName");
                        }
                        event.capacity = jsonObject.getJSONArray("aroundMe").getJSONObject(i).getInt("capacity");
                        event.creatorName = jsonObject.getJSONArray("aroundMe").getJSONObject(i).getString("creatorName");
                        event.creatorId = jsonObject.getJSONArray("aroundMe").getJSONObject(i).getString("creatorId");
                        event.creatorLevel = jsonObject.getJSONArray("aroundMe").getJSONObject(i).getInt("creatorLevel");
                        event.creatorProfilePicture = jsonObject.getJSONArray("aroundMe").getJSONObject(i).getString("creatorProfilePicture");
                        event.numberOfParticipants = jsonObject.getJSONArray("aroundMe").getJSONObject(i).getInt("numberOfParticipants");
                        event.points = jsonObject.getJSONArray("aroundMe").getJSONObject(i).getInt("points");
                        event.ludicoins = jsonObject.getJSONArray("aroundMe").getJSONObject(i).getInt("ludicoins");
                        for (int j = 0; j < jsonObject.getJSONArray("aroundMe").getJSONObject(i).getJSONArray("participantsProfilePicture").length(); j++) {
                            event.participansProfilePicture.add(jsonObject.getJSONArray("aroundMe").getJSONObject(i).getJSONArray("participantsProfilePicture").getString(j));
                        }
                        System.out.println(event.id + " eventid:" + i + "  " + event.numberOfParticipants + " profilepicture" + jsonObject.getJSONArray("aroundMe").getJSONObject(i).getJSONArray("participantsProfilePicture").length());

                        String isFormBasedStr =  jsonObject.getJSONArray("aroundMe").getJSONObject(i).getString("isFormBased");
                        if (isFormBasedStr.equals("0")){
                            event.isFormBased = false;
                        }else event.isFormBased = true;

                        if (event.isFormBased == true && jsonObject.getJSONArray("aroundMe").getJSONObject(i).optJSONArray("formParameters") != null){
                         for (int j = 0; j < jsonObject.getJSONArray("aroundMe").getJSONObject(i).getJSONArray("formParameters").length(); j++) {
                            event.formParameters.add(jsonObject.getJSONArray("aroundMe").getJSONObject(i).getJSONArray("formParameters").getString(j));
                            }
                        }

                        aroundMeEventList.add(event);
                    }
                    if (pageNumberAroundMe == 0) {
                        for (int i = 0; i < jsonObject.getJSONArray("sponsors").length(); i++) {
                            Sponsors sponsors = new Sponsors();
                            sponsors.id = jsonObject.getJSONArray("sponsors").getJSONObject(i).getInt("id");
                            sponsors.logo = jsonObject.getJSONArray("sponsors").getJSONObject(i).getString("logo");
                            sponsorsList.add(sponsors);

                        }
                    }

                    ActivitiesActivity.currentFragment.updateListOfEventsAroundMe();
                    if (jsonObject.getJSONArray("aroundMe").length() >= 1) {
                        ActivitiesActivity.NumberOfRefreshAroundMe++;
                    }

                    if (pageNumberAroundMe == 0) {
                        Persistance.getInstance().setSponsors(activity, sponsorsList);
                        //Persistance.getInstance().setAroundMeActivities(activity, aroundMeEventList);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private Response.Listener<JSONObject> createMyEventSuccesListener() {
        return new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(JSONObject jsonObject) {
                //ActivitiesActivity.startHappeningNow.interrupt();

                System.out.println(jsonObject + " myevent");
                synchronized (myEventList) {
                    if (getFirstPageMyActivity) {
                        myEventList.clear();
                    }
                    try {
                        for (int i = 0; i < jsonObject.getJSONArray("myEvents").length(); i++) {
                            Event event = new Event();
                            event.id = jsonObject.getJSONArray("myEvents").getJSONObject(i).getString("id");
                            int date = jsonObject.getJSONArray("myEvents").getJSONObject(i).getInt("eventDate");
                            event.eventDateTimeStamp = date;
                            java.util.Date date1 = new java.util.Date((long) date * 1000);
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            String displayDate = formatter.format(date1);
                            event.eventDate = displayDate;
                            event.placeName = jsonObject.getJSONArray("myEvents").getJSONObject(i).getString("placeName");
                            event.sportCode = jsonObject.getJSONArray("myEvents").getJSONObject(i).getString("sportName");
                            if (event.sportCode.equalsIgnoreCase("OTH")) {
                                event.otherSportName = jsonObject.getJSONArray("myEvents").getJSONObject(i).getString
                                        ("otherSportName");
                            }
                            event.capacity = jsonObject.getJSONArray("myEvents").getJSONObject(i).getInt("capacity");
                            event.creatorName = jsonObject.getJSONArray("myEvents").getJSONObject(i).getString("creatorName");
                            event.creatorLevel = jsonObject.getJSONArray("myEvents").getJSONObject(i).getInt("creatorLevel");
                            event.creatorId = jsonObject.getJSONArray("myEvents").getJSONObject(i).getString("creatorId");
                            event.numberOfParticipants = jsonObject.getJSONArray("myEvents").getJSONObject(i).getInt("numberOfParticipants");
                            event.points = jsonObject.getJSONArray("myEvents").getJSONObject(i).getInt("points");
                            event.creatorProfilePicture = jsonObject.getJSONArray("myEvents").getJSONObject(i).getString("creatorProfilePicture");
                            event.ludicoins = jsonObject.getJSONArray("myEvents").getJSONObject(i).getInt("ludicoins");
                            event.latitude = jsonObject.getJSONArray("myEvents").getJSONObject(i).getDouble("latitude");
                            event.longitude = jsonObject.getJSONArray("myEvents").getJSONObject(i).getDouble("longitude");
                            for (int j = 0; j < jsonObject.getJSONArray("myEvents").getJSONObject(i).getJSONArray("participantsProfilePicture").length(); j++) {
                                event.participansProfilePicture.add(jsonObject.getJSONArray("myEvents").getJSONObject(i).getJSONArray("participantsProfilePicture").getString(j));

                            }
                            myEventList.add(event);

                        }
                        ActivitiesActivity.currentFragment.updateListOfMyEvents();
                        if (jsonObject.getJSONArray("myEvents").length() >= 1) {
                            ActivitiesActivity.NumberOfRefreshMyEvents++;
                        }
                        if (getFirstPageMyActivity) {
                            ArrayList<Event> localEventList = new ArrayList<>();
                            localEventList.addAll(myEventList);
                            Persistance.getInstance().setMyActivities(activity, localEventList);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                /*
                try {
                    if (!ActivitiesActivity.startHappeningNow.isAlive() && ActivitiesActivity.startHappeningNow.getState() == Thread.State.NEW) {
                        ActivitiesActivity.startHappeningNow.start();
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
                */
            }
        };
    }

    private Response.Listener<JSONObject> createMyPastEventSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                System.out.println(jsonObject + " pastEvents");
                    if (getFirstPagePastEvents) {
                        pastEventList.clear();
                    }
                    try {
                        for (int i = 0; i < jsonObject.getJSONArray("pastEvents").length(); i++) {
                            EventBrief eventBrief = new EventBrief();
                            eventBrief.id = jsonObject.getJSONArray("pastEvents").getJSONObject(i).getString("id");
                            int date = jsonObject.getJSONArray("pastEvents").getJSONObject(i).getInt("eventDate");
                            eventBrief.eventDateTimeStamp = date;
                            java.util.Date date1 = new java.util.Date((long) date * 1000);
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            String displayDate = formatter.format(date1);
                            eventBrief.eventDate = displayDate;
                            eventBrief.placeName = jsonObject.getJSONArray("pastEvents").getJSONObject(i).getString("placeName");
                            eventBrief.sportName = jsonObject.getJSONArray("pastEvents").getJSONObject(i).getString("sportName");
                            if (eventBrief.sportName.equalsIgnoreCase("OTH")) {
                                eventBrief.otherSportName = jsonObject.getJSONArray("pastEvents").getJSONObject(i).getString("otherSportName");
                            }
                            eventBrief.creatorName = jsonObject.getJSONArray("pastEvents").getJSONObject(i).getString("creatorName");
                            eventBrief.creatorLevel = Integer.parseInt(jsonObject.getJSONArray("pastEvents").getJSONObject(i).getString("creatorLevel"));
                            eventBrief.creatorId = jsonObject.getJSONArray("pastEvents").getJSONObject(i).getString("creatorId");
                            eventBrief.creatorProfilePicture = jsonObject.getJSONArray("pastEvents").getJSONObject(i).getString("creatorProfilePicture");
                            eventBrief.numberOfParticipants = Integer.parseInt(jsonObject.getJSONArray("pastEvents").getJSONObject(i).getString("numberOfParticipants"));
                            for (int j = 0; j < jsonObject.getJSONArray("pastEvents").getJSONObject(i).getJSONArray("participantsProfilePicture").length(); j++) {
                                eventBrief.participansProfilePicture.add(jsonObject.getJSONArray("pastEvents").getJSONObject(i).getJSONArray("participantsProfilePicture").getString(j));

                            }
                            eventBrief.ludicoins = jsonObject.getJSONArray("pastEvents").getJSONObject(i).optInt("ludicoins");
                            eventBrief.points = jsonObject.getJSONArray("pastEvents").getJSONObject(i).optInt("points");

                            pastEventList.add(eventBrief);
                        }

                        FullHistory.currentFragment.updateListOfPastEvents();
                        if (jsonObject.getJSONArray("pastEvents").length() >= 1) {
                            FullHistory.NumberOfRefreshPastEvents++;
                        }
                        if (getFirstPagePastEvents) {
                            ArrayList<EventBrief> localEventList = new ArrayList<>();
                            localEventList.addAll(pastEventList);
                            Persistance.getInstance().setPastEvents(activity, localEventList);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
        };
    }

    private Response.Listener<JSONObject> getLocationSuccesListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                System.out.println(jsonObject + " location");
                GMapsActivity.authLocation.clear();
                try {
                    for (int i = 0; i < jsonObject.getJSONArray("locations").length(); i++) {
                        AuthorizedLocation authLocation = new AuthorizedLocation();
                        authLocation.locationId = jsonObject.getJSONArray("locations").getJSONObject(i).getString("locationId");
                        authLocation.latitude = jsonObject.getJSONArray("locations").getJSONObject(i).getDouble("latitude");
                        authLocation.longitude = jsonObject.getJSONArray("locations").getJSONObject(i).getDouble("longitude");
                        authLocation.points = jsonObject.getJSONArray("locations").getJSONObject(i).getInt("points");
                        authLocation.authorizeLevel = jsonObject.getJSONArray("locations").getJSONObject(i).getInt("authorizeLevel");
                        authLocation.ludicoins = jsonObject.getJSONArray("locations").getJSONObject(i).getInt("ludicoins");
                        authLocation.name = jsonObject.getJSONArray("locations").getJSONObject(i).getString("name");
                        authLocation.description = jsonObject.getJSONArray("locations").getJSONObject(i).getString("description");
                        authLocation.address = jsonObject.getJSONArray("locations").getJSONObject(i).getString("address");
                        authLocation.image = jsonObject.getJSONArray("locations").getJSONObject(i).getString("image");
                        authLocation.schedule = jsonObject.getJSONArray("locations").getJSONObject(i).getString("schedule");
                        authLocation.phoneNumber = jsonObject.getJSONArray("locations").getJSONObject(i).getString("phoneNumber");

                        GMapsActivity.authLocation.add(authLocation);
                    }
                    GMapsActivity.putMarkers(GMapsActivity.authLocation);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
    }

    private Response.Listener<JSONObject> getFriendsSuccesListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                System.out.println(jsonObject + " friends");
                try {
                    if (ChatAndFriendsActivity.NumberOfRefreshFriends == 0) {
                        ChatAndFriendsActivity.friends.clear();
                    }
                    for (int i = 0; i < jsonObject.getJSONArray("friends").length(); i++) {
                        Friend friend = new Friend();
                        friend.userID = jsonObject.getJSONArray("friends").getJSONObject(i).getString("userId");
                        friend.userName = jsonObject.getJSONArray("friends").getJSONObject(i).getString("userName");
                        friend.profileImage = jsonObject.getJSONArray("friends").getJSONObject(i).getString("profileImage");
                        friend.numberOfMutuals = jsonObject.getJSONArray("friends").getJSONObject(i).getInt("numberOfMutuals");
                        friend.level = jsonObject.getJSONArray("friends").getJSONObject(i).getInt("level");
                        friend.offlineFriend = false;
                        friend.isInvited = false;

                        if (activity.getLocalClassName().toString().equals("Activities.Main")) {
                            ChatAndFriendsActivity.friends.add(friend);
                        } else {

                            InviteFriendsActivity.friendsList.add(InviteFriendsActivity.numberOfOfflineFriends + 1, friend);
                        }
                    }
                    if (activity.getLocalClassName().toString().equals("Activities.Main")) {

                        if (ChatAndFriendsActivity.NumberOfRefreshFriends == 0 && !ChatAndFriendsActivity.isFirstTimeSetFriends) {
                            ChatAndFriendsActivity.currentChatAndFriends.setFriendsAdapter();
                        } else {
                            ChatAndFriendsActivity.friendsAdapter.notifyDataSetChanged();
                            ChatAndFriendsActivity.progressBarFriends.setAlpha(0f);
                        }
                        ChatAndFriendsActivity.NumberOfRefreshFriends++;

                        RelativeLayout ll = (RelativeLayout) ChatAndFriendsActivity.currentChatAndFriends.getView().findViewById(R.id.noInternetLayout);
                        ll.getLayoutParams().height = 0;
                        ll.setLayoutParams(ll.getLayoutParams());
                    } else {
                        InviteFriendsActivity.inviteFriendsAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
    }

    private Response.Listener<JSONObject> getParticipantsSuccesListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                System.out.println(jsonObject + " participants");
                try {
                    for (int i = 0; i < jsonObject.getJSONArray("participants").length(); i++) {
                        Friend friend = new Friend();
                        friend.userID = jsonObject.getJSONArray("participants").getJSONObject(i).getString("userId");
                        friend.userName = jsonObject.getJSONArray("participants").getJSONObject(i).getString("userName");
                        friend.profileImage = jsonObject.getJSONArray("participants").getJSONObject(i).getString("profilePicture");
                        friend.level = jsonObject.getJSONArray("participants").getJSONObject(i).getInt("level");
                        friend.numberOfOffliners = jsonObject.getJSONArray("participants").getJSONObject(i).getInt("numberOfOffliners");
                        friend.offlineFriend = false;
                        friend.isInvited = false;

                        InviteFriendsActivity.participantList.add(friend);
                        for (int j = 0; j < friend.numberOfOffliners; j++) {
                            Friend offlineFriend = new Friend();
                            if( Locale.getDefault().getLanguage().startsWith("en")) {
                                offlineFriend.userName = friend.userName + activity.getResources().getString(R.string.is_friend);
                            } else if (Locale.getDefault().getLanguage().startsWith("ro")){
                                offlineFriend.userName = activity.getResources().getString(R.string.is_friend) + friend.userName;
                            }else if (Locale.getDefault().getLanguage().startsWith("fr")){
                                offlineFriend.userName = activity.getResources().getString(R.string.is_friend) + friend.userName;
                            }
                            offlineFriend.offlineFriend = true;
                            offlineFriend.profileImage = "";
                            offlineFriend.isOfflineParticipant = true;
                            offlineFriend.userID = friend.userID;
                            InviteFriendsActivity.participantList.add(offlineFriend);
                        }
                    }

                    if (!ActivityDetailsActivity.ifFirstTimeGetParticipants) {
                        System.out.println("intra aici la participanti");
                        Intent intent = new Intent(activity, InviteFriendsActivity.class);
                        intent.putExtra("isParticipant", true);
                        intent.putExtra("isEdit", false);
                        intent.putExtra("mustRedirect", true);
                        InviteFriendsActivity.isFirstTimeInviteFriends = false;
                        InviteFriendsActivity.justSeeParticipants = true;
                        activity.startActivity(intent);
                        //activity.finish();
                    } else {
                        InviteFriendsActivity.inviteFriendsAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
    }

    private Response.Listener<JSONObject> getInvitedFriendsSuccesListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                System.out.println(jsonObject + " invitedfriends");
                try {


                    for (int i = 0; i < jsonObject.getJSONArray("friends").length(); i++) {
                        Friend friend = new Friend();
                        friend.userID = jsonObject.getJSONArray("friends").getJSONObject(i).getString("userID");
                        friend.userName = jsonObject.getJSONArray("friends").getJSONObject(i).getString("userName");
                        friend.profileImage = jsonObject.getJSONArray("friends").getJSONObject(i).getString("profilePicture");
                        friend.level = jsonObject.getJSONArray("friends").getJSONObject(i).getInt("level");
                        friend.isAlreadyInvited = jsonObject.getJSONArray("friends").getJSONObject(i).getInt("isInvited");
                        friend.offlineFriend = false;
                        friend.isInvited = false;

                        InviteFriendsActivity.friendsList.add(InviteFriendsActivity.numberOfOfflineFriends + 1, friend);
                    }

                    for (int i = 0; i < jsonObject.getInt("offlineFriendsCount"); i++) {
                        Friend friend = new Friend();
                        if( Locale.getDefault().getLanguage().startsWith("en")) {
                            friend.userName = Persistance.getInstance().getUserInfo(activity).lastName + activity.getResources().getString(R.string.is_friend);
                        } else if (Locale.getDefault().getLanguage().startsWith("ro")){
                            friend.userName = activity.getResources().getString(R.string.is_friend) + Persistance.getInstance().getUserInfo(activity).lastName;
                        }else if (Locale.getDefault().getLanguage().startsWith("fr")){
                            friend.userName = activity.getResources().getString(R.string.is_friend) + Persistance.getInstance().getUserInfo(activity).lastName;
                        }
                        InviteFriendsActivity.numberOfOfflineFriends++;
                        InviteFriendsActivity.friendsList.add(1, friend);
                        friend.offlineFriend = true;
                    }

                    InviteFriendsActivity.inviteFriendsAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
    }

    private Response.Listener<JSONObject> getEventDetailsSuccesListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Bundle b = new Bundle();
                System.out.println(jsonObject + " eventDetails");
                try {
                    b.putInt("eventDate", jsonObject.getInt("eventDate"));
                    b.putString("description", jsonObject.getString("description"));
                    b.putString("placeName", jsonObject.getJSONObject("location").getString("placeName"));
                    b.putDouble("latitude", jsonObject.getJSONObject("location").getDouble("latitude"));
                    b.putDouble("longitude", jsonObject.getJSONObject("location").getDouble("longitude"));
                    b.putString("placeId", jsonObject.getJSONObject("location").getString("placeId"));
                    b.putString("placeAdress", jsonObject.getJSONObject("location").getString("placeAdress"));
                    b.putString("placeImage", jsonObject.getJSONObject("location").getString("placeImage"));
                    b.putInt("isAuthorized", jsonObject.getJSONObject("location").getInt("isAuthorized"));
                    b.putString("authorizelevel", jsonObject.getJSONObject("location").getString("authorizelevel"));
                    b.putString("sportName", jsonObject.getString("sportName"));
                    if (jsonObject.getString("sportName").equals("OTH")) {
                        b.putString("otherSportName", jsonObject.getString("otherSportName"));
                    } else {
                        b.putString("otherSportName", "");
                    }
                    b.putInt("capacity", jsonObject.getInt("capacity"));
                    b.putInt("numberOfParticipants", jsonObject.getInt("numberOfParticipants"));
                    b.putInt("points", jsonObject.getInt("points"));
                    b.putInt("ludicoins", jsonObject.getInt("ludicoins"));
                    b.putInt("privacy", jsonObject.getInt("privacy"));
                    b.putString("creatorName", jsonObject.getString("creatorName"));
                    b.putInt("creatorLevel", jsonObject.getInt("creatorLevel"));
                    b.putString("creatorId", jsonObject.getString("creatorId"));
                    b.putInt("isParticipant", jsonObject.getInt("isParticipant"));
                    b.putString("chatId", jsonObject.getString("chatId"));
                    b.putString("creatorProfilePicture", jsonObject.getString("creatorProfilePicture"));
                    ArrayList<String> participantsId = new ArrayList<>();
                    ArrayList<String> participantsName = new ArrayList<>();
                    ArrayList<String> participantsProfileImage = new ArrayList<>();
                    ArrayList<Integer> participantsLevel = new ArrayList<>();
                    for (int i = 0; i < jsonObject.getJSONArray("participants").length(); i++) {
                        participantsId.add(jsonObject.getJSONArray("participants").getJSONObject(i).getString("id"));
                        participantsName.add(jsonObject.getJSONArray("participants").getJSONObject(i).getString("name"));
                        participantsProfileImage.add(jsonObject.getJSONArray("participants").getJSONObject(i).getString("profilePicture"));
                        participantsLevel.add(jsonObject.getJSONArray("participants").getJSONObject(i).getInt("level"));
                    }
                    b.putStringArrayList("participantsId", participantsId);
                    b.putStringArrayList("participantsName", participantsName);
                    b.putStringArrayList("participantsProfileImage", participantsProfileImage);
                    b.putIntegerArrayList("participantsLevel", participantsLevel);
                    b.putString("eventId", eventid);
                    b.putString("isFormBased", jsonObject.getString("isFormBased"));
                    ArrayList<String> formParameters = new ArrayList<>();
                    if (jsonObject.optJSONArray("formParameters") != null) {
                        for (int i = 0; i < jsonObject.getJSONArray("formParameters").length(); i++) {
                            formParameters.add(jsonObject.getJSONArray("formParameters").getString(i).toString());
                        }
                        b.putStringArrayList("formParameters", formParameters);
                    }

                    if (flag == true) {
                        oldActivity.finish();
                        flag = false;
                    } else {
                        Intent intent = new Intent(activity, ActivityDetailsActivity.class);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        intent.putExtras(b);
                        activity.startActivity(intent);
                    }


                    if (ChatActivity.chatLoading != null) {
                        ChatActivity.chatLoading.setAlpha(0f);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
    }
    /*private Response.Listener<JSONObject> createGetProfileListener(final Fragment fragment) {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    User u = Persistance.getInstance().getProfileInfo(fragment.getActivity());

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
                    u.age = Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt(jsonObject.getString("yearBorn"));
                    u.profileImage = jsonObject.getString("profileImage");

                    JSONArray sports = jsonObject.getJSONArray("sports");
                    u.sports.clear();
                    for (int i = 0; i < sports.length(); ++i) {
                        u.sports.add(new Sport(sports.getString(i)));
                    }

                    MyProfileActivity mpa = (MyProfileActivity) fragment;
                    Persistance.getInstance().setProfileInfo(fragment.getActivity(), u);
                    mpa.printInfo(u);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }*/

    private Response.Listener<JSONObject> createJoinEventSuccesListener(final String eventId) {
        return new Response.Listener<JSONObject>() {
            @TargetApi(Build.VERSION_CODES.M)
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if (activity.getLocalClassName().toString().equals("Activities.ActivityDetailsActivity")) {
                        Toast.makeText(activity, R.string.join_was_successful, Toast.LENGTH_SHORT).show();

                        HashMap<String, String> params = new HashMap<String, String>();
                        HashMap<String, String> headers = new HashMap<String, String>();
                        HashMap<String, String> urlParams = new HashMap<String, String>();
                        headers.put("authKey", Persistance.getInstance().getUserInfo(activity).authKey);

                        //set urlParams

                        urlParams.put("eventId", eventId);
                        urlParams.put("userId", Persistance.getInstance().getUserInfo(activity).id);
                        flag = true;
                        HTTPResponseController.getInstance().getEventDetails(params, headers, activity, urlParams);

                        oldActivity = activity;

                        for (int i = 0; i < aroundMeEventList.size(); i++) {
                            if (aroundMeEventList.get(i).id.equals(eventId)) {
                                aroundMeEventList.remove(i);
                            }
                        }
                        Persistance.getInstance().setAroundMeActivities(activity, aroundMeEventList);
                        ActivitiesActivity.currentFragment.updateListOfEventsAroundMe();

                        myEventList.clear();
                        ActivitiesActivity.currentFragment.getMyEvents("0");
                    } else {
                        Toast.makeText(activity, R.string.join_was_successful, Toast.LENGTH_SHORT).show();

                        for (int i = 0; i < aroundMeEventList.size(); i++) {
                            if (aroundMeEventList.get(i).id.equals(eventId)) {
                                aroundMeEventList.remove(i);
                            }
                        }
                        Persistance.getInstance().setAroundMeActivities(activity, aroundMeEventList);
                        ActivitiesActivity.currentFragment.updateListOfEventsAroundMe();

                        myEventList.clear();
                        ActivitiesActivity.currentFragment.getMyEvents("0");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
    }

    private Response.Listener<JSONObject> savePointsSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {

                    System.out.println(" save points on succes");

                    PointsReceivedDialog dialog = new PointsReceivedDialog();

                    Bundle bundle = new Bundle();
                    bundle.putInt("ludicoins", jsonObject.getInt("ludicoins"));
                    bundle.putInt("points", jsonObject.getInt("points"));
                    bundle.putInt("level", jsonObject.getInt("level"));
                    dialog.setArguments(bundle);
                    dialog.show(activity.getFragmentManager(), "tag");


                    System.out.println(jsonObject.getInt("points") + " points");
                    SharedPreferences.Editor editor = activity.getSharedPreferences("HappeningNowEvent", 0).edit();
                    editor.clear();
                    editor.commit();
                    editor = activity.getSharedPreferences("locationsList", 0).edit();
                    editor.clear();
                    editor.commit();
                    //happeningNowLocation = null;
                    //happeningNowLocation = new HappeningNowLocation();
                    startedEventDate = Integer.MAX_VALUE;
                    ArrayList<Event> recacheList = new ArrayList<>();
                    for (int i = 0; i < myEventList.size(); i++) {
                        recacheList.add(myEventList.get(i));
                        if (i == 9) {
                            break;
                        }
                    }
                    Persistance.getInstance().setMyActivities(activity, recacheList);

                    myEventList.clear();
                    ActivitiesActivity.currentFragment.getMyEvents("0");

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
    }

    private Response.Listener<JSONObject> checkinSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
    }

    public String trimMessage(String json, String key) {
        String trimmedString = null;
        if (activity.getLocalClassName().toString().equals("Activities.LoginActivity")) {
            LoginActivity.progressBar.setAlpha(0f);
        }

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
            return null;
        }

        return trimmedString;
    }


    private Response.ErrorListener createRequestErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    String json = error.getMessage();
                    if (error instanceof TimeoutError) {
                        Toast.makeText(activity, R.string.check_your_internet_connection, Toast.LENGTH_LONG).show();
                    } else {
                        json = trimMessage(json, "error");
                        System.out.println(json);
                        System.out.println("de aici");
                        if (json != null) displayMessage(json);
                    }

                    if (activity.getLocalClassName().toString().equals("Activities.RegisterActivity")) {
                        ((RegisterActivity)activity).isButtonSubmitEnabled = true;
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                    LoginManager.getInstance().logOut();
                    Intent intent = new Intent(activity, IntroActivity.class);
                    activity.startActivity(intent);
                }


            }

        };
    }

    private Response.ErrorListener createErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = error.getMessage();

                json = trimMessage(json, "error");
                System.out.println("aici eroare");
                System.out.println(json);
                if (json != null) {
                    displayMessage(json);
                }
            }
        };
    }

    private Response.ErrorListener exportDataErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = error.getMessage();
                try {
                    JSONObject errorMessageJson = new JSONObject(errorMessage);
                    Toast.makeText(activity, errorMessageJson.getString("error") , Toast.LENGTH_LONG).show();
                } catch (Throwable t) {
                }
            }
        };
    }

    public void setActivity(Activity activity, String email, String password) {
        this.activity = activity;
        this.email = email;
        this.password = password;
    }

    public void setEventId(String eventId, boolean deleteAnotherUser, String userId, int position, Boolean isEdit) {
        this.eventid = eventId;
        this.position = position;
        this.deleteAnotherUser = deleteAnotherUser;
        this.userId = userId;
        this.isEdit = isEdit;
    }

    public void displayMessage(String toastString) {
        Toast.makeText(activity, toastString, Toast.LENGTH_LONG).show();
        if (activity.getLocalClassName().toString().equals("Activities.IntroActivity")) {
            SharedPreferences settings = activity.getSharedPreferences("UserDetails", activity.MODE_PRIVATE);
            settings.edit().clear().commit();
            SharedPreferences profile = activity.getSharedPreferences("ProfileImage", activity.MODE_PRIVATE);
            profile.edit().clear().commit();
            LoginManager.getInstance().logOut();
            Intent intent = new Intent(activity, IntroActivity.class);
            activity.startActivity(intent);
        } else
            if (activity.getLocalClassName().toString().equals("Activities.CreateNewActivity")) {
                CreateNewActivity.createActivityButton.setEnabled(true);
            }
            else{
                if(activity.getLocalClassName().toString().equals("Activities.LoginActivity") ||
                        activity.getLocalClassName().toString().equals("Activities.RegisterActivity") ||
                                activity.getLocalClassName().toString().equals("Activities.Pop") ||
                                activity.getLocalClassName().toString().equals("Activities.ActivitiesActivity") ){
                 // do nothing just show message
                }
                else {
                    LoginManager.getInstance().logOut();
                    Intent intent = new Intent(activity, IntroActivity.class);
                    activity.startActivity(intent);
                }
            }
    }

    public JSONObject returnResponse(HashMap<String, String> params, HashMap<String, String> headers, Activity activity, String url) {
        setActivity(activity, params.get("email"), params.get("password"));
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, url, params, headers, this.createRequestSuccessListener(), this.createRequestErrorListener());
        requestQueue.add(jsObjRequest);

        return json;
    }

    public void getAroundMeEvent(HashMap<String, String> params, HashMap<String, String> headers, Activity activity, HashMap<String, String> urlParams, Response.ErrorListener errorListener) {
        setActivity(activity, params.get("email"), params.get("password"));
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, prodServer + "api/v2/events?userId=" + urlParams.get("userId") + "&pageNumber=" + urlParams.get("pageNumber") + "&userLatitude=" + urlParams.get("userLatitude") +
                "&userLongitude=" + urlParams.get("userLongitude") + "&userRange=" + urlParams.get("userRange") + "&userSports=" + urlParams.get("userSports"), params, headers, this.createAroundMeEventSuccesListener(), errorListener);


        requestQueue.add(jsObjRequest);
    }

    public void getMyEvent(HashMap<String, String> params, HashMap<String, String> headers, Activity activity, HashMap<String, String> urlParams, Response.ErrorListener errorListener) {
        setActivity(activity, params.get("email"), params.get("password"));
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, prodServer + "api/v2/events?userId=" + urlParams.get("userId") + "&pageNumber=" + urlParams.get("pageNumber"), params, headers, this.createMyEventSuccesListener(), errorListener);
        requestQueue.add(jsObjRequest);
    }

    public void joinEvent(Activity activity, HashMap<String, String> params, HashMap<String, String> headers, String eventId, Response.ErrorListener errorListener) {
        setActivity(activity, params.get("email"), params.get("password"));
        eventid = eventId;

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, prodServer + "api/v2/joinEvent", params, headers, this.createJoinEventSuccesListener(eventId), this.createRequestErrorListener());
        requestQueue.add(jsObjRequest);

    }

    public void getAuthorizeLocations(HashMap<String, String> params, HashMap<String, String> headers, Activity activity, HashMap<String, String> urlParams) {
        setActivity(activity, params.get("email"), params.get("password"));
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, prodServer + "api/locations?latitudeNE=" + urlParams.get("latitudeNE") + "&longitudeNE=" +
                urlParams.get("longitudeNE") + "&latitudeSW=" + urlParams.get("latitudeSW") + "&longitudeSW=" + urlParams.get("longitudeSW") + "&sportCode=" + urlParams.get("sportCode"), params, headers, this.getLocationSuccesListener(), this.createRequestErrorListener());
        requestQueue.add(jsObjRequest);
    }

    public void getFriends(HashMap<String, String> params, HashMap<String, String> headers, Activity activity, HashMap<String, String> urlParams, Response.ErrorListener errorListener) {
        setActivity(activity, params.get("email"), params.get("password"));
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, prodServer + "api/friends?userId=" + urlParams.get("userId") + "&pageNumber=" + urlParams.get("pageNumber"), params, headers, this.getFriendsSuccesListener(), errorListener);
        requestQueue.add(jsObjRequest);
    }

    public void createEvent(HashMap<String, String> params, HashMap<String, String> headers, Activity activity, String eventid, Response.ErrorListener errorListener, Boolean isEdit) {
        setActivity(activity, params.get("email"), params.get("password"));
        setEventId(eventid, false, null, 0, isEdit);
        if (errorListener == null) {
            errorListener = this.createErrorListener();
        }
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, prodServer + "api/v2/event", params, headers, this.createEventSuccesListener(), errorListener);
        requestQueue.add(jsObjRequest);
    }

    public void getEventDetails(HashMap<String, String> params, HashMap<String, String> headers, Activity activity, HashMap<String, String> urlParams) {
        setActivity(activity, params.get("email"), params.get("password"));
        setEventId(urlParams.get("eventId"), false, "", -1, false);
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, prodServer + "api/v2/event?eventId=" + urlParams.get("eventId") + "&userId=" + urlParams.get("userId"), params, headers, this.getEventDetailsSuccesListener(), this.createRequestErrorListener());
        requestQueue.add(jsObjRequest);
    }

    public void leaveEvent(HashMap<String, String> params, HashMap<String, String> headers, Activity activity, boolean deleteAnotherUser) {
        setActivity(activity, params.get("email"), params.get("password"));
        setEventId(params.get("eventId"), deleteAnotherUser, params.get("userId"), -1, false);
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, prodServer + "api/leaveEvent", params, headers, this.leaveEventSuccesListener(), (Response.ErrorListener) activity);
        requestQueue.add(jsObjRequest);
    }

    public void deleteEvent(HashMap<String, String> params, HashMap<String, String> headers, Activity activity) {
        setActivity(activity, params.get("email"), params.get("password"));
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, prodServer + "api/cancelEvent", params, headers, this.cancelEventSuccesListener(), (Response.ErrorListener) activity);
        requestQueue.add(jsObjRequest);
    }

    public void getInvitedFriends(HashMap<String, String> params, HashMap<String, String> headers, Activity activity, HashMap<String, String> urlParams) {
        setActivity(activity, params.get("email"), params.get("password"));
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, prodServer + "api/friendsInvite?userId=" + urlParams.get("userId") + "&eventId=" + urlParams.get("eventId") + "&pageNumber=" + urlParams.get("pageNumber"), params, headers, this.getInvitedFriendsSuccesListener(), this.createRequestErrorListener());
        requestQueue.add(jsObjRequest);
    }

    public void updateUser(HashMap<String, String> params, HashMap<String, String> headers, Activity activity) {
        RequestQueue requestQueue = Volley.newRequestQueue(activity);

        Response.ErrorListener errorListener;
        if (activity instanceof Response.ErrorListener) {
            errorListener = (Response.ErrorListener) activity;
        } else {
            errorListener = this.createRequestErrorListener();
        }

        Response.Listener<JSONObject> listener;
        if (activity instanceof Response.Listener) {
            listener = (Response.Listener<JSONObject>) activity;
        } else {
            listener = this.createRequestSuccessListener();
        }

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, prodServer + "api/v2/user", params, headers, listener, errorListener);
        requestQueue.add(jsObjRequest);
    }

    public void getParticipants(HashMap<String, String> params, HashMap<String, String> headers, Activity activity, HashMap<String, String> urlParams) {
        setActivity(activity, params.get("email"), params.get("password"));
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, prodServer + "api/eventParticipants?eventId=" + urlParams.get("eventId") + "&userId=" + urlParams.get("userId") + "&pageNumber=" + urlParams.get("pageNumber"), params, headers, this.getParticipantsSuccesListener(), (Response.ErrorListener) activity);
        requestQueue.add(jsObjRequest);
    }

    public void kickUser(HashMap<String, String> params, HashMap<String, String> headers, Activity activity, int position) {
        setActivity(activity, params.get("email"), params.get("password"));
        setEventId(params.get("eventId"), deleteAnotherUser, params.get("userId"), position, false);
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, prodServer + "api/leaveEvent", params, headers, this.kickUserSuccesListener(), this.createRequestErrorListener());
        requestQueue.add(jsObjRequest);
    }

    public void removeOffline(HashMap<String, String> params, HashMap<String, String> headers, Activity activity, int position) {
        setActivity(activity, params.get("email"), params.get("password"));
        setEventId(params.get("eventId"), deleteAnotherUser, params.get("userId"), position, false);
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, prodServer + "api/updateOfflineFriends", params, headers, this.removeOfflineSuccesListener(), this.createRequestErrorListener());
        requestQueue.add(jsObjRequest);
    }

    public void changePassword(HashMap<String, String> params, HashMap<String, String> headers, Activity activity) {
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, prodServer + "api/changePassword", params, headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("ChangePassword response", response.toString());
            }
        }, (Response.ErrorListener) activity);
        requestQueue.add(jsObjRequest);
    }

    public void getUserProfile(HashMap<String, String> params, HashMap<String, String> headers, String id, Activity activity, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, prodServer + "api/v2/user?userId=" + id, params, headers, listener, errorListener);
        requestQueue.add(jsObjRequest);
    }

    public void resetPassword(HashMap<String, String> params, HashMap<String, String> headers, final Activity activity) {
        RequestQueue requestQueue = Volley.newRequestQueue(activity);

        Response.Listener<JSONObject> success = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Intent intent = new Intent(activity, ResetPasswordFinalActivity.class);
                intent.putExtra("from", "reset");
                activity.startActivity(intent);
                activity.finish();
            }
        };

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, prodServer + "api/resetEmailPassword", params, headers, success, this.createErrorListener());
        requestQueue.add(jsObjRequest);
    }

    public void getCoupons(String params, HashMap<String, String> headers, final CouponsActivity activity, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        RequestQueue requestQueue = Volley.newRequestQueue(activity.getActivity());
        CustomRequest request = new CustomRequest(Request.Method.GET, prodServer + "api/quests?" + params, new HashMap<String, String>(), headers, listener, activity);
        requestQueue.add(request);
    }

    public void getMyCoupons(String params, HashMap<String, String> headers, CouponsActivity activity, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        RequestQueue requestQueue = Volley.newRequestQueue(activity.getActivity());
        CustomRequest request = new CustomRequest(Request.Method.GET, prodServer + "api/myQuests?" + params, new HashMap<String, String>(), headers, listener, errorListener);
        requestQueue.add(request);
    }

    public void redeemCoupon(HashMap<String, String> params, HashMap<String, String> headers, Activity activity, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest request = new CustomRequest(Request.Method.POST, prodServer + "api/redeemCoupon", params, headers, listener, errorListener);
        requestQueue.add(request);
    }
    public void startQuest(HashMap<String, String> params, HashMap<String, String> headers, Activity activity, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest request = new CustomRequest(Request.Method.POST, prodServer + "api/acceptQuest", params, headers, listener, errorListener);
        requestQueue.add(request);
    }
    public void cancelQuest(HashMap<String, String> params, HashMap<String, String> headers, Activity activity, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest request = new CustomRequest(Request.Method.POST, prodServer + "api/cancelQuest", params, headers, listener, errorListener);
        requestQueue.add(request);
    }


    public void friendRequest(final HashMap<String, String> params, HashMap<String, String> headers, final Activity activity, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest request = new CustomRequest(Request.Method.POST, prodServer + "api/friend", params, headers, listener, errorListener);
        requestQueue.add(request);
    }

    public void getLeaderboard(String urlParams, HashMap<String, String> headers, Activity activity, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest request = new CustomRequest(Request.Method.GET, prodServer + "api/leaderboard?" + urlParams, new HashMap<String, String>(), headers, listener, errorListener);
        requestQueue.add(request);
    }

    public void getBalance(HashMap<String, String> headers, String urlParams, BalanceActivity activity, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest request = new CustomRequest(Request.Method.GET, prodServer + "api/balance?" + urlParams, new HashMap<String, String>(), headers, listener, errorListener);
        requestQueue.add(request);
    }

    public void savePoints(HashMap<String, String> params, HashMap<String, String> headers, Activity activity, Response.ErrorListener errorListener) {
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        Log.v("log", "request");
        System.out.println("request trimis 1");
        CustomRequest request = new CustomRequest(Request.Method.POST, prodServer + "api/v2/savePoints", params, headers, this.savePointsSuccessListener(), errorListener);
        requestQueue.add(request);
        System.out.println("request trimis");
    }

    public void checkin(HashMap<String, String> params, HashMap<String, String> headers, Activity activity) {
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        setActivity(activity, "", "");
        CustomRequest request = new CustomRequest(Request.Method.POST, prodServer + "api/checkin", params, headers, this.checkinSuccessListener(), this.createErrorListener());
        requestQueue.add(request);
    }

    public void valuesForUnauthorized(HashMap<String, String> headers, String urlParams, Activity activity, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest request = new CustomRequest(Request.Method.GET, prodServer + "api/valuesForUnauthorized?" + urlParams, new HashMap<String, String>(), headers, listener, errorListener);
        requestQueue.add(request);
    }

    public void getPastEvents(HashMap<String, String> params, HashMap<String, String> headers, Activity activity, HashMap<String, String> urlParams, Response.ErrorListener errorListener) {
        setActivity(activity, params.get("email"), params.get("password"));
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, prodServer + "api/v2/pastEvents?userId=" + urlParams.get("userId") + "&pageNumber=" + urlParams.get("pageNumber"), params, headers, this.createMyPastEventSuccessListener(), errorListener);
        requestQueue.add(jsObjRequest);
    }


    public void exportEnrollData(HashMap<String, String> params, HashMap<String, String> headers, Activity activity, Response.ErrorListener errorListener) {
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        setActivity(activity, "", "");
        CustomRequest request = new CustomRequest(Request.Method.POST, prodServer + "api/v2/exportEnrollData", params, headers, this.exportEnrollDataSuccesListener(), exportDataErrorListener());
        requestQueue.add(request);
    }

    public void reviewEvent(HashMap<String, String> params, HashMap<String, String> headers, Activity activity, Response.ErrorListener errorListener) {
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        setActivity(activity, "", "");
        CustomRequest request = new CustomRequest(Request.Method.POST, prodServer + "api/v2/reviewEvent", params, headers, this.reviewEventSuccesListener(), errorListener);
        requestQueue.add(request);
    }

    public void reviewLocation(HashMap<String, String> params, HashMap<String, String> headers, Activity activity, Response.ErrorListener errorListener) {
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        setActivity(activity, "", "");
        CustomRequest request = new CustomRequest(Request.Method.POST, prodServer + "api/v2/reviewLocation", params, headers, this.reviewLocationSuccesListener(), errorListener);
        requestQueue.add(request);
    }
}