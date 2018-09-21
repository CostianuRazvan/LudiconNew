package larc.ludiconprod.Utils.MyProfileUtils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
    private TextWatcher textWatcher;

    ImageView check_event;
    ImageView check_location;
    ImageView check_users;
    TextView popReview;
    Button button_check_event;
    Button button_check_location;
    Button button_check_users;
    int checkEvent;
    int checkLocation;
    int checkUsers;


    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.edittab2, container, false);

        try {
            final EditProfileActivity epa = (EditProfileActivity) super.getActivity();
            final Button save = (Button) v.findViewById(R.id.saveChangesButton);
            User u = Persistance.getInstance().getProfileInfo(super.getActivity());

            this.textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (epa.sameProfileInfo()) {
                        epa.findViewById(R.id.saveChangesButton).setAlpha(0);
                        epa.findViewById(R.id.saveChangesButton2).setAlpha(0);
                    } else {
                        epa.findViewById(R.id.saveChangesButton).setAlpha(1);
                        epa.findViewById(R.id.saveChangesButton2).setAlpha(1);
                    }
                }
            };

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
            save.setTypeface(typeFaceBold);
            passwordLayout = (RelativeLayout) v.findViewById(R.id.editPasswordLayout);
            final EditText oldPass = (EditText) v.findViewById(R.id.oldPassword);
            oldPass.setTypeface(typeFace);
            final EditText newPass = (EditText) v.findViewById(R.id.newPassword);
            newPass.setTypeface(typeFace);
            final EditText rePass = (EditText) v.findViewById(R.id.editPasswordRepeat);
            rePass.setTypeface(typeFace);
            TextView email = (TextView) v.findViewById(R.id.emailLabel);
            email.setText(u.email);
            email.setTypeface(typeFace);
            ImageView image = (ImageView) v.findViewById(R.id.editImage);
            check_event = (ImageView) v.findViewById(R.id.check_event);
            check_location = (ImageView) v.findViewById(R.id.check_location);
            check_users = (ImageView) v.findViewById(R.id.check_users);
            popReview = (TextView) v.findViewById(R.id.popReview);
            popReview.setTypeface(typeFace);
            button_check_event = (Button) v.findViewById(R.id.button_check_event);
            button_check_location = (Button) v.findViewById(R.id.button_check_location);
            button_check_users = (Button) v.findViewById(R.id.button_check_users);

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
            this.ageTextView.setText("" + u.age);

            Log.d("Epa gender", u.firstName + u.gender);

            if(u.gender.equals("0")){
                male.setChecked(true);
                male.setTextColor(Color.parseColor("#ffffff"));
                epa.setSex(0);
            } else if(u.gender.equals("1")) {
                female.setChecked(true);
                female.setTextColor(Color.parseColor("#ffffff"));
                epa.setSex(1);
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

                    if (epa.sameProfileInfo()) {
                        epa.findViewById(R.id.saveChangesButton).setAlpha(0);
                        epa.findViewById(R.id.saveChangesButton2).setAlpha(0);
                    } else {
                        epa.findViewById(R.id.saveChangesButton).setAlpha(1);
                        epa.findViewById(R.id.saveChangesButton2).setAlpha(1);
                    }
                }
            });

            if (u.rateEvent.equals("true")){
                check_event.setVisibility(View.VISIBLE);
                checkEvent = 1;
                epa.setEventReview("true");
                button_check_event.setAlpha(1f);
            }else if(u.rateEvent.equals("false")){
                check_event.setVisibility(View.GONE);
                checkEvent = 0;
                epa.setEventReview("false");
                button_check_event.setAlpha(0.5f);
            }

            button_check_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkEvent == 0) {
                        check_event.setVisibility(View.VISIBLE);
                        checkEvent = 1;
                        epa.setEventReview("true");
                        button_check_event.setAlpha(1f);
                    }else if (checkEvent == 1){
                        check_event.setVisibility(View.GONE);
                        checkEvent = 0;
                        epa.setEventReview("false");
                        button_check_event.setAlpha(0.5f);
                    }
                    if (epa.sameProfileInfo()) {
                        epa.findViewById(R.id.saveChangesButton).setAlpha(0);
                        epa.findViewById(R.id.saveChangesButton2).setAlpha(0);
                    } else {
                        epa.findViewById(R.id.saveChangesButton).setAlpha(1);
                        epa.findViewById(R.id.saveChangesButton2).setAlpha(1);
                    }
                }
            });

            if (u.rateLocation.equals("true")){
                check_location.setVisibility(View.VISIBLE);
                checkLocation = 1;
                epa.setLocationReview("true");
                button_check_location.setAlpha(1f);
            }else if (u.rateLocation.equals("false")){
                check_location.setVisibility(View.GONE);
                checkLocation = 0;
                epa.setLocationReview("false");
                button_check_location.setAlpha(0.5f);
            }

            button_check_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkLocation == 0) {
                        check_location.setVisibility(View.VISIBLE);
                        checkLocation = 1;
                        epa.setLocationReview("true");
                        button_check_location.setAlpha(1f);
                    }else if (checkLocation == 1){
                        check_location.setVisibility(View.GONE);
                        checkLocation = 0;
                        epa.setLocationReview("false");
                        button_check_location.setAlpha(0.5f);
                    }
                    if (epa.sameProfileInfo()) {
                        epa.findViewById(R.id.saveChangesButton).setAlpha(0);
                        epa.findViewById(R.id.saveChangesButton2).setAlpha(0);
                    } else {
                        epa.findViewById(R.id.saveChangesButton).setAlpha(1);
                        epa.findViewById(R.id.saveChangesButton2).setAlpha(1);
                    }
                }
            });

            if (u.rateUsers.equals("true")){
                check_users.setVisibility(View.VISIBLE);
                checkUsers = 1;
                epa.setUsersReview("true");
                button_check_users.setAlpha(1f);
            }else if (u.rateUsers.equals("false")) {
                check_users.setVisibility(View.GONE);
                checkUsers = 0;
                epa.setUsersReview("false");
                button_check_users.setAlpha(0.5f);
            }

            button_check_users.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkUsers == 0) {
                        check_users.setVisibility(View.VISIBLE);
                        checkUsers = 1;
                        epa.setUsersReview("true");
                        button_check_users.setAlpha(1f);
                    }else if (checkUsers == 1){
                        check_users.setVisibility(View.GONE);
                        checkUsers = 0;
                        epa.setUsersReview("false");
                        button_check_users.setAlpha(0.5f);
                    }
                    if (epa.sameProfileInfo()) {
                        epa.findViewById(R.id.saveChangesButton).setAlpha(0);
                        epa.findViewById(R.id.saveChangesButton2).setAlpha(0);
                    } else {
                        epa.findViewById(R.id.saveChangesButton).setAlpha(1);
                        epa.findViewById(R.id.saveChangesButton2).setAlpha(1);
                    }
                }
            });

            if (u.facebookId == null || u.facebookId.isEmpty()) {
                changePassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ViewGroup.LayoutParams params = passwordLayout.getLayoutParams();
                        if (params.height == 0) {
                            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                            passwordLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                                @Override
                                public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                                    passwordLayout.removeOnLayoutChangeListener(this);
                                    ScrollView sv = (ScrollView) v.findViewById(R.id.editScroll);
                                    sv.fullScroll(View.FOCUS_DOWN);
                                }
                            });
                        } else {
                            params.height = 0;
                            newPass.setText("");
                            oldPass.setText("");
                            rePass.setText("");
                        }
                        passwordLayout.setLayoutParams(params);
                    }
                });
            } else {
                changePassword.getLayoutParams().width = 0;
                changePassword.setLayoutParams(changePassword.getLayoutParams());
            }

            ImageView editChoosePhoto = (ImageView) v.findViewById(R.id.editChoosePhoto);
            editChoosePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent chooseImageIntent = ImagePicker.getPickImageIntent(getActivity());
                    startActivityForResult(chooseImageIntent, EditProfileActivity.PICK_IMAGE_ID);
                }
            });

            firstName.addTextChangedListener(this.textWatcher);
            lastName.addTextChangedListener(this.textWatcher);
            ageTextView.addTextChangedListener(this.textWatcher);
            oldPass.addTextChangedListener(this.textWatcher);
            newPass.addTextChangedListener(this.textWatcher);
            rePass.addTextChangedListener(this.textWatcher);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }
}