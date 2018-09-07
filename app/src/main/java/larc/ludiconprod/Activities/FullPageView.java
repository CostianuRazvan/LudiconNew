package larc.ludiconprod.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.EventBrief;
import larc.ludiconprod.Utils.EventDetails;
import larc.ludiconprod.Utils.Review;
import larc.ludiconprod.Utils.util.ReviewBrief;

public class FullPageView extends Activity {

    RelativeLayout backButton;
    TextView rating;
    TextView countReview;
    RelativeLayout socialReviews;
    ProgressBar progressBar1;
    ProgressBar progressBar2;
    ProgressBar progressBar3;
    ProgressBar progressBar4;
    ProgressBar progressBar5;
    LinearLayout layout_reviews;
    TextView userName;
    CircleImageView userImageProfile;
    TextView date;
    TextView review;
    int id_layout = 987;

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_page_view);

        final Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Medium.ttf");


        RelativeLayout toolbar = (RelativeLayout) findViewById(R.id.tool_bar_view);
        TextView title = (TextView) toolbar.findViewById(R.id.titleText);
        title.setTypeface(typeFace);
        TextView titleText = (TextView) findViewById(R.id.titleText);
        title.setTypeface(typeFace);
        titleText.setText(getResources().getString(R.string.reviews1));
        this.backButton=(RelativeLayout)findViewById(R.id.backButton) ;
        rating = (TextView) findViewById(R.id.rating);
        countReview = (TextView) findViewById(R.id.countReview);
        socialReviews = (RelativeLayout) findViewById(R.id.socialReviews);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar3 = (ProgressBar) findViewById(R.id.progressBar3);
        progressBar4 = (ProgressBar) findViewById(R.id.progressBar4);
        progressBar5 = (ProgressBar) findViewById(R.id.progressBar5);
        layout_reviews = (LinearLayout) findViewById(R.id.layout_reviews);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final Bundle b;
        b = getIntent().getExtras();
        final ReviewBrief reviewBrief = new ReviewBrief();
        reviewBrief.userRate = b.getDouble("socialRate");
        reviewBrief.countSocialRate = b.getInt("countSocialRate");
        reviewBrief.excellentPerc = b.getInt("excellentPerc");
        reviewBrief.goodPerc = b.getInt("goodPerc");
        reviewBrief.averagePerc = b.getInt("averagePerc");
        reviewBrief.bellowAveragePerc = b.getInt("bellowAveragePerc");
        reviewBrief.poorPerc = b.getInt("poorPerc");
        for (int i = 0; i < b.getStringArrayList("userNameList").size(); i++) {
            Review review = new Review();
            review.id = b.getStringArrayList("idList").get(i);
            review.userId = b.getStringArrayList("userIdList").get(i);
            review.userName = b.getStringArrayList("userNameList").get(i);
            review.userImage = b.getStringArrayList("userPictureList").get(i);
            review.review = b.getStringArrayList("reviewPreviewList").get(i);
            review.rate = Double.valueOf(b.getStringArrayList("socialRateList").get(i));
            review.reviewDate = b.getStringArrayList("reviewDateList").get(i);
            reviewBrief.listOfReviews.add(review);
        }

        rating.setText(String.valueOf(reviewBrief.userRate));
        countReview.setText(getResources().getString(R.string.based_on) + " " + reviewBrief.countSocialRate + " " + getResources().getString(R.string.reviews));

        progressBar1.setProgress(reviewBrief.excellentPerc);
        progressBar2.setProgress(reviewBrief.goodPerc);
        progressBar3.setProgress(reviewBrief.averagePerc);
        progressBar4.setProgress(reviewBrief.bellowAveragePerc);
        progressBar5.setProgress(reviewBrief.poorPerc);


        for (int i = 0; i < reviewBrief.listOfReviews.size(); i++) {

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            LayoutInflater vi = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            params1.setMargins(70, 0, 70, 50);

            LinearLayout mView = (LinearLayout) vi.inflate(R.layout.review, layout_reviews, false);
            mView.setId(id_layout + i);
            if (i != 0) {
                params.addRule(RelativeLayout.BELOW, mView.getId() - 1);
            }
            mView.setLayoutParams(params);
            mView.setLayoutParams(params1);

            final ImageView star1 = (ImageView) mView.findViewById(R.id.star1);
            final ImageView star2 = (ImageView) mView.findViewById(R.id.star2);
            final ImageView star3 = (ImageView) mView.findViewById(R.id.star3);
            final ImageView star4 = (ImageView) mView.findViewById(R.id.star4);
            final ImageView star5 = (ImageView) mView.findViewById(R.id.star5);

            userName = (TextView) mView.findViewById(R.id.userName);
            userImageProfile = (CircleImageView) mView.findViewById(R.id.userImageProfile);
            date = (TextView) mView.findViewById(R.id.date);
            review = (TextView) mView.findViewById(R.id.review);

            star1.setBackgroundResource(R.drawable.icon_star_full);
            star2.setBackgroundResource(R.drawable.icon_star_full);
            star3.setBackgroundResource(R.drawable.icon_star_full);
            star4.setBackgroundResource(R.drawable.icon_star_full);
            star5.setBackgroundResource(R.drawable.icon_star_full);


            userName.setText(reviewBrief.listOfReviews.get(i).userName);
            review.setText("\"" + reviewBrief.listOfReviews.get(i).review + "\"");

            Bitmap bitmap = decodeBase64(reviewBrief.listOfReviews.get(i).userImage);
            userImageProfile.setImageBitmap(bitmap);

            Long date1 = Long.parseLong(reviewBrief.listOfReviews.get(i).reviewDate, 10);
            DateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date1 * 1000);
            String dateStr = formatter.format(calendar.getTime());

            date.setText(dateStr);

            if (reviewBrief.listOfReviews.get(i).rate == 1.0){
                star1.setBackgroundResource(R.drawable.icon_star_full);
                star2.setBackgroundResource(R.drawable.icon_star_line);
                star3.setBackgroundResource(R.drawable.icon_star_line);
                star4.setBackgroundResource(R.drawable.icon_star_line);
                star5.setBackgroundResource(R.drawable.icon_star_line);
            }else if (reviewBrief.listOfReviews.get(i).rate > 1.0 && reviewBrief.listOfReviews.get(i).rate <= 1.5){
                star1.setBackgroundResource(R.drawable.icon_star_full);
                star2.setBackgroundResource(R.drawable.icon_star_half);
                star3.setBackgroundResource(R.drawable.icon_star_line);
                star4.setBackgroundResource(R.drawable.icon_star_line);
                star5.setBackgroundResource(R.drawable.icon_star_line);
            }else if (reviewBrief.listOfReviews.get(i).rate >1.5 && reviewBrief.listOfReviews.get(i).rate <= 2.0){
                star1.setBackgroundResource(R.drawable.icon_star_full);
                star2.setBackgroundResource(R.drawable.icon_star_full);
                star3.setBackgroundResource(R.drawable.icon_star_line);
                star4.setBackgroundResource(R.drawable.icon_star_line);
                star5.setBackgroundResource(R.drawable.icon_star_line);
            }else if (reviewBrief.listOfReviews.get(i).rate > 2.0 && reviewBrief.listOfReviews.get(i).rate <= 2.5){
                star1.setBackgroundResource(R.drawable.icon_star_full);
                star2.setBackgroundResource(R.drawable.icon_star_full);
                star3.setBackgroundResource(R.drawable.icon_star_half);
                star4.setBackgroundResource(R.drawable.icon_star_line);
                star5.setBackgroundResource(R.drawable.icon_star_line);
            }else if (reviewBrief.listOfReviews.get(i).rate > 2.5 && reviewBrief.listOfReviews.get(i).rate <= 3.0){
                star1.setBackgroundResource(R.drawable.icon_star_full);
                star2.setBackgroundResource(R.drawable.icon_star_full);
                star3.setBackgroundResource(R.drawable.icon_star_full);
                star4.setBackgroundResource(R.drawable.icon_star_line);
                star5.setBackgroundResource(R.drawable.icon_star_line);
            }else if (reviewBrief.listOfReviews.get(i).rate > 3.0 && reviewBrief.listOfReviews.get(i).rate <= 3.5){
                star1.setBackgroundResource(R.drawable.icon_star_full);
                star2.setBackgroundResource(R.drawable.icon_star_full);
                star3.setBackgroundResource(R.drawable.icon_star_full);
                star4.setBackgroundResource(R.drawable.icon_star_half);
                star5.setBackgroundResource(R.drawable.icon_star_line);
            }else if (reviewBrief.listOfReviews.get(i).rate > 3.5 && reviewBrief.listOfReviews.get(i).rate <= 4.0){
                star1.setBackgroundResource(R.drawable.icon_star_full);
                star2.setBackgroundResource(R.drawable.icon_star_full);
                star3.setBackgroundResource(R.drawable.icon_star_full);
                star4.setBackgroundResource(R.drawable.icon_star_full);
                star5.setBackgroundResource(R.drawable.icon_star_line);
            }else if (reviewBrief.listOfReviews.get(i).rate > 4.0 && reviewBrief.listOfReviews.get(i).rate <= 4.5){
                star1.setBackgroundResource(R.drawable.icon_star_full);
                star2.setBackgroundResource(R.drawable.icon_star_full);
                star3.setBackgroundResource(R.drawable.icon_star_full);
                star4.setBackgroundResource(R.drawable.icon_star_full);
                star5.setBackgroundResource(R.drawable.icon_star_half);
            }else if (reviewBrief.listOfReviews.get(i).rate >4.5 && reviewBrief.listOfReviews.get(i).rate <= 5.0){
                star1.setBackgroundResource(R.drawable.icon_star_full);
                star2.setBackgroundResource(R.drawable.icon_star_full);
                star3.setBackgroundResource(R.drawable.icon_star_full);
                star4.setBackgroundResource(R.drawable.icon_star_full);
                star5.setBackgroundResource(R.drawable.icon_star_full);
            }

            layout_reviews.addView(mView);

        }


    }
}
