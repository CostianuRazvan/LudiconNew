package larc.ludiconprod.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import larc.ludiconprod.R;


/**
 * Created by ancuta on 7/11/2017.
 */

public class ResetPasswordFinalActivity extends BasicActivity {
    RelativeLayout backButton;
    Button backToLogin;
    TextView titleText;
    TextView sendText;
    TextView emailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password_final_activity);
        Bundle extras = getIntent().getExtras();

        String from = extras.getString("from");
        Typeface typeFaceBold = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.ttf");
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Medium.ttf");

        titleText = (TextView) findViewById(R.id.titleText);
        titleText.setTypeface(typeFace);
        sendText = (TextView) findViewById(R.id.textViewSent);
        emailText = (TextView) findViewById(R.id.textViewEmail);


        sendText.setTypeface(typeFace);
        emailText.setTypeface(typeFace);
        if (from.equals("register")) {
            sendText.setGravity(Gravity.CENTER);
            sendText.setPadding(30, 0, 30, 0);

        }
        backButton = (RelativeLayout) findViewById(R.id.backButton);
        backToLogin = (Button) findViewById(R.id.returnLoginButton);
        backToLogin.setTypeface(typeFaceBold);
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });
        backToLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        translate(from);

    }

    public void translate(String from) {
        if (getLanguage().equalsIgnoreCase("ro")) {
            if (from.equals("reset")) {
                titleText.setText(R.string.ro_reset_password);
            } else if (from.equals("register")) {
                titleText.setText(R.string.ro_register_success);
            }
            backToLogin.setText(R.string.ro_return_to_login);
            sendText.setText(R.string.ro_email_sent_instructions);
            emailText.setText(R.string.ro_check_your_inbox);
        } else {

            if (from.equals("reset")) {
                titleText.setText(R.string.en_reset_password);
            } else if (from.equals("register")) {
                titleText.setText(R.string.en_register_success);
            }

            backToLogin.setText(R.string.en_return_to_login);
            sendText.setText(R.string.en_email_sent_instructions);
            emailText.setText(R.string.en_check_your_inbox);
            emailText.setText(R.string.en_check_your_inbox);

        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
