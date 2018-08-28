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

    public int status;
    public int currentProgress;
    public int totalProgress;
    public long completionDate;
    public int points;

    public Quest(String questId, String title, String description,
                 String picture, long expiryDate, int dificulty, int participantsCount, int points) {
        this.questId = questId;
        this.title = title;
        this.description = description;
        this.picture = picture;
        this.expiryDate = expiryDate;
        this.dificulty = dificulty;
        this.participantsCount = participantsCount;
        this.points = points;
    }

    public Quest(String questId, String title, String description,
                 String picture, long expiryDate, int difficulty, int status, int currentProgress, int totalProgress, long completionDate, int points) {
        this.questId = questId;
        this.title = title;
        this.description = description;
        this.picture = picture;
        this.expiryDate = expiryDate;
        this.dificulty = difficulty;
        this.status = status;
        this.currentProgress = currentProgress;
        this.totalProgress = totalProgress;
        this.completionDate = completionDate;
        this.points = points;
    }


}
