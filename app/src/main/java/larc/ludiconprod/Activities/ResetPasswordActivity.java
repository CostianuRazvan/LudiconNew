package larc.ludiconprod.Activities;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.R;
import me.anwarshahriar.calligrapher.Calligrapher;


/**
 * Created by ancuta on 7/11/2017.
 */

public class ResetPasswordActivity extends BasicActivity {

    RelativeLayout backButton;
    Button resetPassword;
    EditText email;
    TextView titleText;
    TextView enterEmailText;


    public boolean checkFieldsConstraints() {
        boolean isVerified = false;
        if (email.getText().toString().length() == 0 || !email.getText().toString().contains("@") || !email.getText().toString().contains(".")) {
            Animation shake = AnimationUtils.loadAnimation(ResetPasswordActivity.this, R.anim.shake);
            email.startAnimation(shake);
            email.setBackgroundResource(R.drawable.rounded_edittext_red);
            isVerified = true;
        }

        return isVerified;
    }

    // Hide soft keyboard
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password_activity);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Medium.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.ttf");
        titleText = (TextView) findViewById(R.id.titleText);
        titleText.setText("Reset Password");
        titleText.setTypeface(typeFace);

        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(this, "fonts/Quicksand-Medium.ttf", true);
        backButton = (RelativeLayout) findViewById(R.id.backButton);
        enterEmailText = (TextView) findViewById(R.id.textView2);
        resetPassword = (Button) findViewById(R.id.resetPasswordButton);
        resetPassword.setTypeface(typeFaceBold);
        email = (EditText) findViewById(R.id.emailAdress);
        email.setTypeface(typeFace);
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                email.setBackgroundResource(R.drawable.rounded_edittext);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });
        resetPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!checkFieldsConstraints()) {
                    resetPassword();
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Please provide email.", Toast.LENGTH_LONG).show();
                }
            }
        });

        translate();

    }

    private void translate() {
        if (getLanguage().equalsIgnoreCase("ro")) {
            resetPassword.setText(R.string.ro_reset_password);
            titleText.setText(R.string.ro_reset_password);
            resetPassword.setAllCaps(true);
            enterEmailText.setText(R.string.ro_enter_email_for_instructions);
            email.setHint(R.string.ro_email_address);
        } else {
            resetPassword.setText(R.string.en_reset_password);
            titleText.setText(R.string.en_reset_password);
            enterEmailText.setText(R.string.en_enter_email_for_instructions);
            resetPassword.setAllCaps(true);
            email.setHint(R.string.en_email_address);
        }
    }

    private void resetPassword() {
        HashMap<String, String> params = new HashMap<>();
        params.put("email", this.email.getText().toString());
        HashMap<String, String> head = new HashMap<>();
        head.put("apiKey", HTTPResponseController.API_KEY);
        HTTPResponseController.getInstance().resetPassword(params, head, this);
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
