package larc.ludiconprod.Utils;

import java.io.Serializable;

/**
 * Created by alex_ on 18.08.2017.
 */

public class Quest implements Serializable {

    public String picture;
    public String title;
    public String description;
    public String questId;
    public long expiryDate;
    public int dificulty;
    public int participantsCount;

    public Quest(String questId, String title, String description,
                 String picture, long expiryDate, int dificulty, int participantsCount) {
        this.questId = questId;
        this.title = title;
        this.description = description;
        this.picture = picture;
        this.expiryDate = expiryDate;
        this.dificulty = dificulty;
        this.participantsCount = participantsCount;
    }


}
