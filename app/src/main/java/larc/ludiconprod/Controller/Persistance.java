package larc.ludiconprod.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import larc.ludiconprod.Activities.BalanceActivity;
import larc.ludiconprod.User;
import larc.ludiconprod.UserProfile;
import larc.ludiconprod.Utils.Chat;
import larc.ludiconprod.Utils.Coupon;
import larc.ludiconprod.Utils.Event;
import larc.ludiconprod.Utils.EventBrief;
import larc.ludiconprod.Utils.EventDetails;
import larc.ludiconprod.Utils.Friend;
import larc.ludiconprod.Utils.HappeningNowLocation;
import larc.ludiconprod.Utils.UserPosition;
import larc.ludiconprod.Utils.util.ReviewBrief;
import larc.ludiconprod.Utils.util.Sponsors;

/**
 * Created by ancuta on 7/12/2017.
 */

public class Persistance {
    private static Persistance instance = null;

    protected Persistance() {
    }

    public static Persistance getInstance() {
        if (instance == null) {
            instance = new Persistance();
        }
        return instance;
    }

    private final String userDetailsString = "UserDetails";
    private final String profileDetailsString = "ProfileDetails";
    private final String myActivitiesString="MyActivities";
    private final String aroundMeActivitiesString="AroundMeActivities";
    private final String balanceString = "BalanceActivity";
    private final String cuponsString = "CouponsActivityCoupons";
    private final String myCuponsString = "CouponsActivityMyCoupons";
    private final String locationString = "locationsList";
    private final String conversationString = "conversationList";
    private final String leaderboardString = "leaderboardList";
    private final String happeningNowEvent = "HappeningNowEvent";
    private final String SponsorsString = "Sponsors";
    private final String pastEventsString="PastEvents";
    private final String eventString="Event";
    private final String reviewString="Reviews";


    public void setUserInfo(Activity activity, User user) {

        SharedPreferences.Editor editor = activity.getSharedPreferences(userDetailsString, 0).edit();
        Gson gson = new Gson();
        editor.putString(userDetailsString, gson.toJson(user));
        editor.commit();
    }

    public void setUnseenChats(Context activity, ArrayList<String> chatList){
        SharedPreferences.Editor editor = activity.getSharedPreferences("UnseenChats", 0).edit();
        Gson gson = new Gson();
        editor.putString("UnseenChats", gson.toJson(chatList));
        editor.commit();
    }

    public ArrayList<String> getUnseenChats(Context activity){
        String json = null;
        SharedPreferences sharedPreferences = activity.getSharedPreferences("UnseenChats", 0);
        json = sharedPreferences.getString("UnseenChats", "0");
        Gson gson = new Gson();
        ArrayList<String> chatList;
        if (json.equals("0")) {
            chatList = new ArrayList<>();
        } else {
            chatList = gson.fromJson(json, ArrayList.class);
        }
        return chatList;
    }


    public User getUserInfo(Activity activity) {
        String json = null;
        System.out.println(activity + "activity");
        SharedPreferences sharedPreferences = activity.getSharedPreferences(userDetailsString, 0);
        json = sharedPreferences.getString(userDetailsString, "0");
        Gson gson = new Gson();
        User user;
        if (json.equals("0")) {
            user = new User();
        } else {
            user = gson.fromJson(json, User.class);
        }
        return user;
    }

