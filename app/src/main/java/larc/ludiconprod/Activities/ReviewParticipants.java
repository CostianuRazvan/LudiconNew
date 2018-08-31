package larc.ludiconprod.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.EventDetails;
import larc.ludiconprod.Utils.Friend;

public class ReviewParticipants extends Activity {

    Button skip;
    Button submit;
    TextView text;
    TextView userName;
    CircleImageView userImageProfile;
    int id_layout = 987;

    LinearLayout layout_user;

    ArrayList<String> rateList = new ArrayList<>();
    ArrayList<EditText> reviewList = new ArrayList<>();


    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_participants);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        double width = dm.widthPixels;
        double height = dm.heightPixels;

        getWindow().setLayout((int) (width * .9), (int) (height * .7));

        skip = (Button) findViewById(R.id.skip);
        submit = (Button) findViewById(R.id.submit);


        ArrayList<String> participantsName = (ArrayList<String>) getIntent().getSerializableExtra("participantsNameList");
        ArrayList<String> participantsImage = (ArrayList<String>) getIntent().getSerializableExtra("participantsImageList");
        final ArrayList<String> participantsId = (ArrayList<String>) getIntent().getSerializableExtra("userIdList");

        layout_user = (LinearLayout) findViewById(R.id.layout_user);

        int star_id = 456;

        for (int i = 0; i < participantsName.size() - 1; i++) {

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LayoutInflater vi = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            params1.setMargins(70, 0, 70, 70);

            LinearLayout mView = (LinearLayout) vi.inflate(R.layout.user_review, layout_user, false);
            mView.setId(id_layout + i);
            if (i != 0) {
                params.addRule(RelativeLayout.BELOW, mView.getId() - 1);
            }
            mView.setLayoutParams(params);
            mView.setLayoutParams(params1);

            final ImageView stars1Entry = (ImageView) mView.findViewById(R.id.starsUser1);
            final ImageView stars2Entry = (ImageView) mView.findViewById(R.id.starsUser2);
            final ImageView stars3Entry = (ImageView) mView.findViewById(R.id.starsUser3);
            final ImageView stars4Entry = (ImageView) mView.findViewById(R.id.starsUser4);
            final ImageView stars5Entry = (ImageView) mView.findViewById(R.id.starsUser5);

            userName = (TextView) mView.findViewById(R.id.userName);
            userImageProfile = (CircleImageView) mView.findViewById(R.id.userImageProfile);
            EditText review = (EditText) mView.findViewById(R.id.review);

            stars1Entry.setBackgroundResource(R.drawable.icon_star_full);
            stars2Entry.setBackgroundResource(R.drawable.icon_star_full);
            stars3Entry.setBackgroundResource(R.drawable.icon_star_full);
            stars4Entry.setBackgroundResource(R.drawable.icon_star_full);
            stars5Entry.setBackgroundResource(R.drawable.icon_star_full);

            if (!Persistance.getInstance().getUserInfo(ReviewParticipants.this).id.equals(participantsName.get(i).toString())) {
                userName.setText(participantsName.get(i).toString());
            }
            if (!Persistance.getInstance().getUserInfo(ReviewParticipants.this).profileImage.equals(participantsImage.get(i).toString())) {
                Bitmap bitmap = decodeBase64(participantsImage.get(i).toString());
                userImageProfile.setImageBitmap(bitmap);
            }

            layout_user.addView(mView);
            reviewList.add(review);

            stars1Entry.setTag(i);
            stars2Entry.setTag(i);
            stars3Entry.setTag(i);
            stars4Entry.setTag(i);
            stars5Entry.setTag(i);
            rateList.add("5.0");

            stars1Entry.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        float screenX = event.getX();
                        float viewX = v.getRight() - v.getLeft();

                        stars1Entry.setBackgroundResource(R.drawable.icon_star_full);
                        stars2Entry.setBackgroundResource(R.drawable.icon_star_line);
                        stars3Entry.setBackgroundResource(R.drawable.icon_star_line);
                        stars4Entry.setBackgroundResource(R.drawable.icon_star_line);
                        stars5Entry.setBackgroundResource(R.drawable.icon_star_line);
                        rateList.set((Integer) v.getTag(), "1.0");
                    }
                    return true;
                }
            });

            stars2Entry.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        float screenX = event.getX();
                        float viewX = v.getRight() - v.getLeft();

                        stars1Entry.setBackgroundResource(R.drawable.icon_star_full);
                        if (screenX <= viewX / 2) {
                            stars2Entry.setBackgroundResource(R.drawable.icon_star_half);
                            rateList.set((Integer) v.getTag(), "1.5");
                        } else {
                            stars2Entry.setBackgroundResource(R.drawable.icon_star_full);
                            rateList.set((Integer) v.getTag(), "2.0");
                        }
                        stars3Entry.setBackgroundResource(R.drawable.icon_star_line);
                        stars4Entry.setBackgroundResource(R.drawable.icon_star_line);
                        stars5Entry.setBackgroundResource(R.drawable.icon_star_line);
                    }
                    return true;
                }
            });

            stars3Entry.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        float screenX = event.getX();
                        float viewX = v.getRight() - v.getLeft();

                        stars1Entry.setBackgroundResource(R.drawable.icon_star_full);
                        stars2Entry.setBackgroundResource(R.drawable.icon_star_full);
                        if (screenX <= viewX / 2) {
                            stars3Entry.setBackgroundResource(R.drawable.icon_star_half);
                            rateList.set((Integer) v.getTag(), "2.5");
                        } else {
                            stars3Entry.setBackgroundResource(R.drawable.icon_star_full);
                            rateList.set((Integer) v.getTag(), "3.0");
                        }
                        stars4Entry.setBackgroundResource(R.drawable.icon_star_line);
                        stars5Entry.setBackgroundResource(R.drawable.icon_star_line);
                    }
                    return true;
                }
            });

            stars4Entry.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        float screenX = event.getX();
                        float viewX = v.getRight() - v.getLeft();

                        stars1Entry.setBackgroundResource(R.drawable.icon_star_full);
                        stars2Entry.setBackgroundResource(R.drawable.icon_star_full);
                        stars3Entry.setBackgroundResource(R.drawable.icon_star_full);
                        if (screenX <= viewX / 2) {
                            stars4Entry.setBackgroundResource(R.drawable.icon_star_half);
                            rateList.set((Integer) v.getTag(), "3.5");
                        } else {
                            stars4Entry.setBackgroundResource(R.drawable.icon_star_full);
                            rateList.set((Integer) v.getTag(), "4.0");
                        }
                        stars5Entry.setBackgroundResource(R.drawable.icon_star_line);
                    }
                    return true;
                }
            });

            stars5Entry.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        float screenX = event.getX();
                        float viewX = v.getRight() - v.getLeft();

                        stars1Entry.setBackgroundResource(R.drawable.icon_star_full);
                        stars2Entry.setBackgroundResource(R.drawable.icon_star_full);
                        stars3Entry.setBackgroundResource(R.drawable.icon_star_full);
                        stars4Entry.setBackgroundResource(R.drawable.icon_star_full);
                        if (screenX <= viewX / 2) {
                            stars5Entry.setBackgroundResource(R.drawable.icon_star_half);
                            rateList.set((Integer) v.getTag(), "4.5");
                        } else {
                            stars5Entry.setBackgroundResource(R.drawable.icon_star_full);
                            rateList.set((Integer) v.getTag(), "5.0");
                        }
                    }
                    return true;
                }
            });
        }

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> params = new HashMap<String, String>();
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("authKey", Persistance.getInstance().getUserInfo(ReviewParticipants.this).authKey);
                params.put("userId", Persistance.getInstance().getUserInfo(ReviewParticipants.this).id);
                params.put("eventId", Persistance.getInstance().getEventToReview(ReviewParticipants.this).id);
                for (int i = 0; i < participantsId.size() - 1; i++) {
                    if (!Persistance.getInstance().getUserInfo(ReviewParticipants.this).id.equals(participantsId.get(i).toString())) {
                        params.put("participants[" + i + "][id]", participantsId.get(i).toString());
                        params.put("participants[" + i + "][rate]", rateList.get(i).toString());
                        params.put("participants[" + i + "][review]", reviewList.get(i).getText().toString());
                    }
                }
                HTTPResponseController.getInstance().reviewParticipants(params, headers, ReviewParticipants.this, null);
                finish();
            }
        });

    }
}
