package larc.ludiconprod.Adapters.MainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import larc.ludiconprod.Activities.ActivitiesActivity;
import larc.ludiconprod.Activities.FullHistory;
import larc.ludiconprod.Activities.FullPageView;
import larc.ludiconprod.Activities.Pop;
import larc.ludiconprod.Activities.UserProfileActivity;
import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.Event;
import larc.ludiconprod.Utils.EventBrief;
import larc.ludiconprod.Utils.General;
import larc.ludiconprod.Utils.Review;
import larc.ludiconprod.Utils.util.ReviewBrief;
import larc.ludiconprod.Utils.util.Sponsors;
import larc.ludiconprod.Utils.util.Sport;

public class FullReviewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int Type_HEAD = 0;
    private static final int Type_LIST =1;

    private int scrollMax;
    private int scrollPos =	0;
    private Timer scrollTimer	=	null;
    private TimerTask scrollerSchedule;
    Boolean right=true;

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static String getMonth(int month) {
        String date = new DateFormatSymbols().getMonths()[month - 1];
        return date.substring(0, 1).toUpperCase().concat(date.substring(1, 3));
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userImageProfile;
        TextView userName;
        TextView date;
        TextView review;
        RelativeLayout stars;
        ImageView star1;
        ImageView star2;
        ImageView star3;
        ImageView star4;
        ImageView star5;

        int view_type;
        TextView rating;
        TextView countReview;
        RelativeLayout socialReviews;
        ProgressBar progressBar1;
        ProgressBar progressBar2;
        ProgressBar progressBar3;
        ProgressBar progressBar4;
        ProgressBar progressBar5;

        public ViewHolder(View view, int viewType) {
            super(view);

            if (viewType == Type_LIST) {
                this.userImageProfile = (CircleImageView) view.findViewById(R.id.userImageProfile);
                this.userName = (TextView) view.findViewById(R.id.userName);
                this.date = (TextView) view.findViewById(R.id.date);
                this.review = (TextView) view.findViewById(R.id.review);
                this.stars = (RelativeLayout) view.findViewById(R.id.stars);
                this.star1 = (ImageView) view.findViewById(R.id.star1);
                this.star2 = (ImageView) view.findViewById(R.id.star2);
                this.star3 = (ImageView) view.findViewById(R.id.star3);
                this.star4 = (ImageView) view.findViewById(R.id.star4);
                this.star5 = (ImageView) view.findViewById(R.id.star5);

                Typeface typeFace = Typeface.createFromAsset(activity.getAssets(), "fonts/Quicksand-Medium.ttf");

                this.userName.setTypeface(typeFace);
                this.date.setTypeface(typeFace);
                this.review.setTypeface(typeFace);
                view_type = 1;
            }else if (viewType == Type_HEAD){
                this.rating = (TextView) view.findViewById(R.id.rating);
                this.countReview = (TextView) view.findViewById(R.id.countReview);
                this.socialReviews = (RelativeLayout) view.findViewById(R.id.socialReviews);
                this.progressBar1 = (ProgressBar) view.findViewById(R.id.progressBar1);
                this.progressBar2 = (ProgressBar) view.findViewById(R.id.progressBar2);
                this.progressBar3 = (ProgressBar) view.findViewById(R.id.progressBar3);
                this.progressBar4 = (ProgressBar) view.findViewById(R.id.progressBar4);
                this.progressBar5 = (ProgressBar) view.findViewById(R.id.progressBar5);
                view_type = 0;
            }

            final View currView = view;


        }
    }

    private ArrayList<ReviewBrief> list = new ArrayList<>();
    private Context context;
    private Activity activity;
    private Resources resources;
    private FullPageView fragment;
    final RecyclerView listView;
    public static ProgressBar progressBarCard;

    public FullReviewsAdapter(ArrayList<ReviewBrief> list, Context context, Activity activity, Resources resources, FullPageView fragment) {
        this.list = list;
        this.context = context;
        this.activity = activity;
        this.resources = resources;
        this.fragment = fragment;

        this.listView = (RecyclerView) activity.findViewById(R.id.events_listViewPastEvents); // era v.
    }

    public FullReviewsAdapter(ArrayList<ReviewBrief> list, Context context, Activity activity, Resources resources) {
        this.list = list;
        this.context = context;
        this.activity = activity;
        this.resources = resources;
        this.listView = (RecyclerView) activity.findViewById(R.id.events_listViewPastEvents); // era v.

    }

    public void setListOfEvents(ArrayList<ReviewBrief> newList) {
        this.list = newList;
        this.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;

            if (viewType == Type_LIST) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review, parent, false);
                viewHolder = new FullReviewsAdapter.ViewHolder(view, viewType);
                return viewHolder;
            } else if (viewType == Type_HEAD) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_reviews, parent, false);
                viewHolder = new FullReviewsAdapter.ViewHolder(view, viewType);
                return viewHolder;
            }

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (((ViewHolder) holder).view_type == Type_LIST) {

            final ReviewBrief currentEvent = list.get(position-1);

            ((ViewHolder) holder).userName.setText(currentEvent.userName);

            ((ViewHolder) holder).review.setText("\"" + currentEvent.reviewPreview + "\"");

            Long date1 = Long.parseLong(currentEvent.reviewDate, 10);
            DateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date1 * 1000);
            String dateStr = formatter.format(calendar.getTime());
            ((ViewHolder) holder).date.setText(dateStr);

            Bitmap bitmap = decodeBase64(currentEvent.userPicture);
            ((ViewHolder) holder).userImageProfile.setImageBitmap(bitmap);

            ((ViewHolder) holder).star1.setBackgroundResource(R.drawable.icon_star_full);
            ((ViewHolder) holder).star2.setBackgroundResource(R.drawable.icon_star_full);
            ((ViewHolder) holder).star3.setBackgroundResource(R.drawable.icon_star_full);
            ((ViewHolder) holder).star4.setBackgroundResource(R.drawable.icon_star_full);
            ((ViewHolder) holder).star5.setBackgroundResource(R.drawable.icon_star_full);

            if (Double.valueOf(currentEvent.socialRateReview) == 1.0) {
                ((ViewHolder) holder).star1.setBackgroundResource(R.drawable.icon_star_full);
                ((ViewHolder) holder).star2.setBackgroundResource(R.drawable.icon_star_line);
                ((ViewHolder) holder).star3.setBackgroundResource(R.drawable.icon_star_line);
                ((ViewHolder) holder).star4.setBackgroundResource(R.drawable.icon_star_line);
                ((ViewHolder) holder).star5.setBackgroundResource(R.drawable.icon_star_line);
            } else if (Double.valueOf(currentEvent.socialRateReview) > 1.0 && Double.valueOf(currentEvent.socialRateReview) <= 1.5) {
                ((ViewHolder) holder).star1.setBackgroundResource(R.drawable.icon_star_full);
                ((ViewHolder) holder).star2.setBackgroundResource(R.drawable.icon_star_half);
                ((ViewHolder) holder).star3.setBackgroundResource(R.drawable.icon_star_line);
                ((ViewHolder) holder).star4.setBackgroundResource(R.drawable.icon_star_line);
                ((ViewHolder) holder).star5.setBackgroundResource(R.drawable.icon_star_line);
            } else if (Double.valueOf(currentEvent.socialRateReview) > 1.5 && Double.valueOf(currentEvent.socialRateReview) <= 2.0) {
                ((ViewHolder) holder).star1.setBackgroundResource(R.drawable.icon_star_full);
                ((ViewHolder) holder).star2.setBackgroundResource(R.drawable.icon_star_full);
                ((ViewHolder) holder).star3.setBackgroundResource(R.drawable.icon_star_line);
                ((ViewHolder) holder).star4.setBackgroundResource(R.drawable.icon_star_line);
                ((ViewHolder) holder).star5.setBackgroundResource(R.drawable.icon_star_line);
            } else if (Double.valueOf(currentEvent.socialRateReview) > 2.0 && Double.valueOf(currentEvent.socialRateReview) <= 2.5) {
                ((ViewHolder) holder).star1.setBackgroundResource(R.drawable.icon_star_full);
                ((ViewHolder) holder).star2.setBackgroundResource(R.drawable.icon_star_full);
                ((ViewHolder) holder).star3.setBackgroundResource(R.drawable.icon_star_half);
                ((ViewHolder) holder).star4.setBackgroundResource(R.drawable.icon_star_line);
                ((ViewHolder) holder).star5.setBackgroundResource(R.drawable.icon_star_line);
            } else if (Double.valueOf(currentEvent.socialRateReview) > 2.5 && Double.valueOf(currentEvent.socialRateReview) <= 3.0) {
                ((ViewHolder) holder).star1.setBackgroundResource(R.drawable.icon_star_full);
                ((ViewHolder) holder).star2.setBackgroundResource(R.drawable.icon_star_full);
                ((ViewHolder) holder).star3.setBackgroundResource(R.drawable.icon_star_full);
                ((ViewHolder) holder).star4.setBackgroundResource(R.drawable.icon_star_line);
                ((ViewHolder) holder).star5.setBackgroundResource(R.drawable.icon_star_line);
            } else if (Double.valueOf(currentEvent.socialRateReview) > 3.0 && Double.valueOf(currentEvent.socialRateReview) <= 3.5) {
                ((ViewHolder) holder).star1.setBackgroundResource(R.drawable.icon_star_full);
                ((ViewHolder) holder).star2.setBackgroundResource(R.drawable.icon_star_full);
                ((ViewHolder) holder).star3.setBackgroundResource(R.drawable.icon_star_full);
                ((ViewHolder) holder).star4.setBackgroundResource(R.drawable.icon_star_half);
                ((ViewHolder) holder).star5.setBackgroundResource(R.drawable.icon_star_line);
            } else if (Double.valueOf(currentEvent.socialRateReview) > 3.5 && Double.valueOf(currentEvent.socialRateReview) <= 4.0) {
                ((ViewHolder) holder).star1.setBackgroundResource(R.drawable.icon_star_full);
                ((ViewHolder) holder).star2.setBackgroundResource(R.drawable.icon_star_full);
                ((ViewHolder) holder).star3.setBackgroundResource(R.drawable.icon_star_full);
                ((ViewHolder) holder).star4.setBackgroundResource(R.drawable.icon_star_full);
                ((ViewHolder) holder).star5.setBackgroundResource(R.drawable.icon_star_line);
            } else if (Double.valueOf(currentEvent.socialRateReview) > 4.0 && Double.valueOf(currentEvent.socialRateReview) <= 4.5) {
                ((ViewHolder) holder).star1.setBackgroundResource(R.drawable.icon_star_full);
                ((ViewHolder) holder).star2.setBackgroundResource(R.drawable.icon_star_full);
                ((ViewHolder) holder).star3.setBackgroundResource(R.drawable.icon_star_full);
                ((ViewHolder) holder).star4.setBackgroundResource(R.drawable.icon_star_full);
                ((ViewHolder) holder).star5.setBackgroundResource(R.drawable.icon_star_half);
            } else if (Double.valueOf(currentEvent.socialRateReview) > 4.5 && Double.valueOf(currentEvent.socialRateReview) <= 5.0) {
                ((ViewHolder) holder).star1.setBackgroundResource(R.drawable.icon_star_full);
                ((ViewHolder) holder).star2.setBackgroundResource(R.drawable.icon_star_full);
                ((ViewHolder) holder).star3.setBackgroundResource(R.drawable.icon_star_full);
                ((ViewHolder) holder).star4.setBackgroundResource(R.drawable.icon_star_full);
                ((ViewHolder) holder).star5.setBackgroundResource(R.drawable.icon_star_full);
            }

        } else if (((ViewHolder) holder).view_type == Type_HEAD) {
            DecimalFormat df = new DecimalFormat("#.00");
            ((ViewHolder) holder).rating.setText(df.format(Double.valueOf(HTTPResponseController.review.socialRate)));
            ((ViewHolder) holder).countReview.setText(activity.getResources().getString(R.string.based_on) + " " + HTTPResponseController.review.countSocialRate + " " + activity.getResources().getString(R.string.reviews));

            ((ViewHolder) holder).progressBar1.setProgress(HTTPResponseController.review.excellentPerc);
            ((ViewHolder) holder).progressBar2.setProgress(HTTPResponseController.review.goodPerc);
            ((ViewHolder) holder).progressBar3.setProgress(HTTPResponseController.review.averagePerc);
            ((ViewHolder) holder).progressBar4.setProgress(HTTPResponseController.review.bellowAveragePerc);
            ((ViewHolder) holder).progressBar5.setProgress(HTTPResponseController.review.poorPerc);
        }

    }
    @Override
    public long getItemId(int pos) {
        return Long.valueOf(list.get(pos).id);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0)
        return Type_HEAD;
        return Type_LIST;

    }
    public void getScrollMaxAmount(HorizontalScrollView horizontalScrollView){
        int actualWidth = (horizontalScrollView.getMaxScrollAmount());

        scrollMax   = actualWidth;
    }

    public void startAutoScrolling(final HorizontalScrollView horizontalScrollView){
        if (scrollTimer == null) {
            scrollTimer					=	new Timer();
            final Runnable Timer_Tick 	= 	new Runnable() {
                public void run() {
                    moveScrollView(horizontalScrollView);
                }
            };

            if(scrollerSchedule != null){
                scrollerSchedule.cancel();
                scrollerSchedule = null;
            }
            scrollerSchedule = new TimerTask(){
                @Override
                public void run(){
                    activity.runOnUiThread(Timer_Tick);
                }
            };

            scrollTimer.schedule(scrollerSchedule, 30, 30);
        }
    }

    public void moveScrollView(HorizontalScrollView horizontalScrollView){

        if(scrollPos < scrollMax && right){
            scrollPos = (int) (horizontalScrollView.getScrollX() + 1.0);

        }
        if(scrollPos > 0 && !right){
            scrollPos = (int) (horizontalScrollView.getScrollX() - 1.0);

        }
        horizontalScrollView.scrollTo(scrollPos, 0);
        if(scrollPos == scrollMax){
            right =false;
        }
        if(scrollPos == 0){
            right =true;
        }

    }

}
