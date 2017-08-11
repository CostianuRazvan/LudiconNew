package larc.ludiconprod.Controller;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Base64;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;


import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import larc.ludiconprod.Activities.ActivitiesActivity;
import larc.ludiconprod.Activities.CreateNewActivity;
import larc.ludiconprod.Activities.GMapsActivity;
import larc.ludiconprod.Activities.IntroActivity;
import larc.ludiconprod.Activities.InviteFriendsActivity;
import larc.ludiconprod.Activities.LoginActivity;
import larc.ludiconprod.Activities.Main;
import larc.ludiconprod.Activities.ProfileDetailsActivity;
import larc.ludiconprod.Utils.EventDetails;
import larc.ludiconprod.Utils.Friend;
import larc.ludiconprod.Utils.util.AuthorizedLocation;
import larc.ludiconprod.Utils.util.Sport;
import larc.ludiconprod.User;
import larc.ludiconprod.Utils.Event;

import static larc.ludiconprod.Activities.ActivitiesActivity.aroundMeEventList;
import static larc.ludiconprod.Activities.ActivitiesActivity.fradapter;
import static larc.ludiconprod.Activities.ActivitiesActivity.myAdapter;
import static larc.ludiconprod.Activities.ActivitiesActivity.myEventList;

/**
 * Created by ancuta on 7/12/2017.
 */

public class HTTPResponseController {

    String prodServer ="http://207.154.236.13/";

    private static HTTPResponseController instance = null;

    protected HTTPResponseController() {
    }

    public static HTTPResponseController getInstance() {
        if(instance == null) {
            instance = new HTTPResponseController();
        }
        return instance;
    }

    public JSONObject json=null;
    Activity activity;
    String password;
    String email;
    String eventid;

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public void animateProfileImage(){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("ProfileImage", 0);
        String image=sharedPreferences.getString("ProfileImage", "0");
        if(image != null && !image.equals("0")){
            Bitmap bitmap =decodeBase64(image);
            IntroActivity.profileImage.setImageBitmap(bitmap);
            IntroActivity.profileImage.setAlpha(0.3f);
            IntroActivity.profileImage.animate().alpha(1f).setDuration(1000);}
       // }else if(!imageJson.equals("")){
           // Bitmap bitmap =decodeBase64(imageJson);
           // IntroActivity.profileImage.setImageBitmap(bitmap);

       // }



    }

