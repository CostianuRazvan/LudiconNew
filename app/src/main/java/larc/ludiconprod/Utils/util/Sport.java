package larc.ludiconprod.Utils.util;

import android.app.Activity;
import android.support.annotation.NonNull;

import java.util.Locale;
import java.util.TreeMap;

import larc.ludiconprod.R;

/**
 * Created by ancuta on 7/12/2017.
 */

public class Sport {
    private static final TreeMap<String, String> SPORT_MAP = new TreeMap<>();
    public static final String GENERAL = "GEN";

    static {
        if( Locale.getDefault().getLanguage().startsWith("en")) {
            SPORT_MAP.put("BAS", "basketball");
            SPORT_MAP.put("CYC", "cycling");
            SPORT_MAP.put("FOT", "football");
            SPORT_MAP.put("GYM", "gym");
            SPORT_MAP.put("JOG", "jogging");
            SPORT_MAP.put("OTH", "other");
            SPORT_MAP.put("PIN", "ping-pong");
            SPORT_MAP.put("SQU", "squash");
            SPORT_MAP.put("TEN", "tennis");
            SPORT_MAP.put("VOL", "volleyball");
        }else  if (Locale.getDefault().getLanguage().startsWith("ro")){
            SPORT_MAP.put("BAS", "baschet");
            SPORT_MAP.put("CYC", "biciclete");
            SPORT_MAP.put("FOT", "fotbal");
            SPORT_MAP.put("GYM", "sală");
            SPORT_MAP.put("JOG", "alergat");
            SPORT_MAP.put("OTH", "altul");
            SPORT_MAP.put("PIN", "ping-pong");
            SPORT_MAP.put("SQU", "squash");
            SPORT_MAP.put("TEN", "tenis");
            SPORT_MAP.put("VOL", "volei");
        } else if( Locale.getDefault().getLanguage().startsWith("fr")) {
            SPORT_MAP.put("BAS", "basketball");
            SPORT_MAP.put("CYC", "cyclisme");
            SPORT_MAP.put("FOT", "football");
            SPORT_MAP.put("GYM", "salle de sport");
            SPORT_MAP.put("JOG", "jogging");
            SPORT_MAP.put("OTH", "autres");
            SPORT_MAP.put("PIN", "ping-pong");
            SPORT_MAP.put("SQU", "squash");
            SPORT_MAP.put("TEN", "tennis");
            SPORT_MAP.put("VOL", "volley-ball");
        }
    }
    public final String sportName;
    public final String code;


    public Sport(String code){

        this.code=code;
        /*switch (code){
           case "BAS":this.sportName="basketball";
               break;
            case "CYC":this.sportName="cycling";
                break;
            case "FOT":this.sportName="football";
                break;
            case "GYM":this.sportName="gym";
                break;
            case "JOG":this.sportName="jogging";
                break;
            case "OTH":this.sportName="other";
                break;
            case "PIN":this.sportName="ping-pong";
                break;
            case "SQU":this.sportName="squash";
                break;
            case "TEN":this.sportName="tennis";
                break;
            case "VOL":this.sportName="volleyball";
                break;
            default: this.sportName="";
                break;

        }*/

        String temp = SPORT_MAP.get(this.code);
        if (temp != null) {
            this.sportName = temp;
        } else {
            this.sportName = "";
        }
    }

    public static TreeMap<String, String> getSportMap() {
        return SPORT_MAP;
    }
}

