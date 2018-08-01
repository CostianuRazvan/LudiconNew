package larc.ludiconprod.Utils.MyProfileUtils;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.ScrollView;
import android.widget.TextView;

import larc.ludiconprod.Activities.BasicFragment;
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

public class EditProfileTab2 extends BasicFragment {
    private RadioGroup sexSwitch;
    private RadioButton male;
    private RadioButton female;

    private RadioGroup languageSwitch;
    private RadioButton romanian;
    private RadioButton english;

    private EditText ageTextView;
    private RelativeLayout passwordLayout;
    private TextWatcher textWatcher;
    private boolean languageChanged = false;

    EditText firstName;
    Button changePassword;
    EditText oldPass;
    EditText newPass;
    EditText rePass;
    EditText lastName;
    Button save;

    private void translate() {
        if (getLanguage().equalsIgnoreCase("ro")) {
            save.setText(R.string.ro_save_changes);
            male.setText(R.string.ro_male);
            female.setText(R.string.ro_female);
            english.setText(R.string.ro_english);
            romanian.setText(R.string.ro_romanian);
            firstName.setHint(R.string.ro_first_name);
            lastName.setHint(R.string.ro_last_name);
            changePassword.setText(R.string.ro_change_password);
            oldPass.setHint(R.string.ro_old_pass);
            rePass.setHint(R.string.ro_re_pass);
            newPass.setHint(R.string.ro_new_pass);
        } else {
            save.setText(R.string.en_save_changes);
            male.setText(R.string.en_male);
            female.setText(R.string.en_female);
            english.setText(R.string.en_english);
            romanian.setText(R.string.en_romanian);
            firstName.setHint(R.string.en_first_name);
            lastName.setHint(R.string.en_last_name);
            changePassword.setText(R.string.en_change_password);
            oldPass.setHint(R.string.en_old_pass);
            rePass.setHint(R.string.en_re_pass);
            newPass.setHint(R.string.en_new_pass);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        translate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.edittab2, container, false);

        try {
            final EditProfileActivity epa = (EditProfileActivity) super.getActivity();
            save = (Button) v.findViewById(R.id.saveChangesButton);
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
                    try {
                        if (epa.sameProfileInfo()) {
                            epa.findViewById(R.id.saveChangesButton).setAlpha(0);
                            epa.findViewById(R.id.saveChangesButton2).setAlpha(0);
                        } else {
                            epa.findViewById(R.id.saveChangesButton).setAlpha(1);
                            epa.findViewById(R.id.saveChangesButton2).setAlpha(1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            AssetManager assets = inflater.getContext().getAssets();// Is this the right asset?
            Typeface typeFace = Typeface.createFromAsset(assets, "fonts/Quicksand-Medium.ttf");
            Typeface typeFaceBold = Typeface.createFromAsset(assets, "fonts/Quicksand-Bold.ttf");

            firstName = (EditText) v.findViewById(R.id.editFirstName);
            firstName.setTypeface(typeFace);
            lastName = (EditText) v.findViewById(R.id.editLastName);
            lastName.setTypeface(typeFace);
            male = (RadioButton) v.findViewById(R.id.editMale);
            female = (RadioButton) v.findViewById(R.id.editFemale);
            sexSwitch = (RadioGroup) v.findViewById(R.id.editSexSwitch);
            romanian = (RadioButton) v.findViewById(R.id.RadioBtnEditRo);
            english = (RadioButton) v.findViewById(R.id.RadioBtnEditEn);
            languageSwitch = (RadioGroup) v.findViewById(R.id.RadioBtnEditLang);

            ageTextView = (EditText) v.findViewById(R.id.editDate);
            ageTextView.setTypeface(typeFace);
            changePassword = (Button) v.findViewById(R.id.editPasswordButton);
            changePassword.setTypeface(typeFaceBold);
            save.setTypeface(typeFaceBold);
            passwordLayout = (RelativeLayout) v.findViewById(R.id.editPasswordLayout);
            oldPass = (EditText) v.findViewById(R.id.oldPassword);
            oldPass.setTypeface(typeFace);
            newPass = (EditText) v.findViewById(R.id.newPassword);
            newPass.setTypeface(typeFace);
            rePass = (EditText) v.findViewById(R.id.editPasswordRepeat);
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
            this.ageTextView.setText("" + u.age);

            Log.d("Epa gender", u.firstName + u.gender);

            if (u.gender.equals("0")) {
                male.setChecked(true);
                male.setTextColor(Color.parseColor("#ffffff"));
            } else if (u.gender.equals("1")) {
                female.setChecked(true);
                female.setTextColor(Color.parseColor("#ffffff"));
            }

            if (male.isChecked()) {
                male.setBackgroundResource(R.drawable.toggle_male);
                male.setTextColor(Color.parseColor("#ffffff"));
            } else {
                female.setBackgroundResource(R.drawable.toggle_female);
                male.setTextColor(Color.parseColor("#ffffff"));
            }

            if (getLanguage().equals("ro"))
                romanian.setChecked(true);
            else
                english.setChecked(false);

            if (romanian.isChecked()) {
                romanian.setBackgroundResource(R.drawable.toggle_male);
                romanian.setTextColor(Color.parseColor("#ffffff"));
            } else {
                english.setBackgroundResource(R.drawable.toggle_female);
                english.setTextColor(Color.parseColor("#ffffff"));
            }

            sexSwitch.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                    if (male.isChecked()) {
                        male.setBackgroundResource(R.drawable.toggle_male);
                        female.setBackgroundResource(transparent);
                        male.setTextColor(Color.parseColor("#ffffff"));
                        female.setTextColor(Color.parseColor("#1A0c3855"));
                        epa.setSex(0);
                    } else {
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

            languageSwitch.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                    if (romanian.isChecked()) {
                        romanian.setBackgroundResource(R.drawable.toggle_male);
                        english.setBackgroundResource(transparent);
                        romanian.setTextColor(Color.parseColor("#ffffff"));
                        english.setTextColor(Color.parseColor("#1A0c3855"));
                        epa.setSavedLanguage("ro");
                    } else {
                        english.setBackgroundResource(R.drawable.toggle_female);
                        romanian.setBackgroundResource(transparent);
                        romanian.setTextColor(Color.parseColor("#1A0c3855"));
                        english.setTextColor(Color.parseColor("#ffffff"));
                        epa.setSavedLanguage("en");
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
        } catch (
                Exception e)

        {
            e.printStackTrace();
        }

        return v;
    }
}