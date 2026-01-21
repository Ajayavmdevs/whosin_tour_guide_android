package com.whosin.app.ui.activites.Profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.ActivityUpdateProfileBinding;
import com.whosin.app.databinding.GenderSelectBottomshitBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.ImageUploadModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.auth.AuthenticationActivity;
import com.whosin.app.ui.activites.comman.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UpdateProfileActivity extends BaseActivity {

    private ActivityUpdateProfileBinding binding;

    private GenderSelectBottomshitBinding mBinding;

    private String selectedDate = "";

    private String isGenderSelected = "";

    private String imageUrl;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void initUi() {

        applyTranslations();

        SessionManager.shared.setContext( activity );
        Graphics.loadImageWithFirstLetter( SessionManager.shared.getUser().getImage(), binding.ivProfile, SessionManager.shared.getUser().getFirstName() );

        binding.etEmail.setClickable( false );
        binding.etEmail.setFocusable( false );

//        binding.etPhone.setClickable( false );
//        binding.etPhone.setFocusable( false );

        if (SessionManager.shared.getUser().getIsEmailVerified() == 1){
            binding.emailVerify.setBackground(getResources().getDrawable(R.drawable.green_gradient_bg));
            binding.emailVerify.setText(getValue("verified"));
        }


    }

    @Override
    protected void setListeners() {

//        binding.editPhone.setOnClickListener( view -> {
//            startActivity( new Intent( activity, ChangePhoneNumberActivity.class )
//                    .putExtra( "phone", SessionManager.shared.getUser().getPhone()
//                    ).putExtra( "country_code", SessionManager.shared.getUser().getCountryCode() ) );
//        } );

        binding.closeBtn.setOnClickListener( v -> onBackPressed() );

        binding.ilDate.setOnClickListener( view -> {
            DatePickerDialog datePickerDialog = Utils.getDatePickerDialog( activity, data -> {
                selectedDate = Utils.formatDate( data, "dd MMM yyyy" );
                binding.tvBirthDate.setText( selectedDate );
            } );
            datePickerDialog.show();
        } );

        binding.ilGender.setOnClickListener( v -> genderSelectBottomShit() );

        binding.updateBtn.setOnClickListener( view -> updateProfile() );

        binding.ivPicker.setOnClickListener( view -> getImagePicker() );

        binding.ilNationality.setOnClickListener( v -> {
            binding.etNationality.launchCountrySelectionDialog();
        } );

        binding.etNationality.setOnCountryChangeListener(() -> {
            if (!binding.etNationality.getSelectedCountryNameCode().isEmpty()) {
                binding.etNationality.setVisibility(View.VISIBLE);
            }
        });


        binding.editEmail.setOnClickListener(v -> {
            Intent intent = new Intent(activity, ChangePhoneNumberActivity.class);
            intent.putExtra("email", binding.etEmail.getText().toString());
            intent.putExtra("isEmailVerify", true);
            activityLauncher.launch(intent, result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    boolean isVerify = result.getData().getBooleanExtra("verify", false);
                    if (isVerify) {
                        binding.emailVerify.setBackground(getResources().getDrawable(R.drawable.green_gradient_bg));
                        binding.emailVerify.setText(getValue("verified"));
                    }
                }
            });

        });

        binding.deactivateAccount.setOnClickListener( v -> {
            Graphics.showAlertDialogWithOkCancel( activity, getString(R.string.app_name), getValue("deactivate_info"),
                    getValue("deactivate_account"), getValue("cancel"), aBoolean -> {
                        if (aBoolean) {
                            requestDeleteUserAccount( "deactive" );
                        }

                    } );
        } );

        binding.deleteAccount.setOnClickListener( view -> {
            Graphics.showAlertDialogWithOkCancel( activity, getString(R.string.app_name), getValue("delete_account_confirmation"), getValue("temporary"), getValue("permanently"), getValue("cancel"), action -> {
                switch (action) {
                    case "temporary":
                        Graphics.showAlertDialogWithOkCancel( activity, getString(R.string.app_name), getValue("temporary_delete_info"),
                                getValue("yes_delete"), getValue("cancel"), aBoolean -> {
                                    if (aBoolean) {
                                        requestDeleteUserAccount( "temporary" );
                                    }

                                } );
                        break;
                    case "permanently":
                        Graphics.showAlertDialogWithOkCancel( activity, getString(R.string.app_name), getValue("delete_data_warning"),
                                getValue("yes_delete"), getValue("cancel"), aBoolean -> {
                                    if (aBoolean) {
                                        requestDeleteUserAccount( "permanent" );
                                    }

                                } );
                        break;
                    case "cancel":
                        break;
                }
            } );
        } );

        if (SessionManager.shared.getUser().getEmail() != null) {
            binding.emailVerify.setVisibility( View.VISIBLE );
        } else {
            binding.emailVerify.setVisibility( View.GONE );
        }

        binding.etEmail.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().isEmpty()) {
                    binding.emailVerify.setVisibility( View.GONE );
                } else {
                    binding.emailVerify.setVisibility( View.VISIBLE );
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        } );