    public void setProfileInfo(Activity activity, UserProfile user) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(this.profileDetailsString, 0).edit();
        Gson gson = new Gson();
        editor.putString(this.profileDetailsString, gson.toJson(user));
        editor.commit();
    }


    public void setConversation(Activity activity, ArrayList<Chat> chatList){
        SharedPreferences.Editor editor = activity.getSharedPreferences(this.conversationString, 0).edit();
        Gson gson = new Gson();
        editor.putString(this.conversationString, gson.toJson(chatList));
        editor.commit();
    }

    public ArrayList<Chat> getConversation(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(this.conversationString, 0);
        String json = sharedPreferences.getString(this.conversationString, "0");
        ArrayList<Chat> chatList = new ArrayList<>();;

        Type type = new TypeToken<ArrayList<Chat>>() {
        }.getType();
        if (json.equals("0")) {
            chatList = new ArrayList<>();
        } else {
            chatList = new Gson().fromJson(json, type);
        }
        return  chatList;
    }

    public void setSponsors(Activity activity, ArrayList<Sponsors> sponsorsList){
        SharedPreferences.Editor editor = activity.getSharedPreferences(this.SponsorsString, 0).edit();
        Gson gson = new Gson();
        editor.putString(this.SponsorsString, gson.toJson(sponsorsList));
        editor.commit();
    }

    public ArrayList<Sponsors> getSponsors(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(this.SponsorsString, 0);
        String json = sharedPreferences.getString(this.SponsorsString, "0");
        ArrayList<Sponsors> sponsorsList = new ArrayList<>();;

        Type type = new TypeToken<ArrayList<Sponsors>>() {
        }.getType();
        if (json.equals("0")) {
            sponsorsList = new ArrayList<>();
        } else {
            sponsorsList = new Gson().fromJson(json, type);
        }
        return  sponsorsList;
    }

    public void setEventToReview(Activity activity, Event event){
        SharedPreferences.Editor editor = activity.getSharedPreferences(this.eventString, 0).edit();
        Gson gson = new Gson();
        editor.putString(this.eventString, gson.toJson(event));
        editor.apply();
    }

    public Event getEventToReview(Activity activity) {
        String json = null;
        System.out.println(activity + "activity");
        SharedPreferences sharedPreferences = activity.getSharedPreferences(eventString, 0);
        json = sharedPreferences.getString(eventString, "0");
        Gson gson = new Gson();
        Event event;
        if (json.equals("0")) {
            event = new Event();
        } else {
            event = gson.fromJson(json, Event.class);
        }
        return event;
    }


    public void setMyActivities(Activity activity, ArrayList<Event> eventList){
        SharedPreferences.Editor editor = activity.getSharedPreferences(this.myActivitiesString, 0).edit();
        Gson gson = new Gson();
        editor.putString(this.myActivitiesString, gson.toJson(eventList));
        editor.apply();
    }

    public void setPastEvents(Activity activity, ArrayList<EventBrief> eventList){
        SharedPreferences.Editor editor = activity.getSharedPreferences(this.pastEventsString, 0).edit();
        Gson gson = new Gson();
        editor.putString(this.pastEventsString, gson.toJson(eventList));
        editor.apply();
    }

    public ArrayList<EventBrief> getPastEvents(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(this.pastEventsString, 0);
        String json = sharedPreferences.getString(this.pastEventsString, "0");
        Gson gson = new Gson();
        ArrayList<EventBrief>eventList=new ArrayList<>();
        Type type = new TypeToken<ArrayList<EventBrief>>() {
        }.getType();
        if (json.equals("0")) {
            eventList = new ArrayList<>();
        } else {
            eventList = gson.fromJson(json, type);
        }
        return  eventList;
    }

    public void setReviews(Activity activity, ArrayList<ReviewBrief> eventList){
        SharedPreferences.Editor editor = activity.getSharedPreferences(this.reviewString, 0).edit();
        Gson gson = new Gson();
        editor.putString(this.reviewString, gson.toJson(eventList));
        editor.apply();
    }

    public ArrayList<ReviewBrief> getReviews(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(this.reviewString, 0);
        String json = sharedPreferences.getString(this.reviewString, "0");
        Gson gson = new Gson();
        ArrayList<ReviewBrief>eventList=new ArrayList<>();
        Type type = new TypeToken<ArrayList<ReviewBrief>>() {
        }.getType();
        if (json.equals("0")) {
            eventList = new ArrayList<>();
        } else {
            eventList = gson.fromJson(json, type);
        }
        return  eventList;
    }

    public ArrayList<Event> getMyActivities(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(this.myActivitiesString, 0);
        String json = sharedPreferences.getString(this.myActivitiesString, "0");
        ArrayList<Event> eventList = new ArrayList<>();;

        Type type = new TypeToken<ArrayList<Event>>() {
        }.getType();
        if (json.equals("0")) {
            eventList = new ArrayList<>();
        } else {
            eventList = new Gson().fromJson(json, type);
        }
        return  eventList;
    }

    public void setAroundMeActivities(Activity activity, ArrayList<Event> eventList){
        SharedPreferences.Editor editor = activity.getSharedPreferences(this.aroundMeActivitiesString, 0).edit();
        Gson gson = new Gson();
        editor.putString(this.aroundMeActivitiesString, gson.toJson(eventList));
        editor.commit();
    }

    public ArrayList<Event> getAroundMeActivities(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(this.aroundMeActivitiesString, 0);
        String json = sharedPreferences.getString(this.aroundMeActivitiesString, "0");
        Gson gson = new Gson();
        ArrayList<Event>eventList=new ArrayList<>();
        Type type = new TypeToken<ArrayList<Event>>() {
        }.getType();
        if (json.equals("0")) {
            eventList = new ArrayList<>();
        } else {
            eventList = gson.fromJson(json, type);
        }
        return  eventList;
    }

    public UserProfile getProfileInfo(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(this.profileDetailsString, 0);
        String json = sharedPreferences.getString(this.profileDetailsString, "0");
        Gson gson = new Gson();
        UserProfile user;
        if (json.equals("0")) {
            user = new UserProfile();
        } else {
            user = gson.fromJson(json, UserProfile.class);
        }
        return user;
    }

    public void deleteUserProfileInfo(Activity activity) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(this.profileDetailsString, 0).edit();
        editor.clear();
        editor.commit();
        editor = activity.getSharedPreferences(this.userDetailsString, 0).edit();
        editor.clear();
        editor.commit();
        LoginManager.getInstance().logOut();
    }

    public ArrayList<BalanceActivity.BalanceEntry> getBalanceCache(Activity activity) {
        String json = null;
        System.out.println(activity + "activity");
        SharedPreferences sharedPreferences = activity.getSharedPreferences(balanceString, 0);
        json = sharedPreferences.getString(balanceString, "0");
        Gson gson = new Gson();
        User user;
        if (json.equals("0")) {
            return new ArrayList<>();
        }
        Type listType = new TypeToken<ArrayList<BalanceActivity.BalanceEntry>>(){}.getType();

        return gson.fromJson(json, listType);
    }

    public void setBalanceCache(ArrayList<BalanceActivity.BalanceEntry> entries, Activity activity) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(this.balanceString, 0).edit();
        Gson gson = new Gson();
        editor.putString(this.balanceString, gson.toJson(entries));
        editor.commit();
    }
    public void setLocation(Activity activity, HappeningNowLocation locationList){
        SharedPreferences.Editor editor = activity.getSharedPreferences(this.locationString, 0).edit();
        Gson gson = new Gson();
        editor.putString(this.locationString, gson.toJson(locationList));
        editor.commit();
    }

    public HappeningNowLocation getLocation(Activity activity){
        try {
            SharedPreferences sharedPreferences = activity.getSharedPreferences(this.locationString, 0);
            String json = sharedPreferences.getString(this.locationString, "0");
            HappeningNowLocation locationsList;

            if (json.equals("0")) {
                locationsList = new HappeningNowLocation();
            } else {
                Type type = new TypeToken<HappeningNowLocation>() {}.getType();
                locationsList = new Gson().fromJson(json, type);
            }
            return locationsList;
        }
        catch (Exception ex){
            return new HappeningNowLocation();
        }
    }

    public ArrayList<Coupon> getCouponsCache(Activity activity) {
        System.out.println(activity + "activity");
        SharedPreferences sharedPreferences = activity.getSharedPreferences(cuponsString, 0);
        String json = sharedPreferences.getString(cuponsString, "0");
        Gson gson = new Gson();
        if (json.equals("0")) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<Coupon>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void setCouponsCache(ArrayList<Coupon> cs, Activity activity) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(this.cuponsString, 0).edit();
        Gson gson = new Gson();
        editor.putString(this.cuponsString, gson.toJson(cs));
        editor.commit();
    }

    public ArrayList<Coupon> getMyCouponsCache(Activity activity) {
        System.out.println(activity + "activity");
        SharedPreferences sharedPreferences = activity.getSharedPreferences(myCuponsString, 0);
        String json = sharedPreferences.getString(myCuponsString, "0");
        Gson gson = new Gson();
        if (json.equals("0")) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<Coupon>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void setMyCouponsCache(ArrayList<Coupon> cs, Activity activity) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(this.myCuponsString, 0).edit();
        Gson gson = new Gson();
        editor.putString(this.myCuponsString, gson.toJson(cs));
        editor.commit();
    }

    public ArrayList<UserPosition> getLeaderboardCache(Activity activity) {
        System.out.println(activity + "activity");
        SharedPreferences sharedPreferences = activity.getSharedPreferences(leaderboardString, 0);
        String json = sharedPreferences.getString(leaderboardString, "0");
        Gson gson = new Gson();
        if (json.equals("0")) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<UserPosition>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void setLeaderboardCache(ArrayList<UserPosition> cs, Activity activity) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(this.leaderboardString, 0).edit();
        Gson gson = new Gson();
        editor.putString(this.leaderboardString, gson.toJson(cs));
        editor.commit();
    }

    public Event getHappeningNow(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(this.happeningNowEvent, 0);
        String json = sharedPreferences.getString(this.happeningNowEvent, "0");
        if (json.equals("0")) {
            return null;
        }
        Gson gson = new Gson();
        return gson.fromJson(json, Event.class);
    }

    public void setHappeningNow(Event event, Activity activity) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(this.happeningNowEvent, 0).edit();
        Gson gson = new Gson();
        if (event != null) {
            editor.putString(this.happeningNowEvent, gson.toJson(event));
        } else {
            editor.clear();
        }
        editor.commit();
    }

    public boolean getIsUserFirstTime(Activity activity) {
       SharedPreferences sharedPreferences = activity.getSharedPreferences("isUserFirstTime", 0);
       boolean isUserFirstTime = sharedPreferences.getBoolean("isUserFirstTime", false);

       return isUserFirstTime;
    }

    public void setIsUserFirstTime(Activity activity, boolean isUserFirstTime) {
        SharedPreferences.Editor editor = activity.getSharedPreferences("isUserFirstTime", 0).edit();
        editor.putBoolean("isUserFirstTime", isUserFirstTime);
        editor.commit();
    }

}
