package larc.ludiconprod.Activities;


import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class BasicFragment extends android.support.v4.app.Fragment {

    private final String MY_LANGUAGE = "LANGUAGE";
    private final String LANGUAGE_KEY = "com.example.ludicon.language";

    public BasicFragment() {
        // Required empty public constructor
    }

    public Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public String getLanguage() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences(MY_LANGUAGE, MODE_PRIVATE);
        return sharedPref.getString(LANGUAGE_KEY, "ATATATA");
    }

    public void setLanguage(String language) {

        Log.d("LANGUAGE", language);
        try {
            SharedPreferences sharedPref = getActivity().getSharedPreferences(MY_LANGUAGE, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove(LANGUAGE_KEY);
            editor.putString(LANGUAGE_KEY, language);
            editor.apply();
        } catch (NullPointerException e) {
            e.printStackTrace();
            language = "en";
        }
    }

}