//        binding.etPhone.addTextChangedListener( new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (charSequence.toString().trim().isEmpty()) {
//                    binding.phoneVerify.setVisibility( View.GONE );
//                } else {
//                    binding.phoneVerify.setVisibility( View.VISIBLE );
//                }
//            }
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        } );


        binding.textNationally.setOnClickListener(view -> {
            binding.etNationality.launchCountrySelectionDialog();
        });

        binding.etNationality.setOnCountryChangeListener(() -> {
            String selectedCountryName = binding.etNationality.getSelectedCountryName();
            binding.textNationally.setText(selectedCountryName);
        });

    }


    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityUpdateProfileBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        runOnUiThread( () -> {
            requestUserProfile();
            getProfileDetails();
        } );

    }

    ActivityResultLauncher<Intent> startActivity = registerForActivityResult( new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Uri imageData = result.getData().getData();
            requestImageUpload( imageData );
        }
    } );

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.nameControl, "update_your_profile");
        map.put(binding.tvBasicInformationTitle, "basic_information");
        map.put(binding.tvFirstNameTitle, "first_name");
        map.put(binding.tvLastNameTitle, "last_name");
        map.put(binding.tvBio, "bio");
        map.put(binding.etBio, "about_you_placeholder");
        map.put(binding.tvEmail, "email");
        map.put(binding.etEmail, "email");
        map.put(binding.tvPhone, "phone");
        map.put(binding.etPhone, "phone");
        map.put(binding.tvPersonalInformationTitle, "personal_information");
        map.put(binding.dobTitle, "date_of_birth");

        map.put(binding.tvNationalityTitle, "nationality");
        map.put(binding.tvGenderTitle, "gender");
        map.put(binding.tvSocialAccountTitle, "social_accounts");
        map.put(binding.description, "private_profiles_notice");
        map.put(binding.deactivateAccount, "deactivate_account");
        map.put(binding.deleteAccount, "delete_account");
        map.put(binding.updateBtn, "update");

        binding.layoutInstagram.setHintText(getValue("add_your_instagram_handle"));
        binding.layoutTiktok.setHintText(getValue("add_your_tiktok_account_optional"));
        binding.layoutYoutube.setHintText(getValue("add_your_youtube_channel_optional"));
        binding.layoutFacebook.setHintText(getValue("add_your_facebook_account_optional"));

        binding.tvBirthDate.setHint(getValue("select_dob"));
        binding.textNationally.setHint(getValue("select_nationality"));
        binding.tvGender.setHint(getValue("select_gender"));
        return map;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    @SuppressLint("UseCompatLoadingForDrawables")
    private void getProfileDetails() {
        binding.etFirstName.setText( SessionManager.shared.getUser().getFirstName() );
        binding.etLastName.setText( SessionManager.shared.getUser().getLastName() );
        binding.etEmail.setText( SessionManager.shared.getUser().getEmail() );
        binding.etPhone.setText( SessionManager.shared.getUser().getPhone() );
        binding.tvGender.setText( SessionManager.shared.getUser().getGender() );
        selectedDate = SessionManager.shared.getUser().getDateOfBirth();
        binding.tvBirthDate.setText( Utils.changeDateFormat(selectedDate,"yyyy-MM-dd","dd MMM yyyy"));
        binding.etBio.setText( SessionManager.shared.getUser().getBio() );

        if (SessionManager.shared.getUser().getEmail() != null && !SessionManager.shared.getUser().getEmail().isEmpty()) {
            binding.emailVerify.setVisibility( View.VISIBLE );
        } else {
            binding.emailVerify.setVisibility( View.GONE );
        }

        String countryCodeString = SessionManager.shared.getUser().getCountryCode();
        if (!TextUtils.isEmpty( countryCodeString )) {
            try {
                int countryCode = Integer.parseInt( countryCodeString );
                binding.countryCode.setCountryForPhoneCode( countryCode );
            } catch (NumberFormatException e) {
                e.printStackTrace();
                binding.countryCode.setAutoDetectedCountry(true);
            }
        } else {
            binding.countryCode.setAutoDetectedCountry(true);
        }


        if (SessionManager.shared.getUser().getNationality() != null && !SessionManager.shared.getUser().getNationality().isEmpty()) {
            binding.etNationality.setVisibility(View.GONE);
            binding.textNationally.setText(SessionManager.shared.getUser().getNationality());
        } else {
            binding.etNationality.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(SessionManager.shared.getUser().getPhone())) {
            binding.phoneVerify.setVisibility(View.GONE);
        } else {
            binding.phoneVerify.setVisibility(View.VISIBLE);
        }

        if (SessionManager.shared.getUser().getIsPhoneVerified() == 1) {
            binding.phoneVerify.setBackground( getResources().getDrawable( R.drawable.green_gradient_bg ) );
            binding.phoneVerify.setText( getValue("verified") );
            binding.countryCode.setCcpClickable( false );
            binding.countryCode.setClickable( false );
            binding.etPhone.setClickable( false );
            binding.etPhone.setFocusable( false );

        } else {
            binding.phoneVerify.setBackground( getResources().getDrawable( R.drawable.pink_gradient_bg ) );
            binding.phoneVerify.setText( getValue("verify") );
            binding.phoneVerify.setOnClickListener( view -> {
                requestSentOtp( "phone" );
                PhoneVerifiedBottomSheet bottomSheet = new PhoneVerifiedBottomSheet();
                bottomSheet.userId = SessionManager.shared.getUser().getId();
                bottomSheet.callback = data -> {
                    if (data) {
                        binding.phoneVerify.setBackground( getResources().getDrawable( R.drawable.green_gradient_bg ) );
                        binding.phoneVerify.setText( getValue("verified"));
                        requestUserProfile();
                    }
                };
                bottomSheet.isNewPhone = false;
                bottomSheet.type = "phone";
                bottomSheet.show( getSupportFragmentManager(), "1" );
            } );


        }
        if (SessionManager.shared.getUser().getIsEmailVerified() == 1) {
            binding.emailVerify.setBackground( getResources().getDrawable( R.drawable.green_gradient_bg ) );
            binding.emailVerify.setText(getValue("verified"));
        } else {
            binding.emailVerify.setBackground( getResources().getDrawable( R.drawable.pink_gradient_bg ) );
            binding.emailVerify.setText(getValue("verify"));
        }


        if (!TextUtils.isEmpty(SessionManager.shared.getUser().getInstagram())) {
            binding.layoutInstagram.setText(SessionManager.shared.getUser().getInstagram());
        }
        if (!TextUtils.isEmpty(SessionManager.shared.getUser().getTiktok())) {
            binding.layoutTiktok.setText(SessionManager.shared.getUser().getTiktok());
        }
        if (!TextUtils.isEmpty(SessionManager.shared.getUser().getYoutube())) {
            binding.layoutYoutube.setText(SessionManager.shared.getUser().getYoutube());
        }
        if (!TextUtils.isEmpty(SessionManager.shared.getUser().getFacebook())) {
            binding.layoutFacebook.setText(SessionManager.shared.getUser().getFacebook());
        }
    }

    private void genderSelectBottomShit() {
        BottomSheetDialog dialog = new BottomSheetDialog( activity );
        mBinding = GenderSelectBottomshitBinding.inflate( getLayoutInflater() );
        dialog.setContentView( mBinding.getRoot() );

        mBinding.tvMale.setOnClickListener( view -> {
            selectGender( "Male" );
            dialog.cancel();
        } );

        mBinding.tvFemale.setOnClickListener( view -> {
            selectGender( "Female" );
            dialog.cancel();
        } );

        mBinding.tvOther.setOnClickListener( view -> {
            dialog.dismiss();
            selectGender( "Prefer Not to Say" );

        } );

        mBinding.tvCancel.setOnClickListener( view -> dialog.cancel() );
        dialog.show();

    }

    private void selectGender(String type) {
        mBinding.tvMale.setBackgroundColor( getColor( R.color.light_black ) );
        mBinding.tvFemale.setBackgroundColor( getColor( R.color.light_black ) );
        mBinding.tvOther.setBackgroundColor( getColor( R.color.light_black ) );
        isGenderSelected = type;
        binding.tvGender.setText(isGenderSelected);

        switch (type) {
            case "Male":
                mBinding.tvMale.setBackgroundColor( getResources().getColor( R.color.light_transparent ) );
                break;
            case "Female":
                mBinding.tvFemale.setBackgroundColor( getResources().getColor( R.color.light_transparent ) );
                break;
            case "Prefer Not To Say":
                mBinding.tvOther.setBackgroundColor( getResources().getColor( R.color.light_transparent ) );
                break;
        }
    }

    private void getImagePicker() {
        ArrayList<String> data = new ArrayList<>();
        data.add(getValue("gallery"));
        data.add(getValue("camera"));
        Graphics.showActionSheet( activity, getValue("choose_any_one"), data, (data1, position) -> {
            switch (position) {
                case 0:
                    ImagePicker.with( activity ).galleryOnly().cropSquare().createIntent( intent -> {
                        startActivity.launch( intent );
                        return null;
                    } );
                    break;
                case 1:
                    ImagePicker.with( activity ).cameraOnly().cropSquare().createIntent( intent -> {
                        startActivity.launch( intent );
                        return null;
                    } );
                    break;
            }
        } );
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void updateProfile() {

        String firstName = binding.etFirstName.getText().toString().trim();
        String lastName = binding.etLastName.getText().toString().trim();
        String gender = binding.tvGender.getText().toString();
        String dateOfBirth = binding.tvBirthDate.getText().toString();
        String email = binding.etEmail.getText().toString().trim();
        String phone = binding.etPhone.getText().toString();
        String nationality = binding.textNationally.getText().toString();
        String countryCode = binding.countryCode.getSelectedCountryCode();
        String region = binding.countryCode.getSelectedCountryNameCode();
        String bio = binding.etBio.getText().toString();

        if (Utils.isNullOrEmpty( firstName )) {
            Toast.makeText( this, getValue("enter_first_name"), Toast.LENGTH_SHORT ).show();
            return;
        }
        if (Utils.isNullOrEmpty( lastName )) {
            Toast.makeText( this, getValue("enter_last_name"), Toast.LENGTH_SHORT ).show();
            return;
        }

        if (!TextUtils.isEmpty(phone)){
            if (!Utils.isValidPhoneNumber( countryCode, phone, region )) {
                Toast.makeText( this, getValue("error_invalid_phone"), Toast.LENGTH_SHORT ).show();
                return;
            }
        }

//        if (Utils.isNullOrEmpty( phone )) {
//            Toast.makeText( this, "Please enter phone", Toast.LENGTH_SHORT ).show();
//            return;
//        }


//        if (Utils.isNullOrEmpty(binding.layoutInstagram.getText())) {
//            Toast.makeText( this, "Please enter valid instagram profile link.", Toast.LENGTH_SHORT ).show();
//            return;
//        }


        JsonObject object = new JsonObject();
        object.addProperty( "first_name", firstName );
        object.addProperty( "last_name", lastName );
        object.addProperty( "gender", gender.toLowerCase() );
        object.addProperty( "dateOfBirth", Utils.changeDateFormat(dateOfBirth,"dd MMM yyyy","yyyy-MM-dd"));
        object.addProperty( "email", email );
        object.addProperty( "phone", phone );
        object.addProperty( "nationality", nationality );
        object.addProperty( "country_code", countryCode );
        object.addProperty( "bio", bio );

        object.addProperty("facebook", binding.layoutFacebook.getText());
        object.addProperty("instagram", binding.layoutInstagram.getText());
        object.addProperty("youtube", binding.layoutYoutube.getText());
        object.addProperty("tiktok", binding.layoutTiktok.getText());

        Log.d("Dubai", "updateProfile: " + object);

        if (!TextUtils.isEmpty( imageUrl )) {
            object.addProperty( "image", imageUrl );
        }
        showProgress();
        SessionManager.shared.updateProfile( activity, object, (success, error) -> {
            hideProgress();
            if (!Utils.isNullOrEmpty( error )) {
                Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                return;
            }

            if (success) {
//                if (!oldEmail.equals( email )) {
//                    verifyEmail();
//                }
                AppSettingManager.shared.reloadHomeFragment.onReceive(true);
                Toast.makeText( activity, "Profile Updated Successfully", Toast.LENGTH_SHORT ).show();
                getProfileDetails();
                finish();

            }
        } );


    }

    public void requestImageUpload(Uri imageData) {
        showProgress();
        DataService.shared( activity ).requestUploadImage( activity, imageData, new RestCallback<ContainerModel<ImageUploadModel>>(this) {
            @Override
            public void result(ContainerModel<ImageUploadModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty( error )) {
                    Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                if (model.getData() != null && !model.getData().getUrl().isEmpty()) {
                    imageUrl = model.getData().getUrl();
                    Graphics.loadImage( imageUrl, binding.ivProfile );
                    updateProfile();
                }
            }
        } );
    }

    private void requestSentOtp(String type) {
        JsonObject object = new JsonObject();
        object.addProperty( "type", type );
        showProgress();
        DataService.shared( activity ).requestSentOtp( object, new RestCallback<ContainerModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty( error )) {
                    Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                Toast.makeText( activity, model.message, Toast.LENGTH_SHORT ).show();
            }

        } );
    }

    private void requestUserProfile() {
        SessionManager.shared.getCurrentUserProfile( this, (success, error) -> {
            if (!Utils.isNullOrEmpty( error )) {
                Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                return;
            }
            getProfileDetails();
        } );
    }

    private void requestDeleteUserAccount(String type) {
        showProgress();
        DataService.shared( activity ).requestUserDeleteAccount( type, new RestCallback<ContainerModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty( error )) {
                    Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                Toast.makeText( activity, model.message, Toast.LENGTH_SHORT ).show();
                SessionManager.shared.clearSessionData( activity );
                startActivity( new Intent( activity, AuthenticationActivity.class ).addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK ).addFlags( Intent.FLAG_ACTIVITY_NEW_TASK ) );
                finish();

            }
        } );
    }

}
