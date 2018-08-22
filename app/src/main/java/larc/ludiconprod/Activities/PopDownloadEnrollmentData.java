package larc.ludiconprod.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.IntentCompat;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.User;

import static larc.ludiconprod.Activities.ActivitiesActivity.deleteCachedInfo;

public class PopDownloadEnrollmentData extends Activity {

    TextView emailText;
    TextView newEmail;
    RelativeLayout emailTextView;
    RelativeLayout emailEditText;
    Button submit;
    String eventId;
    EditText emailText1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_download_data);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*.8), (int) (height*.4));

        emailText = (TextView) findViewById(R.id.emailText);
        newEmail = (TextView) findViewById(R.id.newEmail);
        emailTextView = (RelativeLayout) findViewById(R.id.email);
        emailEditText = (RelativeLayout) findViewById(R.id.email1);
        submit = (Button) findViewById(R.id.submit);
        emailText1 = (EditText) findViewById(R.id.emailText1);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final User u = Persistance.getInstance().getProfileInfo(this);
        eventId = (String) getIntent().getSerializableExtra("eventId");

        emailText.setText(u.email);
        newEmail.setText(getResources().getString(R.string.not_this_one));

        SpannableString ss = new SpannableString(getResources().getString(R.string.not_this_one));
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                emailTextView.setVisibility(View.INVISIBLE);
                emailEditText.setVisibility(View.VISIBLE);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
            }
        };
        if (Locale.getDefault().getLanguage().startsWith("en")) {
            ss.setSpan(clickableSpan, 20, 24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(Color.parseColor("#0c3855")), 20, 24, 0);
            ss.setSpan(bss, 20, 24, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        } else if( Locale.getDefault().getLanguage().startsWith("ro")) {
            ss.setSpan(clickableSpan, 22, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(Color.parseColor("#0c3855")), 22, 26, 0);
            ss.setSpan(bss, 22, 26, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }else if (Locale.getDefault().getLanguage().startsWith("fr")) {
            ss.setSpan(clickableSpan, 21, 24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(Color.parseColor("#0c3855")), 21, 24, 0);
            ss.setSpan(bss, 21, 24, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        newEmail.setText(ss);
        newEmail.setMovementMethod(LinkMovementMethod.getInstance());
        newEmail.setHighlightColor(Color.TRANSPARENT);



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> params = new HashMap<String, String>();
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("authKey", Persistance.getInstance().getUserInfo(PopDownloadEnrollmentData.this).authKey);
                params.put("userId", Persistance.getInstance().getUserInfo(PopDownloadEnrollmentData.this).id);
                params.put("eventId", eventId);
                if (emailEditText.getVisibility() == View.INVISIBLE) {
                    params.put("email", Persistance.getInstance().getUserInfo(PopDownloadEnrollmentData.this).email);
                }else{
                    params.put("email", emailText1.getText().toString().trim());
                }
                System.out.println("Params" + params);
                HTTPResponseController.getInstance().exportEnrollData(params, headers, PopDownloadEnrollmentData.this, null);
                finish();
            }
        });
    }

}
