package larc.ludiconprod.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.login.LoginManager;

import larc.ludiconprod.R;


/**
 * Created by ancuta on 7/13/2017.
 */

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        setContentView(R.layout.main_activity);
        Button logOutButton=(Button)findViewById(R.id.logOutButton);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences settings = MainActivity.this.getSharedPreferences("UserDetails", MainActivity.this.MODE_PRIVATE);
                settings.edit().clear().commit();
                SharedPreferences profile = MainActivity.this.getSharedPreferences("ProfileImage", MainActivity.this.MODE_PRIVATE);
                profile.edit().clear().commit();
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(MainActivity.this, IntroActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);//***Change Here***
        startActivity(intent);
        finish();
        System.exit(0);
    }
}
