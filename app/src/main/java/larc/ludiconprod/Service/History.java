package larc.ludiconprod.Service;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class History {
    @SerializedName("id")
    private String id;
    @SerializedName("eventDate")
    private Long eventDate;
    @SerializedName("placeName")
    private String placeName;
    @SerializedName("sportName")
    private String sportName;
    @SerializedName("creatorName")
    private String creatorName;
    @SerializedName("creatorLevel")
    private int creatorLevel;
    @SerializedName("numberOfParticipants")
    private int numberOfParticipants;
    @SerializedName("pointsGained")
    private int pointsGained;
    @SerializedName("ludicoinsGained")
    private int ludicoinsGained;
    @SerializedName("participantsProfilePicture")
    private ArrayList<String> profilePictures;

    public int getCreatorLevel() {
        return creatorLevel;
    }

    public void setCreatorLevel(int creatorLevel) {
        this.creatorLevel = creatorLevel;
    }

    public String getSportName() {
        return sportName;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }

    public int getNumberOfParticipants() {
        return numberOfParticipants;
    }

    public void setNumberOfParticipants(int numberOfParticipants) {
        this.numberOfParticipants = numberOfParticipants;
    }

    public int getLudicoinsGained() {
        return ludicoinsGained;
    }

    public void setLudicoinsGained(int ludicoinsGained) {
        this.ludicoinsGained = ludicoinsGained;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public long getEventDate() {
        return eventDate;
    }

    public void setEventDate(long eventDate) {
        this.eventDate = eventDate;
    }

    public int getPointsGained() {
        return pointsGained;
    }

    public void setPointsGained(int pointsGained) {
        this.pointsGained = pointsGained;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public ArrayList<String> getProfilePictures() {
        return profilePictures;
    }

    public void setProfilePictures(ArrayList<String> profilePictures) {
        this.profilePictures = profilePictures;
    }


}