    private Response.Listener<JSONObject>  createRequestSuccessListener(){
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if(activity.getLocalClassName().toString().equals("Activities.LoginActivity")||activity.getLocalClassName().toString().equals("Activities.IntroActivity")) {
                    try {
                        json = jsonObject;
                        ArrayList<String> listOfSports =new ArrayList<String>();
                        for(int i=0;i<jsonObject.getJSONObject("user").getJSONArray("sports").length();i++){
                            listOfSports.add(jsonObject.getJSONObject("user").getJSONArray("sports").get(i).toString());
                        }
                        ArrayList<Sport> sports =new ArrayList<Sport>();
                        for(int i=0;i<listOfSports.size();i++){
                            Sport sport=new Sport(listOfSports.get(i));
                            sports.add(sport);
                        }
                        User user=new User(jsonObject.getString("authKey"),jsonObject.getJSONObject("user").getString("id"),
                                jsonObject.getJSONObject("user").getString("firstName"),jsonObject.getJSONObject("user").getString("gender"),
                                jsonObject.getJSONObject("user").getString("facebookId"),jsonObject.getJSONObject("user").getString("lastName"),
                                jsonObject.getJSONObject("user").getInt("ludicoins"),jsonObject.getJSONObject("user").getInt("level"),
                                jsonObject.getJSONObject("user").getString("profileImage"),jsonObject.getJSONObject("user").getString("range"),
                                sports,email,password);
                        Persistance.getInstance().setUserInfo(activity, user);


                        if(user.range.equals("0")){
                           /* new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(activity, ProfileDetailsActivity.class);
                                    activity.startActivity(intent);
                                }
                            }, 5000);*/

                            new CountDownTimer(1100, 100) {
                                @Override
                                public void onTick(long l) {
                                    animateProfileImage();
                                }

                                @Override
                                public void onFinish() {
                                    Intent intent = new Intent(activity, ProfileDetailsActivity.class);
                                    activity.startActivity(intent);
                                }
                            }.start();
                            }



                        else{
                            /*new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(activity, Main.class);
                                    activity.startActivity(intent);
                                }
                            }, 5000);*/

                            new CountDownTimer(1100, 100) {
                                @Override
                                public void onTick(long l) {
                                    animateProfileImage();
                                }

                                @Override
                                public void onFinish() {
                                    Intent intent = new Intent(activity, Main.class);
                                    activity.startActivity(intent);
                                }
                            }.start();


                        }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
                else if(activity.getLocalClassName().toString().equals("Activities.RegisterActivity")){

                    Toast.makeText(activity,"Account has created!!",Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(activity, LoginActivity.class);
                            activity.startActivity(intent);
                        }
                    }, 3000);


                }
                else if(activity.getLocalClassName().toString().equals("Activities.SportDetailsActivity")){


                    Intent intent = new Intent(activity, Main.class);
                    activity.startActivity(intent);

                }

            }
        };
    }
    private Response.Listener<JSONObject>  createEventSuccesListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Intent intent =new Intent(activity,Main.class);
                activity.startActivity(intent);
                activity.finish();
            }
        };
    }

    private Response.Listener<JSONObject>  createAroundMeEventSuccesListener(){
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                System.out.println(jsonObject+" ceva");
                try {
                    for (int i = 0; i < jsonObject.getJSONArray("aroundMe").length();i++ ){
                        Event event=new Event();
                        event.id=jsonObject.getJSONArray("aroundMe").getJSONObject(i).getString("id");
                        int date=jsonObject.getJSONArray("aroundMe").getJSONObject(i).getInt("eventDate");
                        java.util.Date date1=new java.util.Date((long)date*1000);
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        String displayDate = formatter.format(date1);
                        event.eventDate=displayDate;
                        event.placeName=jsonObject.getJSONArray("aroundMe").getJSONObject(i).getString("placeName");
                        event.sportCode=jsonObject.getJSONArray("aroundMe").getJSONObject(i).getString("sportName");
                        event.capacity=jsonObject.getJSONArray("aroundMe").getJSONObject(i).getInt("capacity");
                        event.creatorName=jsonObject.getJSONArray("aroundMe").getJSONObject(i).getString("creatorName");
                        event.creatorId=jsonObject.getJSONArray("aroundMe").getJSONObject(i).getString("creatorId");
                        event.creatorLevel=jsonObject.getJSONArray("aroundMe").getJSONObject(i).getInt("creatorLevel");
                        event.creatorProfilePicture=jsonObject.getJSONArray("aroundMe").getJSONObject(i).getString("creatorProfilePicture");
                        event.numberOfParticipants=jsonObject.getJSONArray("aroundMe").getJSONObject(i).getInt("numberOfParticipants");
                        event.points=jsonObject.getJSONArray("aroundMe").getJSONObject(i).getInt("points");
                        event.ludicoins=jsonObject.getJSONArray("aroundMe").getJSONObject(i).getInt("ludicoins");
                        for(int j=0;j < jsonObject.getJSONArray("aroundMe").getJSONObject(i).getJSONArray("participantsProfilePicture").length();j++){
                            event.participansProfilePicture.add(jsonObject.getJSONArray("aroundMe").getJSONObject(i).getJSONArray("participantsProfilePicture").getString(j));
                        }
                        System.out.println(event.id+" eventid:"+i+"  "+  event.numberOfParticipants + " profilepicture"+ jsonObject.getJSONArray("aroundMe").getJSONObject(i).getJSONArray("participantsProfilePicture").length() );
                        aroundMeEventList.add(event);

                    }
                    ActivitiesActivity.currentFragment.updateListOfEventsAroundMe(false);
                    if(jsonObject.getJSONArray("aroundMe").length() >= 1){
                        ActivitiesActivity.NumberOfRefreshAroundMe++;
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        };
    }

    private Response.Listener<JSONObject>  createMyEventSuccesListener(){
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                System.out.println(jsonObject+" myevent");
                try {
                    for (int i = 0; i < jsonObject.getJSONArray("myEvents").length();i++ ){
                        Event event=new Event();
                        event.id=jsonObject.getJSONArray("myEvents").getJSONObject(i).getString("id");
                        int date=jsonObject.getJSONArray("myEvents").getJSONObject(i).getInt("eventDate");
                        java.util.Date date1=new java.util.Date((long)date*1000);
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        String displayDate = formatter.format(date1);
                        event.eventDate=displayDate;
                        event.placeName=jsonObject.getJSONArray("myEvents").getJSONObject(i).getString("placeName");
                        event.sportCode=jsonObject.getJSONArray("myEvents").getJSONObject(i).getString("sportName");
                        event.capacity=jsonObject.getJSONArray("myEvents").getJSONObject(i).getInt("capacity");
                        event.creatorName=jsonObject.getJSONArray("myEvents").getJSONObject(i).getString("creatorName");
                        event.creatorLevel=jsonObject.getJSONArray("myEvents").getJSONObject(i).getInt("creatorLevel");
                        event.numberOfParticipants=jsonObject.getJSONArray("myEvents").getJSONObject(i).getInt("numberOfParticipants");
                        event.points=jsonObject.getJSONArray("myEvents").getJSONObject(i).getInt("points");
                        event.creatorProfilePicture=jsonObject.getJSONArray("myEvents").getJSONObject(i).getString("creatorProfilePicture");
                        event.ludicoins=jsonObject.getJSONArray("myEvents").getJSONObject(i).getInt("ludicoins");
                        for(int j=0;j < jsonObject.getJSONArray("myEvents").getJSONObject(i).getJSONArray("participantsProfilePicture").length();j++){
                            event.participansProfilePicture.add(jsonObject.getJSONArray("myEvents").getJSONObject(i).getJSONArray("participantsProfilePicture").getString(j));

                        }
                        myEventList.add(event);

                    }
                    ActivitiesActivity.currentFragment.updateListOfMyEvents(false);
                    //adapter notifydatssetchanged
                   // myAdapter.notifyDataSetChanged();
                    if(jsonObject.getJSONArray("myEvents").length() >= 1){
                        ActivitiesActivity.NumberOfRefreshMyEvents++;
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        };
    }
    private Response.Listener<JSONObject>  getLocationSuccesListener(){
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                System.out.println(jsonObject +" location");
                GMapsActivity.authLocation.clear();
                try {
                    for (int i = 0; i < jsonObject.getJSONArray("locations").length();i++ ) {
                        AuthorizedLocation authLocation=new AuthorizedLocation();
                        authLocation.locationId = jsonObject.getJSONArray("locations").getJSONObject(i).getString("locationId");
                        authLocation.latitude=jsonObject.getJSONArray("locations").getJSONObject(i).getDouble("latitude");
                        authLocation.longitude=jsonObject.getJSONArray("locations").getJSONObject(i).getDouble("longitude");
                        authLocation.points=jsonObject.getJSONArray("locations").getJSONObject(i).getInt("points");
                        authLocation.authorizeLevel=jsonObject.getJSONArray("locations").getJSONObject(i).getInt("authorizeLevel");
                        authLocation.ludicoins=jsonObject.getJSONArray("locations").getJSONObject(i).getInt("ludicoins");
                        authLocation.name=jsonObject.getJSONArray("locations").getJSONObject(i).getString("name");
                        authLocation.description=jsonObject.getJSONArray("locations").getJSONObject(i).getString("description");
                        authLocation.address=jsonObject.getJSONArray("locations").getJSONObject(i).getString("address");
                        authLocation.image=jsonObject.getJSONArray("locations").getJSONObject(i).getString("image");
                        authLocation.schedule=jsonObject.getJSONArray("locations").getJSONObject(i).getString("schedule");
                        authLocation.phoneNumber=jsonObject.getJSONArray("locations").getJSONObject(i).getString("phoneNumber");

                        GMapsActivity.authLocation.add(authLocation);
                    }
                    GMapsActivity.putMarkers(GMapsActivity.authLocation);
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        };
    }
    private Response.Listener<JSONObject>  getFriendsSuccesListener(){
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                System.out.println(jsonObject +" friends");
                try {
                    for (int i = 0; i < jsonObject.getJSONArray("friends").length();i++ ) {
                        Friend friend=new Friend();
                        friend.userID = jsonObject.getJSONArray("friends").getJSONObject(i).getString("userId");
                        friend.userName=jsonObject.getJSONArray("friends").getJSONObject(i).getString("userName");
                        friend.profileImage=jsonObject.getJSONArray("friends").getJSONObject(i).getString("profileImage");
                        friend.numberOfMutuals=jsonObject.getJSONArray("friends").getJSONObject(i).getInt("numberOfMutuals");
                        friend.level=jsonObject.getJSONArray("friends").getJSONObject(i).getInt("level");
                        friend.offlineFriend=false;
                        friend.isInvited=false;

                        InviteFriendsActivity.friendsList.add(InviteFriendsActivity.numberOfOfflineFriends+1,friend);
                    }
                    InviteFriendsActivity.inviteFriendsAdapter.notifyDataSetChanged();
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        };
    }
    private Response.Listener<JSONObject>  getEventDetailsSuccesListener(){
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                System.out.println(jsonObject +" eventDetails");
                try {
                    EventDetails eventDetails=new EventDetails();
                    eventDetails.eventDate=jsonObject.getInt("eventDate");
                    eventDetails.description=jsonObject.getString("description");
                    eventDetails.placeName=jsonObject.getJSONObject("location").getString("placeName");
                    eventDetails.latitude=jsonObject.getJSONObject("location").getInt("latitude");
                    eventDetails.longitude=jsonObject.getJSONObject("location").getInt("longitude");
                    eventDetails.placeId=jsonObject.getJSONObject("location").getString("placeId");
                    eventDetails.isAuthorized=jsonObject.getJSONObject("location").getBoolean("isAuthorized");
                    eventDetails.sportName=jsonObject.getString("sportName");
                    eventDetails.otherSportName=jsonObject.getString("otherSportName");
                    eventDetails.capacity=jsonObject.getInt("capacity");
                    eventDetails.numberOfParticipants=jsonObject.getInt("numberOfParticipants");
                    eventDetails.points=jsonObject.getInt("points");
                    eventDetails.ludicoins=jsonObject.getInt("ludicoins");
                    eventDetails.creatorName=jsonObject.getString("creatorName");
                    eventDetails.creatorLevel=jsonObject.getInt("creatorLevel");
                    eventDetails.creatorId=jsonObject.getString("creatorId");
                    eventDetails.creatorProfilePicture=jsonObject.getString("creatorProfilePicture");
                    for(int i=0;i < jsonObject.getJSONArray("participants").length();i++){
                        Friend friend=new Friend();
                        friend.userID=jsonObject.getJSONArray("participants").getJSONObject(i).getString("id");
                        friend.userName=jsonObject.getJSONArray("participants").getJSONObject(i).getString("name");
                        friend.profileImage=jsonObject.getJSONArray("participants").getJSONObject(i).getString("profilePicture");
                        friend.level=jsonObject.getJSONArray("participants").getJSONObject(i).getInt("level");
                    }


                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        };
    }
    private Response.Listener<JSONObject>  createJoinEventSuccesListener(){
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    myEventList.clear();
                    ActivitiesActivity.currentFragment.getMyEvents("0");
                    for(int i=0;i < aroundMeEventList.size();i++){
                        if(aroundMeEventList.get(i).id.equals(eventid)){
                            aroundMeEventList.remove(i);
                        }
                    }
                    fradapter.notifyDataSetChanged();
                    Toast.makeText(activity,"Join was successful!",Toast.LENGTH_SHORT).show();

                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        };
    }
    public String trimMessage(String json, String key){
        String trimmedString = null;
        if(activity.getLocalClassName().toString().equals("Activities.LoginActivity")) {
            LoginActivity.progressBar.setAlpha(0f);
        }

        try{
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
        } catch(JSONException e){
            e.printStackTrace();
            return null;
        }

        return trimmedString;
    }


    private  Response.ErrorListener createRequestErrorListener(){
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = error.getMessage();
                json = trimMessage(json, "error");
                if(json != null) displayMessage(json);


            }

        };
    }

    public void setActivity(Activity activity, String email, String password){
        this.activity=activity;
        this.email=email;
        this.password=password;
    }

    public void displayMessage(String toastString){
        Toast.makeText(activity, toastString, Toast.LENGTH_LONG).show();
        if(activity.getLocalClassName().toString().equals("Activities.IntroActivity")){
            SharedPreferences settings = activity.getSharedPreferences("UserDetails",activity.MODE_PRIVATE);
            settings.edit().clear().commit();
            SharedPreferences profile = activity.getSharedPreferences("ProfileImage", activity.MODE_PRIVATE);
            profile.edit().clear().commit();
            LoginManager.getInstance().logOut();
            Intent intent = new Intent(activity, IntroActivity.class);
            activity.startActivity(intent);
        }else if(activity.getLocalClassName().toString().equals("Activities.CreateNewActivity")){
            CreateNewActivity.createActivityButton.setEnabled(true);

        }
    }

    public JSONObject returnResponse(HashMap<String,String> params, HashMap<String,String> headers, Activity activity, String url){
        setActivity(activity,params.get("email"),params.get("password"));
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, url, params,headers,this.createRequestSuccessListener(), this.createRequestErrorListener());
        requestQueue.add(jsObjRequest);

        return json;
    }
    public void getAroundMeEvent(HashMap<String,String> params, HashMap<String,String> headers, Activity activity,HashMap<String,String> urlParams){
        setActivity(activity,params.get("email"),params.get("password"));
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, prodServer+"api/events?userId="+urlParams.get("userId")+"&pageNumber="+urlParams.get("pageNumber")+"&userLatitude="+urlParams.get("userLatitude")+
                "&userLongitude="+urlParams.get("userLongitude")+"&userRange="+urlParams.get("userRange")+"&userSports="+urlParams.get("userSports"), params,headers,this.createAroundMeEventSuccesListener(), this.createRequestErrorListener());
        requestQueue.add(jsObjRequest);

    }
    public void getMyEvent(HashMap<String,String> params, HashMap<String,String> headers, Activity activity,HashMap<String,String> urlParams){
        setActivity(activity,params.get("email"),params.get("password"));
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, prodServer+"api/events?userId="+urlParams.get("userId")+"&pageNumber="+urlParams.get("pageNumber"),params,headers,this.createMyEventSuccesListener(), this.createRequestErrorListener());
        requestQueue.add(jsObjRequest);

    }
    public void joinEvent(HashMap<String,String> params, HashMap<String,String> headers,String eventId){
        setActivity(activity,params.get("email"),params.get("password"));
        eventid=eventId;
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, prodServer+"api/joinEvent/", params,headers,this.createJoinEventSuccesListener(), this.createRequestErrorListener());
        requestQueue.add(jsObjRequest);

    }

    public void getAuthorizeLocations(HashMap<String,String> params, HashMap<String,String> headers, Activity activity,HashMap<String,String> urlParams){
        setActivity(activity,params.get("email"),params.get("password"));
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, prodServer+"api/locations?latitudeNE="+urlParams.get("latitudeNE")+"&longitudeNE="+
                urlParams.get("longitudeNE")+"&latitudeSW="+urlParams.get("latitudeSW")+"&longitudeSW="+urlParams.get("longitudeSW")+"&sportCode="+urlParams.get("sportCode"),params,headers,this.getLocationSuccesListener(), this.createRequestErrorListener());
        requestQueue.add(jsObjRequest);
    }
    public void getFriends(HashMap<String,String> params, HashMap<String,String> headers, Activity activity,HashMap<String,String> urlParams){
        setActivity(activity,params.get("email"),params.get("password"));
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, prodServer+"api/friends?userId="+urlParams.get("userId")+"&pageNumber="+urlParams.get("pageNumber"),params,headers,this.getFriendsSuccesListener(), this.createRequestErrorListener());
        requestQueue.add(jsObjRequest);
    }
    public void createEvent(HashMap<String,String> params, HashMap<String,String> headers, Activity activity){
        setActivity(activity,params.get("email"),params.get("password"));
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, prodServer+"api/event/",params,headers,this.createEventSuccesListener(), this.createRequestErrorListener());
        requestQueue.add(jsObjRequest);
    }
    public void getEventDetails(HashMap<String,String> params, HashMap<String,String> headers, Activity activity,HashMap<String,String> urlParams){
        setActivity(activity,params.get("email"),params.get("password"));
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, prodServer+"api/event?eventId="+urlParams.get("eventId")+"&userId="+urlParams.get("userId"),params,headers,this.getEventDetailsSuccesListener(), this.createRequestErrorListener());
        requestQueue.add(jsObjRequest);
    }

}
