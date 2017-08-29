package larc.ludiconprod.Utils.MyProfileUtils;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

import larc.ludiconprod.Activities.EditProfileActivity;
import larc.ludiconprod.Activities.IntroActivity;
import larc.ludiconprod.Controller.ImagePicker;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.User;

import static android.R.color.transparent;

/**
 * Created by alex_ on 10.08.2017.
 */

public class EditProfileTab2 extends Fragment {
    private RadioButton male;
    private RadioButton female;
    private RadioGroup sexSwitch;

    private EditText ageTextView;
    private RelativeLayout passwordLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edittab2, container, false);

        try {
            final EditProfileActivity epa = (EditProfileActivity) super.getActivity();
            User u = Persistance.getInstance().getProfileInfo(super.getActivity());


            AssetManager assets = inflater.getContext().getAssets();// Is this the right asset?
            Typeface typeFace= Typeface.createFromAsset(assets, "fonts/Quicksand-Medium.ttf");
            Typeface typeFaceBold = Typeface.createFromAsset(assets,"fonts/Quicksand-Bold.ttf");

            EditText firstName = (EditText) v.findViewById(R.id.editFirstName);
            firstName.setTypeface(typeFace);
            EditText lastName = (EditText) v.findViewById(R.id.editLastName);
            lastName.setTypeface(typeFace);
            male = (RadioButton) v.findViewById(R.id.editMale);
            female = (RadioButton) v.findViewById(R.id.editFemale);
            sexSwitch = (RadioGroup) v.findViewById(R.id.editSexSwitch);
            ageTextView = (EditText) v.findViewById(R.id.editDate);
            ageTextView.setTypeface(typeFace);
            Button changePassword = (Button) v.findViewById(R.id.editPasswordButton);
            changePassword.setTypeface(typeFaceBold);
            Button save = (Button) v.findViewById(R.id.saveChangesButton);
            save.setTypeface(typeFaceBold);
            passwordLayout = (RelativeLayout) v.findViewById(R.id.editPasswordLayout);
            EditText oldPass = (EditText) v.findViewById(R.id.oldPassword);
            oldPass.setTypeface(typeFace);
            EditText newPass = (EditText) v.findViewById(R.id.newPassword);
            newPass.setTypeface(typeFace);
            EditText rePass = (EditText) v.findViewById(R.id.editPasswordRepeat);
            rePass.setTypeface(typeFace);
            TextView email = (TextView) v.findViewById(R.id.emailLabel);
            email.setText(u.email);
            email.setTypeface(typeFace);
            ImageView image = (ImageView) v.findViewById(R.id.editImage);

            if (u.profileImage != null && !u.profileImage.isEmpty()) {
                Bitmap im = IntroActivity.decodeBase64(u.profileImage);
                image.setImageBitmap(im);
            }

            epa.setFirstName(firstName);
            epa.setLastName(lastName);
            epa.setDate(ageTextView);
            epa.setNewPassword(newPass);
            epa.setOldPassword(oldPass);
            epa.setRepeatPassword(rePass);
            save.setOnClickListener(epa);
            epa.setImage(image);

            male.setTypeface(typeFace);
            female.setTypeface(typeFace);

            firstName.setText(u.firstName);
            lastName.setText(u.lastName);
            this.ageTextView.setText("" + (Calendar.getInstance().get(Calendar.YEAR) - u.age));

            Log.d("Epa gender", u.firstName + u.gender);

            if(u.gender.equals("0")){
                male.setChecked(true);
                male.setTextColor(Color.parseColor("#ffffff"));
            } else if(u.gender.equals("1")) {
                female.setChecked(true);
                female.setTextColor(Color.parseColor("#ffffff"));
            }

            if(male.isChecked()) {
                male.setBackgroundResource(R.drawable.toggle_male);
                male.setTextColor(Color.parseColor("#ffffff"));
            } else{
                female.setBackgroundResource(R.drawable.toggle_female);
                male.setTextColor(Color.parseColor("#ffffff"));
            }

            sexSwitch.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                    if(male.isChecked()){
                        male.setBackgroundResource(R.drawable.toggle_male);
                        female.setBackgroundResource(transparent);
                        male.setTextColor(Color.parseColor("#ffffff"));
                        female.setTextColor(Color.parseColor("#1A0c3855"));
                        epa.setSex(0);;
                    }
                    else{
                        female.setBackgroundResource(R.drawable.toggle_female);
                        male.setBackgroundResource(transparent);
                        male.setTextColor(Color.parseColor("#1A0c3855"));
                        female.setTextColor(Color.parseColor("#ffffff"));
                        epa.setSex(1);
                    }
                }
            });

            changePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ViewGroup.LayoutParams params = passwordLayout.getLayoutParams();
                    if (params.height == 0) {
                        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    } else {
                        params.height = 0;
                    }
                    passwordLayout.setLayoutParams(params);
                }
            });

            ImageView editChoosePhoto = (ImageView) v.findViewById(R.id.editChoosePhoto);
            editChoosePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent chooseImageIntent = ImagePicker.getPickImageIntent(getActivity());
                    startActivityForResult(chooseImageIntent, EditProfileActivity.PICK_IMAGE_ID);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }


}