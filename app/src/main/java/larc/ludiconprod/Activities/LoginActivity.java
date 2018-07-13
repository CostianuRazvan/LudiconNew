package larc.ludiconprod.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.PasswordEncryptor;
import larc.ludiconprod.R;
import me.anwarshahriar.calligrapher.Calligrapher;

/**
 * Created by ancuta on 7/11/2017.
 */

public class LoginActivity extends BasicActivity {
    RelativeLayout backButton;
    TextView forgotPasswordText;
    TextView titleText;
    Button loginButton;
    EditText email;
    EditText password;
    String language = "EN";
    public static ProgressBar progressBar;
    private static String TAG = LoginActivity.class.getSimpleName();
    Toast mToast;
    Context mContext;
    Typeface typeFace;
    Typeface typeFaceBold;


    public boolean checkFieldsConstraints() {
        boolean isVerified = false;

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()) {
            Animation shake = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.shake);
            email.startAnimation(shake);
            email.setBackgroundResource(R.drawable.rounded_edittext_red);
            isVerified = true;
        }
        if (password.getText().length() == 0) {
            Animation shake = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.shake);
            password.startAnimation(shake);
            password.startAnimation(shake);
            password.setBackgroundResource(R.drawable.rounded_edittext_red);
            isVerified = true;
        }
        return isVerified;
    }

    /* Switch languages */

    public void translate() {

        Toast.makeText(getApplicationContext(), "Limba mea este " + getLanguage(), Toast.LENGTH_SHORT).show();

        language = getLanguage();
        if (language.compareToIgnoreCase("en") == 0) {

            titleText.setText(R.string.en_login);
            email.setHint(R.string.en_email);
            password.setHint(R.string.en_password);
            forgotPasswordText.setText(R.string.en_forgot_password);
            loginButton.setText(R.string.en_login);
        } else {
            titleText.setText(R.string.ro_login);
            email.setHint(R.string.ro_email);
            password.setHint(R.string.ro_password);
            loginButton.setText(R.string.ro_login);
            forgotPasswordText.setText(R.string.ro_forgot_password);

        }
        titleText.setTypeface(typeFace);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        typeFace = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Medium.ttf");
        typeFaceBold = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.ttf");

        mContext = getBaseContext();
        RelativeLayout toolbar = (RelativeLayout) findViewById(R.id.tool_bar);
        TextView title = (TextView) toolbar.findViewById(R.id.titleText);
        title.setTypeface(typeFace);

        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(this, "fonts/Quicksand-Medium.ttf", true);

        backButton = (RelativeLayout) findViewById(R.id.backButton);
        forgotPasswordText = (TextView) findViewById(R.id.forgotPasswordText);
        titleText = (TextView) findViewById(R.id.titleText);

        forgotPasswordText.setTypeface(typeFaceBold);
        password = (EditText) findViewById(R.id.password);
        password.setTypeface(typeFace);
        email = (EditText) findViewById(R.id.email);
        email.setTypeface(typeFace);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setAlpha(0f);
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
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                password.setBackgroundResource(R.drawable.rounded_edittext);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setTypeface(typeFaceBold);
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), IntroActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        forgotPasswordText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
                startActivity(intent);
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!checkFieldsConstraints()) {
                    try {

                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("email", email.getText().toString());
                        params.put("password", PasswordEncryptor.generateSHA255FromString(password.getText().toString()));//PasswordEncryptor.generateSHA255FromString(password.getText().toString()));
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("apiKey", HTTPResponseController.API_KEY);
                        // headers.put("Content-Type","application/json;charset=utf-8");
                        HTTPResponseController.getInstance().returnResponse(params, headers, LoginActivity.this, "http://207.154.236.13/api/login/");
                        progressBar.setIndeterminate(true);
                        progressBar.setAlpha(1f);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    showAToast();
                }
            }
        });

        translate();


    }

    @Override
    protected void onResume() {
        super.onResume();
        translate();
    }

    public void showAToast() {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, R.string.errorMessage, Toast.LENGTH_LONG);
        mToast.show();
    }

}
