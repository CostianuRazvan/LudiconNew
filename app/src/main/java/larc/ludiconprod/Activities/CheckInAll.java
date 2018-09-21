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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import de.hdodenhof.circleimageview.CircleImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;

public class CheckInAll extends Activity {

    Button skip;
    Button submit;
    LinearLayout checkInUser;
    CircleImageView userImageProfile;
    TextView userName;
    TextView socialRating;

    ArrayList<String> userIds = new ArrayList<String>();

    int id_layout = 987;
    int check = 1;

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_in_all);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        double width = dm.widthPixels;
        double height = dm.heightPixels;

        getWindow().setLayout((int) (width * .9), (int) (height * .8));

        skip = (Button) findViewById(R.id.skip);
        submit = (Button) findViewById(R.id.submit);
        checkInUser = (LinearLayout) findViewById(R.id.checkInUser);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ArrayList<String> participantsName = (ArrayList<String>) getIntent().getSerializableExtra("participantsNameList");
        ArrayList<String> participantsImage = (ArrayList<String>) getIntent().getSerializableExtra("participantsImageList");
        final ArrayList<String> participantsId = (ArrayList<String>) getIntent().getSerializableExtra("userIdList");
        ArrayList<Integer> participantsLevel = (ArrayList<Integer>) getIntent().getSerializableExtra("levelList");

        for (int i = 0; i < participantsName.size(); i++) {

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LayoutInflater vi = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            params1.setMargins(50, 0, 50, 50);

            LinearLayout mView = (LinearLayout) vi.inflate(R.layout.checkin_user, checkInUser, false);
            mView.setId(id_layout + i);
            if (i != 0) {
                params.addRule(RelativeLayout.BELOW, mView.getId() - 1);
            }
            mView.setLayoutParams(params);
            mView.setLayoutParams(params1);

            userImageProfile = (CircleImageView) mView.findViewById(R.id.userImageProfile);
            userName = (TextView) mView.findViewById(R.id.userName);
            socialRating = (TextView) mView.findViewById(R.id.socialRating);
            final ImageView checkButton = (ImageView) mView.findViewById(R.id.check_button);
            final RelativeLayout checkLayout = (RelativeLayout) mView.findViewById(R.id.checkLayout);

            Bitmap bitmap = decodeBase64(participantsImage.get(i).toString());
            userImageProfile.setImageBitmap(bitmap);
            userName.setText(participantsName.get(i).toString());
            socialRating.setText(participantsLevel.get(i).toString());

            checkLayout.setTag(participantsId.get(i).toString());
            checkLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (check == 0) {
                        checkButton.setVisibility(View.VISIBLE);
                        check = 1;
                        checkLayout.setAlpha(1f);
                        userIds.add((String) checkLayout.getTag());
                    }else if (check == 1){
                        checkButton.setVisibility(View.GONE);
                        check = 0;
                        checkLayout.setAlpha(0.5f);
                        userIds.remove((String) checkLayout.getTag());
                    }
                }
            });

            checkInUser.addView(mView);

        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> params = new HashMap<String, String>();
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("authKey", Persistance.getInstance().getUserInfo(CheckInAll.this).authKey);
                params.put("userId", Persistance.getInstance().getUserInfo(CheckInAll.this).id);
                params.put("eventId", Persistance.getInstance().getHappeningNow(CheckInAll.this).id);
                int counter = 0;
                if (userIds.size() > 0) {
                    for (int i = 0; i < userIds.size(); i++) {
                        params.put("userIds[" + counter + "]", userIds.get(i));
                        counter++;
                    }
                }
                HTTPResponseController.getInstance().savePointsAsAdmin(params, headers, CheckInAll.this, null);
                finish();
            }
        });
    }
}
