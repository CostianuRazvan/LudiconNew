package larc.ludiconprod.Utils;

import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TreeMap;

import larc.ludiconprod.R;

public class EventBrief {

    public String id;
    public String sportName;
    public String eventDate;
    public String participantsCount;
    public String date;

    public String otherSportName;
    public int eventDateTimeStamp;
    public String placeName;
    public int ludicoins;
    public int points;
    public String creatorName;
    public int creatorLevel;
    public int numberOfParticipants;
    public String creatorProfilePicture;
    public ArrayList<String> participansProfilePicture = new ArrayList<String>();
    public String creatorId;


    public EventBrief(){}

    public EventBrief(JSONObject briefEvent) throws JSONException {

        this.id = briefEvent.getString("id");
        this.sportName = briefEvent.getString("sportName");
        this.eventDate = briefEvent.getString("eventDate");
        this.participantsCount = briefEvent.getString("participantsCount");

        Long date1 = Long.parseLong(this.eventDate, 10);
        DateFormat formatter = new SimpleDateFormat("d MMM HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date1 * 1000);
        date = formatter.format(calendar.getTime());

    }

    public String sportName_method(String sportName){
        String sport = null;
        this.sportName = sportName;
        if( Locale.getDefault().getLanguage().startsWith("ro")) {
            if (sportName.equals("FOT")) {
                sport = "Fotbal";
            } else if (sportName.equals("BAS")) {
                sport = "Baschet";
            } else if (sportName.equals("CYC")) {
                sport = "Biciclete";
            } else if (sportName.equals("GYM")) {
                sport = "Sală";
            } else if (sportName.equals("JOG")) {
                sport = "Alergat";
            } else if (sportName.equals("PIN")) {
                sport = "Ping-Pong";
            } else if (sportName.equals("SQU")) {
                sport = "Squash";
            } else if (sportName.equals("TEN")) {
                sport = "Tenis";
            } else if (sportName.equals("VOL")) {
                sport = "Volei";
            } else {
                sport = "Altul";
            }
        }else if (Locale.getDefault().getLanguage().startsWith("en")){
            if (sportName.equals("FOT")) {
                sport = "Football";
            } else if (sportName.equals("BAS")) {
                sport = "Basketball";
            } else if (sportName.equals("CYC")) {
                sport = "Cycling";
            } else if (sportName.equals("GYM")) {
                sport = "Gym";
            } else if (sportName.equals("JOG")) {
                sport = "Jogging";
            } else if (sportName.equals("PIN")) {
                sport = "Ping-Pong";
            } else if (sportName.equals("SQU")) {
                sport = "Squash";
            } else if (sportName.equals("TEN")) {
                sport = "Tennis";
            } else if (sportName.equals("VOL")) {
                sport = "Volleyball";
            } else {
                sport = "Other";
            }
        }else if (Locale.getDefault().getLanguage().startsWith("fr")) {
            if (sportName.equals("FOT")) {
                sport = "Football";
            } else if (sportName.equals("BAS")) {
                sport = "Basketball";
            } else if (sportName.equals("CYC")) {
                sport = "Cyclisme";
            } else if (sportName.equals("GYM")) {
                sport = "Salle de sport";
            } else if (sportName.equals("JOG")) {
                sport = "Jogging";
            } else if (sportName.equals("PIN")) {
                sport = "Ping-Pong";
            } else if (sportName.equals("SQU")) {
                sport = "Squash";
            } else if (sportName.equals("TEN")) {
                sport = "Tennis";
            } else if (sportName.equals("VOL")) {
                sport = "Volley-ball";
            } else {
                sport = "Autres";
            }
        }

        return sport;
    }

}
