package larc.ludiconprod.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class BasicActivity extends AppCompatActivity {

    private final String MY_LANGUAGE = "LANGUAGE";
    private final String LANGUAGE_KEY = "com.example.ludicon.language";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public String getLanguage() {
        SharedPreferences sharedPref = getSharedPreferences(MY_LANGUAGE, MODE_PRIVATE);
        return sharedPref.getString(LANGUAGE_KEY, "ATATATA");
    }

    public void setLanguage(String language) {

        Log.d("LANGUAGE", language);
        try {
            SharedPreferences sharedPref = getSharedPreferences(MY_LANGUAGE, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(LANGUAGE_KEY, language);
            editor.apply();
        } catch (NullPointerException e) {
            e.printStackTrace();
            language = "en";
        }
    }

}
