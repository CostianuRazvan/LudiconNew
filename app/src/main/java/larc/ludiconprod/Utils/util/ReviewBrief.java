package larc.ludiconprod.Utils.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import larc.ludiconprod.Utils.Review;

public class ReviewBrief {

    public String id;
    public String userName;
    public String socialRate;
    public String reviewPreview;
    public String reviewDate;
    public String date;

    public double userRate;
    public int countSocialRate;
    public int excellentPerc;
    public int goodPerc;
    public int averagePerc;
    public int bellowAveragePerc;
    public int poorPerc;
    public ArrayList<Review> listOfReviews = new ArrayList<>();

    public ReviewBrief(){}

    public ReviewBrief(JSONObject briefReview) throws JSONException {

        this.id = briefReview.getString("id");
        this.userName = briefReview.getString("userName");
        this.socialRate = briefReview.getString("socialRate");
        this.reviewPreview = briefReview.getString("reviewPreview");
        this.reviewDate = briefReview.getString("reviewDate");

        Long date1 = Long.parseLong(this.reviewDate, 10);
        DateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date1 * 1000);
        date = formatter.format(calendar.getTime());

    }

}
