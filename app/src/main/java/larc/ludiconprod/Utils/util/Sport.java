package larc.ludiconprod.Utils.util;

import java.util.TreeMap;

/**
 * Created by ancuta on 7/12/2017.
 */

public class Sport {
    private static final TreeMap<String, String> SPORT_MAP = new TreeMap<>();
    public static final String GENERAL = "GEN";

    static {
        SPORT_MAP.put("BAS_en", "basketball");
        SPORT_MAP.put("CYC_en", "cycling");
        SPORT_MAP.put("FOT_en", "football");
        SPORT_MAP.put("GYM_en", "gym");
        SPORT_MAP.put("JOG_en", "jogging");
        SPORT_MAP.put("OTH_en", "other");
        SPORT_MAP.put("PIN_en", "ping-pong");
        SPORT_MAP.put("SQU_en", "squash");
        SPORT_MAP.put("TEN_en", "tennis");
        SPORT_MAP.put("VOL_en", "volleyball");

        SPORT_MAP.put("BAS_ro", "baschet");
        SPORT_MAP.put("CYC_ro", "mers cu bicicleta");
        SPORT_MAP.put("FOT_ro", "fotbal");
        SPORT_MAP.put("GYM_ro", "sala");
        SPORT_MAP.put("JOG_ro", "alergat");
        SPORT_MAP.put("OTH_ro", "altele");
        SPORT_MAP.put("PIN_ro", "ping-pong");
        SPORT_MAP.put("SQU_ro", "squash");
        SPORT_MAP.put("TEN_ro", "tenis");
        SPORT_MAP.put("VOL_ro", "volei");
    }


    public String sportName;
    public String code;

    public Sport() {

    }

    public String getSportName(String code, String language) {
        return SPORT_MAP.get(this.code + "_" + language);
    }

    public Sport(String code, String language) {

        this.code = code;

        String temp = SPORT_MAP.get(this.code + "_" + language);
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

