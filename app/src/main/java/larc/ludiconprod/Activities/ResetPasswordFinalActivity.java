package larc.ludiconprod.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import larc.ludiconprod.R;


/**
 * Created by ancuta on 7/11/2017.
 */

public class ResetPasswordFinalActivity extends Activity {
    RelativeLayout backButton;
    Button backToLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password_final_activity);
        Bundle extras = getIntent().getExtras();
        String from = extras.getString("from");
        Typeface typeFaceBold= Typeface.createFromAsset(getAssets(),"fonts/Quicksand-Bold.ttf");
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Medium.ttf");
        TextView titleText=(TextView) findViewById(R.id.titleText);
        titleText.setTypeface(typeFace);
        TextView sendText = (TextView) findViewById(R.id.textViewSent);
        TextView emailText = (TextView) findViewById(R.id.textViewEmail);
        if(from.equals("reset")) {
            titleText.setText(R.string.reset_password);
        }else if(from.equals("register")){
            titleText.setText(R.string.register_succes);
        }
        sendText.setTypeface(typeFace);
        emailText.setTypeface(typeFace);
        if(from.equals("register")){
            sendText.setText(R.string.sent_email_with_confirmation_code);
            sendText.setGravity(Gravity.CENTER);
            sendText.setPadding(30,0,30,0);

        }
        backButton=(RelativeLayout) findViewById(R.id.backButton);
        backToLogin=(Button) findViewById(R.id.returnLoginButton);
        backToLogin.setTypeface(typeFaceBold);
        backButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                finish();
            }
        });
        backToLogin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
