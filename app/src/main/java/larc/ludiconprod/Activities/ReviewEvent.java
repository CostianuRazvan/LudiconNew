package larc.ludiconprod.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;

import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;

public class ReviewEvent extends Activity {

    RelativeLayout stars;
    ImageView stars1;
    ImageView stars2;
    ImageView stars3;
    ImageView stars4;
    ImageView stars5;
    TextView rate;
    Button submit;
    EditText review;

    String rateText;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_event);

        stars = (RelativeLayout) findViewById(R.id.stars);
        stars1 = (ImageView) findViewById(R.id.stars1);
        stars2 = (ImageView) findViewById(R.id.stars2);
        stars3 = (ImageView) findViewById(R.id.stars3);
        stars4 = (ImageView) findViewById(R.id.stars4);
        stars5 = (ImageView) findViewById(R.id.stars5);
        rate = (TextView) findViewById(R.id.rate);
        submit = (Button) findViewById(R.id.submitButton);
        review = (EditText) findViewById(R.id.review);

        stars1.setBackgroundResource(R.drawable.icon_star_full);
        stars2.setBackgroundResource(R.drawable.icon_star_full);
        stars3.setBackgroundResource(R.drawable.icon_star_full);
        stars4.setBackgroundResource(R.drawable.icon_star_full);
        stars5.setBackgroundResource(R.drawable.icon_star_full);
        rateText = "5.0";
        rate.setText(rateText + " " + getResources().getString(R.string.legendary) + "!");


        stars1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    float screenX = event.getX();
                    float viewX = v.getRight() - v.getLeft();

                    stars1.setBackgroundResource(R.drawable.icon_star_full);
                    stars2.setBackgroundResource(R.drawable.icon_star_line);
                    stars3.setBackgroundResource(R.drawable.icon_star_line);
                    stars4.setBackgroundResource(R.drawable.icon_star_line);
                    stars5.setBackgroundResource(R.drawable.icon_star_line);
                    rateText = "1.0";
                    rate.setText(rateText + " " + getResources().getString(R.string.poor) + "!");
                }
                return true;
            }
        });

        stars2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    float screenX = event.getX();
                    float viewX = v.getRight() - v.getLeft();

                    stars1.setBackgroundResource(R.drawable.icon_star_full);
                    if (screenX <= viewX / 2) {
                        stars2.setBackgroundResource(R.drawable.icon_star_half);
                        rateText = "1.5";
                    } else {
                        stars2.setBackgroundResource(R.drawable.icon_star_full);
                        rateText = "2.0";
                    }
                    stars3.setBackgroundResource(R.drawable.icon_star_line);
                    stars4.setBackgroundResource(R.drawable.icon_star_line);
                    stars5.setBackgroundResource(R.drawable.icon_star_line);
                    rate.setText(rateText + " " + getResources().getString(R.string.below_average) + "!");
                }
                return true;
            }
        });

        stars3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    float screenX = event.getX();
                    float viewX = v.getRight() - v.getLeft();

                    stars1.setBackgroundResource(R.drawable.icon_star_full);
                    stars2.setBackgroundResource(R.drawable.icon_star_full);
                    if (screenX <= viewX / 2) {
                        stars3.setBackgroundResource(R.drawable.icon_star_half);
                        rateText = "2.5";
                    } else {
                        stars3.setBackgroundResource(R.drawable.icon_star_full);
                        rateText = "3.0";
                    }
                    stars4.setBackgroundResource(R.drawable.icon_star_line);
                    stars5.setBackgroundResource(R.drawable.icon_star_line);
                    rate.setText(rateText + " " + getResources().getString(R.string.average) + "!");
                }
                return true;
            }
        });

        stars4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    float screenX = event.getX();
                    float viewX = v.getRight() - v.getLeft();

                    stars1.setBackgroundResource(R.drawable.icon_star_full);
                    stars2.setBackgroundResource(R.drawable.icon_star_full);
                    stars3.setBackgroundResource(R.drawable.icon_star_full);
                    if (screenX <= viewX / 2) {
                        stars4.setBackgroundResource(R.drawable.icon_star_half);
                        rateText = "3.5";
                    } else {
                        stars4.setBackgroundResource(R.drawable.icon_star_full);
                        rateText = "4.0";
                    }
                    stars5.setBackgroundResource(R.drawable.icon_star_line);
                    rate.setText(rateText + " " + getResources().getString(R.string.good) + "!");
                }
                return true;
            }
        });

        stars5.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    float screenX = event.getX();
                    float viewX = v.getRight() - v.getLeft();

                    stars1.setBackgroundResource(R.drawable.icon_star_full);
                    stars2.setBackgroundResource(R.drawable.icon_star_full);
                    stars3.setBackgroundResource(R.drawable.icon_star_full);
                    stars4.setBackgroundResource(R.drawable.icon_star_full);
                    if (screenX <= viewX / 2) {
                        stars5.setBackgroundResource(R.drawable.icon_star_half);
                        rateText = "4.5";
                    } else {
                        stars5.setBackgroundResource(R.drawable.icon_star_full);
                        rateText = "5.0";
                    }
                    rate.setText(rateText + " " + getResources().getString(R.string.excellent) + "!");
                }
                return true;
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> params = new HashMap<String, String>();
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("authKey", Persistance.getInstance().getUserInfo(ReviewEvent.this).authKey);
                params.put("userId", Persistance.getInstance().getUserInfo(ReviewEvent.this).id);
                params.put("eventId", Persistance.getInstance().getEventToReview(ReviewEvent.this).id);
                params.put("rate", rateText);
                params.put("review", review.getText().toString());
                HTTPResponseController.getInstance().reviewEvent(params, headers, ReviewEvent.this, null);

                if (Persistance.getInstance().getUserInfo(ReviewEvent.this).rateLocation.equals("true")) {
                    Intent intent = new Intent(ReviewEvent.this, ReviewLocation.class);
                    startActivity(intent);
                }else if (Persistance.getInstance().getUserInfo(ReviewEvent.this).rateUsers.equals("true")) {
                    Intent intent = new Intent(ReviewEvent.this, ReviewParticipants.class);
                    startActivity(intent);
                }

                finish();
            }
        });


    }
}
