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
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import larc.ludiconprod.Activities.ActivitiesActivity;
import larc.ludiconprod.Activities.UserProfileActivity;
import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.Event;
import larc.ludiconprod.Utils.General;
import larc.ludiconprod.Utils.util.Sponsors;
import larc.ludiconprod.Utils.util.Sport;

public class PastEventsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int scrollMax;
    private int scrollPos = 0;
    private Timer scrollTimer = null;
    private TimerTask scrollerSchedule;
    Boolean right = true;
    private final String MY_LANGUAGE = "LANGUAGE";
    private final String LANGUAGE_KEY = "com.example.ludicon.language";

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static String getMonth(int month) {
        String date = new DateFormatSymbols().getMonths()[month - 1];
        return date.substring(0, 1).toUpperCase().concat(date.substring(1, 3));
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profileImage;
        TextView creatorName;
        TextView sportName;
        TextView sportLabel;
        TextView ludicoinsNumber;
        TextView pointsNumber;
        TextView eventDate;
        TextView playerLabel;
        TextView perHourLabel;
        TextView dateLabel;
        TextView youGainLabel;
        TextView locationLabel;
        TextView locationEvent;
        TextView playersNumber;
        CircleImageView friends0;
        CircleImageView friends1;
        CircleImageView friends2;
        TextView friendsNumber;
        ImageView imageViewBackground;
        TextView creatorLevelAroundMe;
        ProgressBar progressBar;
        View view;
        RelativeLayout friendsNumberLayout;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            this.youGainLabel = (TextView) view.findViewById(R.id.youGainPE);
            this.dateLabel = (TextView) view.findViewById(R.id.datePE);
            this.playerLabel = (TextView) view.findViewById(R.id.playersPE);
            this.perHourLabel = (TextView) view.findViewById(R.id.perHourPE);
            this.locationLabel = (TextView) view.findViewById(R.id.locationPE);
            this.profileImage = (CircleImageView) view.findViewById(R.id.profileImagePE);
            this.creatorName = (TextView) view.findViewById(R.id.creatorNamePE);
            this.sportName = (TextView) view.findViewById(R.id.sportNamePE);
            this.sportLabel = (TextView) view.findViewById(R.id.sportNameLabelPE);
            this.ludicoinsNumber = (TextView) view.findViewById(R.id.ludicoinsNumberPE);
            this.pointsNumber = (TextView) view.findViewById(R.id.pointsNumberPE);
            this.eventDate = (TextView) view.findViewById(R.id.eventDatePE);
            this.locationEvent = (TextView) view.findViewById(R.id.locationEventPE);
            this.playersNumber = (TextView) view.findViewById(R.id.playersNumberPE);
            this.friends0 = (CircleImageView) view.findViewById(R.id.friends0PE);
            this.friends1 = (CircleImageView) view.findViewById(R.id.friends1PE);
            this.friends2 = (CircleImageView) view.findViewById(R.id.friends2PE);
            this.friendsNumber = (TextView) view.findViewById(R.id.friendsNumberPE);
            this.imageViewBackground = (ImageView) view.findViewById(R.id.imageViewBackgroundPE);
            this.creatorLevelAroundMe = (TextView) view.findViewById(R.id.creatorLevelMyActivityPE);
            this.progressBar = (ProgressBar) view.findViewById(R.id.cardProgressBarPE);
            this.friendsNumberLayout = (RelativeLayout) view.findViewById(R.id.friendsNumberLayoutPE);
            this.progressBar.setAlpha(0);

            Typeface typeFace = Typeface.createFromAsset(activity.getAssets(), "fonts/Quicksand-Medium.ttf");
            Typeface typeFaceBold = Typeface.createFromAsset(activity.getAssets(), "fonts/Quicksand-Bold.ttf");

            this.creatorName.setTypeface(typeFace);
            this.sportName.setTypeface(typeFace);
            this.sportLabel.setTypeface(typeFace);
            this.ludicoinsNumber.setTypeface(typeFace);
            this.pointsNumber.setTypeface(typeFace);
            this.eventDate.setTypeface(typeFace);
            this.youGainLabel.setTypeface(typeFace);
            this.perHourLabel.setTypeface(typeFace);
            this.playerLabel.setTypeface(typeFace);
            this.dateLabel.setTypeface(typeFace);
            this.locationLabel.setTypeface(typeFace);
            this.locationEvent.setTypeface(typeFace);
            this.playersNumber.setTypeface(typeFace);
            this.friendsNumber.setTypeface(typeFace);
            this.creatorLevelAroundMe.setTypeface(typeFace);


            // Clean up layout
            this.friends0.setVisibility(View.INVISIBLE);
            this.friends1.setVisibility(View.INVISIBLE);
            this.friends2.setVisibility(View.INVISIBLE);
            this.friendsNumber.setVisibility(View.INVISIBLE);
            this.friendsNumberLayout.setVisibility(View.INVISIBLE);
            this.profileImage.setImageResource(R.drawable.ph_user);
            this.friends0.setImageResource(R.drawable.ph_user);
            this.friends1.setImageResource(R.drawable.ph_user);
            this.friends2.setImageResource(R.drawable.ph_user);


            // Set name and picture for the first user of the event
            //view.setBackgroundColor(Color.parseColor("#FFFFFF"));

            final View currView = view;


        }
    }

    public class ViewHolderSponsors extends RecyclerView.ViewHolder {

        public LinearLayout sponsors;
        public HorizontalScrollView scrollView;
        View view;

        public ViewHolderSponsors(View view) {
            super(view);
            this.view = view;
            this.sponsors = (LinearLayout) view.findViewById(R.id.sponsors);
            this.scrollView = (HorizontalScrollView) view.findViewById(R.id.scrollView);


            // Set name and picture for the first user of the event
            //view.setBackgroundColor(Color.parseColor("#FFFFFF"));

            final View currView = view;


        }

    }

    private ArrayList<Event> list = new ArrayList<>();
    private ArrayList<Sponsors> sponsorsList = new ArrayList<>();
    private Context context;
    private Activity activity;
    private Resources resources;
    private ActivitiesActivity fragment;
    final RecyclerView listView;
    public static ProgressBar progressBarCard;

    public PastEventsAdapter(ArrayList<Event> list, ArrayList<Sponsors> sponsorList, Context context, Activity activity, Resources resources, ActivitiesActivity fragment) {
        this.list = list;
        this.context = context;
        this.activity = activity;
        this.resources = resources;
        this.fragment = fragment;
        this.sponsorsList = sponsorList;

        this.listView = (RecyclerView) activity.findViewById(R.id.events_listView3); // era v.
    }

    public PastEventsAdapter(ArrayList<Event> list, ArrayList<Sponsors> sponsorList, Context context, Activity activity, Resources resources) {
        this.list = list;
        this.context = context;
        this.activity = activity;
        this.resources = resources;
        this.sponsorsList = sponsorList;
        this.listView = (RecyclerView) activity.findViewById(R.id.events_listView3); // era v.
    }

    public void setListOfEvents(ArrayList<Event> newList) {
        this.list = newList;
        this.notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Log.d("MYVIEW", viewType + "");
        switch (viewType) {

            case 0:
                view = inflater.inflate(R.layout.activities_sponsors_card, null);
                viewHolder = new PastEventsAdapter.ViewHolderSponsors(view);
                break;
            case 1:

                view = inflater.inflate(R.layout.past_events_adapter, null);
                viewHolder = new PastEventsAdapter.ViewHolder(view);
                break;

        }
        return viewHolder;
    }

    public void translate(String language, PastEventsAdapter.ViewHolder holder) {
        if (language.equalsIgnoreCase("ro")) {
            holder.youGainLabel.setText(R.string.ro_gained);
            holder.locationLabel.setText(R.string.ro_location);
            holder.dateLabel.setText(R.string.ro_date_and_time);
            holder.playerLabel.setText(R.string.ro_players);
            holder.perHourLabel.setText(R.string.ro_hour);
        } else {
            holder.locationLabel.setText(R.string.en_location);
            holder.dateLabel.setText(R.string.en_date_and_time);
            holder.playerLabel.setText(R.string.en_players);
            holder.perHourLabel.setText(R.string.en_hour);
            holder.youGainLabel.setText(R.string.en_gained);

        }
    }

    public String translateDate(String date) {
        String ro = "";
        Log.d("DEBUG", date);
        String[] words = date.split(" ");
        if (words[0].equalsIgnoreCase("today,"))
            ro = "Azi,";
        else if (words[0].equalsIgnoreCase("tomorrow,"))
            ro = "Maine,";
        else if (words[0].equalsIgnoreCase("monday,"))
            ro = "Luni,";
        else if (words[0].equalsIgnoreCase("tuesday,"))
            ro = "Marti,";
        else if (words[0].equalsIgnoreCase("wednesday,"))
            ro = "Miercuri,";
        else if (words[0].equalsIgnoreCase("thursday,"))
            ro = "Joi,";
        else if (words[0].equalsIgnoreCase("friday,"))
            ro = "Vineri,";
        else if (words[0].equalsIgnoreCase("saturday,"))
            ro = "Sambata,";
        else if (words[0].equalsIgnoreCase("sunday,"))
            ro = "Duminica,";
        else
            ro = words[0];

        if (words.length <= 2) {
            ro = ro + " " + words[1];
        } else {
            ro += " " + words[1] + " " + words[2];
        }

        return ro;

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        // Event details redirect
        // Clean up layout

        switch (holder.getItemViewType()) {
            case 0:
                ((PastEventsAdapter.ViewHolderSponsors) holder).sponsors.removeAllViews();
                for (int i = 0; i < sponsorsList.size(); i++) {
                    int margins = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, activity.getResources().getDisplayMetrics());
                    ImageView imageView = new ImageView(activity);
                    Bitmap bitmap = decodeBase64(sponsorsList.get(i).logo);
                    imageView.setImageBitmap(bitmap);


                    LinearLayout.LayoutParams layoutMargins = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    if (i == sponsorsList.size() - 1) {
                        layoutMargins.setMargins(0, 0, 0, 0);
                    } else {
                        layoutMargins.setMargins(0, 0, margins, 0);
                    }
                    ((PastEventsAdapter.ViewHolderSponsors) holder).sponsors.addView(imageView, layoutMargins);
                    ((PastEventsAdapter.ViewHolderSponsors) holder).scrollView.setOnTouchListener(new View.OnTouchListener() {

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            // TODO Auto-generated method stub
                            return true;
                        }
                    });
                }

                ViewTreeObserver vto = ((PastEventsAdapter.ViewHolderSponsors) holder).sponsors.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        ((PastEventsAdapter.ViewHolderSponsors) holder).sponsors.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        getScrollMaxAmount(((PastEventsAdapter.ViewHolderSponsors) holder).scrollView);
                        startAutoScrolling(((PastEventsAdapter.ViewHolderSponsors) holder).scrollView);
                    }
                });


                break;
            case 1:
                final int pos;
                if (sponsorsList.size() >= 1) {
                    pos = position - 1;

                } else {
                    pos = position;
                }
                ((PastEventsAdapter.ViewHolder) holder).friends0.setVisibility(View.INVISIBLE);
                ((PastEventsAdapter.ViewHolder) holder).friends1.setVisibility(View.INVISIBLE);
                ((PastEventsAdapter.ViewHolder) holder).friends2.setVisibility(View.INVISIBLE);
                ((PastEventsAdapter.ViewHolder) holder).friendsNumber.setVisibility(View.INVISIBLE);
                ((PastEventsAdapter.ViewHolder) holder).friendsNumberLayout.setVisibility(View.INVISIBLE);
                ((PastEventsAdapter.ViewHolder) holder).profileImage.setImageResource(R.drawable.ph_user);
                ((PastEventsAdapter.ViewHolder) holder).friends0.setImageResource(R.drawable.ph_user);
                ((PastEventsAdapter.ViewHolder) holder).friends1.setImageResource(R.drawable.ph_user);
                ((PastEventsAdapter.ViewHolder) holder).friends2.setImageResource(R.drawable.ph_user);
                ((PastEventsAdapter.ViewHolder) holder).view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //getEventDetails & show progress bar when loading data
                        ((PastEventsAdapter.ViewHolder) holder).progressBar.setAlpha(1);
                        progressBarCard = ((PastEventsAdapter.ViewHolder) holder).progressBar;
                        HashMap<String, String> params = new HashMap<String, String>();
                        HashMap<String, String> headers = new HashMap<String, String>();
                        HashMap<String, String> urlParams = new HashMap<String, String>();
                        headers.put("authKey", Persistance.getInstance().getUserInfo(activity).authKey);

                        //set urlParams

                        urlParams.put("eventId", list.get(pos).id);
                        urlParams.put("userId", Persistance.getInstance().getUserInfo(activity).id);
                        HTTPResponseController.getInstance().getEventDetails(params, headers, activity, urlParams);
                    }
                });


                // Set user event creator name and picture
                ((PastEventsAdapter.ViewHolder) holder).creatorName.setText(list.get(pos).creatorName);
                if (!list.get(pos).creatorProfilePicture.equals("")) {
                    Bitmap bitmap = decodeBase64(list.get(pos).creatorProfilePicture);
                    ((PastEventsAdapter.ViewHolder) holder).profileImage.setImageBitmap(bitmap);
                }

                // Card color for Ludicon Bucharest
                /*
                if(list.get(pos).creatorName.compareTo("Ludicon Bucharest") == 0){
                    ((ViewHolder)holder).view.setBackgroundColor(0xFFffdae0);
                }
                else{
                    ((ViewHolder)holder).view.setBackgroundColor(0xFFFFFFFF);
                }
                */

                // Redirect to user profile on picture tap
                ((PastEventsAdapter.ViewHolder) holder).profileImage.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        String id = list.get(pos).creatorId;
                        if (Persistance.getInstance().getUserInfo(activity).id.equals(id)) {
                            Toast.makeText(activity, "It's you :)", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Intent intent = new Intent(activity, UserProfileActivity.class);
                        intent.putExtra("UserId", list.get(pos).creatorId);
                        activity.startActivity(intent);
                    }
                });


                // Event details set message for sport played
                String language = activity.getSharedPreferences(MY_LANGUAGE, Context.MODE_PRIVATE).getString(LANGUAGE_KEY, "en");
                Sport sport = new Sport(list.get(pos).sportCode, language);
                String weWillPlayString = "";
                String sportName = "";

                if (list.get(pos).sportCode.equalsIgnoreCase("JOG") || list.get(pos).sportCode.equalsIgnoreCase("GYM") || list.get(position).sportCode.equalsIgnoreCase("CYC")) {
                    if (language.equalsIgnoreCase("ro"))
                        weWillPlayString = activity.getResources().getString(R.string.ro_went);
                    else
                        weWillPlayString = activity.getResources().getString(R.string.en_went);
                    sportName = sport.sportName;
                } else {
                    if (list.get(pos).sportCode.equalsIgnoreCase("OTH")) {
                        sportName = list.get(pos).otherSportName;
                    } else {
                        sportName = sport.sportName;
                    }
                    if (language.equalsIgnoreCase("ro"))
                        weWillPlayString = activity.getResources().getString(R.string.ro_played);
                    else
                        weWillPlayString = activity.getResources().getString(R.string.en_played);
                }

                weWillPlayString += " ";

                sportName = sportName.substring(0, 1).toUpperCase() + sportName.substring(1);

                ((PastEventsAdapter.ViewHolder) holder).sportLabel.setText(weWillPlayString);
                ((PastEventsAdapter.ViewHolder) holder).sportName.setText(sportName);


                ((PastEventsAdapter.ViewHolder) holder).ludicoinsNumber.setText("  +" + String.valueOf(list.get(pos).ludicoins));
                ((PastEventsAdapter.ViewHolder) holder).pointsNumber.setText("  +" + String.valueOf(list.get(pos).points));


                ((PastEventsAdapter.ViewHolder) holder).locationEvent.setText(list.get(pos).placeName);
                ((PastEventsAdapter.ViewHolder) holder).playersNumber.setText(list.get(pos).numberOfParticipants + "");
                if (list.get(pos).numberOfParticipants - 1 >= 1) {
                    ((PastEventsAdapter.ViewHolder) holder).friends0.setVisibility(View.VISIBLE);
                }
                if (list.get(pos).numberOfParticipants - 1 >= 2) {
                    ((PastEventsAdapter.ViewHolder) holder).friends1.setVisibility(View.VISIBLE);
                }
                if (list.get(pos).numberOfParticipants - 1 >= 3) {
                    ((PastEventsAdapter.ViewHolder) holder).friends2.setVisibility(View.VISIBLE);
                }
                if (list.get(pos).numberOfParticipants - 1 >= 4) {
                    ((PastEventsAdapter.ViewHolder) holder).friendsNumber.setVisibility(View.VISIBLE);
                    ((PastEventsAdapter.ViewHolder) holder).friendsNumberLayout.setVisibility(View.VISIBLE);
                    ((PastEventsAdapter.ViewHolder) holder).friendsNumber.setText("+" + String.valueOf(list.get(pos).numberOfParticipants - 4));

                }
                for (int i = 0; i < list.get(pos).participansProfilePicture.size(); i++) {
                    if (!list.get(pos).participansProfilePicture.get(i).equals("") && i == 0) {
                        Bitmap bitmap = decodeBase64(list.get(pos).participansProfilePicture.get(i));
                        ((PastEventsAdapter.ViewHolder) holder).friends0.setImageBitmap(bitmap);
                    } else if (!list.get(pos).participansProfilePicture.get(i).equals("") && i == 1) {
                        Bitmap bitmap = decodeBase64(list.get(pos).participansProfilePicture.get(i));
                        ((PastEventsAdapter.ViewHolder) holder).friends1.setImageBitmap(bitmap);
                    } else if (!list.get(pos).participansProfilePicture.get(i).equals("") && i == 2) {
                        Bitmap bitmap = decodeBase64(list.get(pos).participansProfilePicture.get(i));
                        ((PastEventsAdapter.ViewHolder) holder).friends2.setImageBitmap(bitmap);
                    }
                }

                switch (list.get(pos).sportCode) {
                    case "FOT":
                        ((PastEventsAdapter.ViewHolder) holder).imageViewBackground.setBackgroundResource(R.drawable.bg_sport_football);
                        break;
                    case "BAS":
                        ((PastEventsAdapter.ViewHolder) holder).imageViewBackground.setBackgroundResource(R.drawable.bg_sport_basketball);
                        break;
                    case "VOL":
                        ((PastEventsAdapter.ViewHolder) holder).imageViewBackground.setBackgroundResource(R.drawable.bg_sport_volleyball);
                        break;
                    case "JOG":
                        ((PastEventsAdapter.ViewHolder) holder).imageViewBackground.setBackgroundResource(R.drawable.bg_sport_jogging);
                        break;
                    case "GYM":
                        ((PastEventsAdapter.ViewHolder) holder).imageViewBackground.setBackgroundResource(R.drawable.bg_sport_gym);
                        break;
                    case "CYC":
                        ((PastEventsAdapter.ViewHolder) holder).imageViewBackground.setBackgroundResource(R.drawable.bg_sport_cycling);
                        break;
                    case "TEN":
                        ((PastEventsAdapter.ViewHolder) holder).imageViewBackground.setBackgroundResource(R.drawable.bg_sport_tennis);
                        break;
                    case "PIN":
                        ((PastEventsAdapter.ViewHolder) holder).imageViewBackground.setBackgroundResource(R.drawable.bg_sport_pingpong);
                        break;
                    case "SQU":
                        ((PastEventsAdapter.ViewHolder) holder).imageViewBackground.setBackgroundResource(R.drawable.bg_sport_squash);
                        break;
                    case "OTH":
                        ((PastEventsAdapter.ViewHolder) holder).imageViewBackground.setBackgroundResource(R.drawable.bg_sport_others);
                        break;
                }

                ((PastEventsAdapter.ViewHolder) holder).creatorLevelAroundMe.setText(String.valueOf(list.get(pos).creatorLevel));

                // Event details set message for date and time
                Calendar c = Calendar.getInstance();
                Date today = c.getTime();
                int todayDay = General.getDayOfMonth(today);
                int todayMonth = today.getMonth();
                int todayYear = c.get(Calendar.YEAR);
                String displayDate = "";
                String[] stringDateAndTime = list.get(pos).eventDate.split(" ");
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    displayDate = formatter.format(formatter.parse(stringDateAndTime[0]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String[] stringDate = displayDate.split("-");
                int year = Integer.parseInt(stringDate[0]);
                String date = "";
                if (year <= todayYear && Integer.parseInt(stringDate[1]) - 1 == todayMonth && Integer.parseInt(stringDate[2]) == todayDay) {
                    date = "Today, " + stringDateAndTime[1].substring(0, 5);
                } else if (year <= todayYear && Integer.parseInt(stringDate[1]) - 1 == todayMonth && Integer.parseInt(stringDate[2]) - 1 == todayDay) {
                    date = "Tomorrow, " + stringDateAndTime[1].substring(0, 5);
                } else if (year <= todayYear) {
                    date = getMonth(Integer.parseInt(stringDate[1])) + " " + stringDate[2] + ", " + stringDateAndTime[1].substring(0, 5);
                } else {
                    date = stringDate[2] + " " + getMonth(Integer.parseInt(stringDate[1])) + " " + year + ", " + stringDateAndTime[1].substring(0, 5);
                }

                if (language.equalsIgnoreCase("ro"))
                    date = translateDate(date);

                translate(language, (PastEventsAdapter.ViewHolder) holder);

                ((PastEventsAdapter.ViewHolder) holder).eventDate.setText(date);

                System.out.println(list.get(pos).id + " eventid:" + pos + "  " + list.get(pos).numberOfParticipants + " profilepicture" + list.get(position).participansProfilePicture.size());
                break;
        }
    }


    @Override
    public long getItemId(int pos) {
        return Long.valueOf(list.get(pos).id);
    }

    @Override
    public int getItemCount() {
        Log.d("All PAST EVENTS", list.size() + "");
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        if (position == 0 && sponsorsList.size() >= 1) {
            return 0;
        } else {
            return 1;
        }
    }

    public void getScrollMaxAmount(HorizontalScrollView horizontalScrollView) {
        int actualWidth = (horizontalScrollView.getMaxScrollAmount());

        scrollMax = actualWidth;
    }

    public void startAutoScrolling(final HorizontalScrollView horizontalScrollView) {
        if (scrollTimer == null) {
            scrollTimer = new Timer();
            final Runnable Timer_Tick = new Runnable() {
                public void run() {
                    moveScrollView(horizontalScrollView);
                }
            };

            if (scrollerSchedule != null) {
                scrollerSchedule.cancel();
                scrollerSchedule = null;
            }
            scrollerSchedule = new TimerTask() {
                @Override
                public void run() {
                    activity.runOnUiThread(Timer_Tick);
                }
            };

            scrollTimer.schedule(scrollerSchedule, 30, 30);
        }
    }

    public void moveScrollView(HorizontalScrollView horizontalScrollView) {

        if (scrollPos < scrollMax && right) {
            scrollPos = (int) (horizontalScrollView.getScrollX() + 1.0);

        }
        if (scrollPos > 0 && !right) {
            scrollPos = (int) (horizontalScrollView.getScrollX() - 1.0);

        }
        horizontalScrollView.scrollTo(scrollPos, 0);
        if (scrollPos == scrollMax) {
            right = false;
        }
        if (scrollPos == 0) {
            right = true;
        }

    }

}
