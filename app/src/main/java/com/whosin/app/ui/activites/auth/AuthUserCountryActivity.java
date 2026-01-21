package com.whosin.app.ui.activites.auth;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.hbb20.CountryCodePicker;
import com.whosin.app.R;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.ActivityAuthUserCountryBinding;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.ui.activites.comman.BaseActivity;

public class AuthUserCountryActivity extends BaseActivity {
    private ActivityAuthUserCountryBinding binding;
    private String selectedDate = "";
    private String selectedGender;
    private String selectedCountry;


    // --------------------------------------
    // region Life Cycle
    // --------------------------------------

    @Override
    protected void initUi() {
        binding.editTextDate.setShowSoftInputOnFocus( false );
        binding.editTextDate.setCursorVisible( false );
    }

    @Override
    protected void setListeners() {

        binding.navbar.getBackBtn().setOnClickListener( view -> {
            onBackPressed();
        } );
        binding.roundMale.setOnClickListener( view -> selectGender( "Male" ) );
        binding.linearFemale.setOnClickListener( view -> selectGender( "Female" ) );
        binding.linearPrefer.setOnClickListener( view -> selectGender( "Perfer Not to Say" ) );
        binding.imageNext.setOnClickListener( view -> {
            requestDetails();
        } );

        View.OnClickListener onClickListener = view -> {
            DatePickerDialog datePickerDialog = Utils.getDatePickerDialog( activity, data -> {
                        selectedDate = Utils.formatDate( data, "EEE, dd MMM yyyy" );
                        binding.editTextDate.setText( selectedDate );
                        checkValidation();
                    }
            );
            datePickerDialog.getDatePicker().setMaxDate( System.currentTimeMillis() );
            datePickerDialog.show();
        };

        binding.btnDate.setOnClickListener( onClickListener );
        binding.editTextDate.setOnClickListener( onClickListener );

        binding.countryCode.setOnCountryChangeListener( () -> {
            selectedCountry = binding.countryCode.getSelectedCountryName();
            checkValidation();
        } );

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityAuthUserCountryBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }

    // region Private
    // --------------------------------------
    private void selectGender(String type) {

        binding.roundMale.setBackground( getResources().getDrawable( R.drawable.img_input_bg ) );
        binding.linearFemale.setBackground( getResources().getDrawable( R.drawable.img_input_bg ) );
        binding.linearPrefer.setBackground( getResources().getDrawable( R.drawable.img_input_bg ) );

        selectedGender = type;
        checkValidation();
        switch (type) {
            case "Male":
                binding.roundMale.setBackground( getResources().getDrawable( R.drawable.brand_pink_bg ) );
                //  binding.roundMale.setTextColor(getResources().getColor(R.color.white));

                break;
            case "Female":
                binding.linearFemale.setBackground( getResources().getDrawable( R.drawable.brand_pink_bg ) );
                //   binding.linearFemale.setTextColor(getResources().getColor(R.color.white));

                break;
            case "Perfer Not to Say":
                binding.linearPrefer.setBackground( getResources().getDrawable( R.drawable.brand_pink_bg ) );
                //   binding.linearPrefer.setTextColor(getResources().getColor(R.color.white));

                break;
        }
        /*binding.imageNext.setEnable(true);*/

    }

    private void checkValidation() {
        boolean isEnable = !TextUtils.isEmpty( selectedCountry ) && !TextUtils.isEmpty( selectedDate ) && !TextUtils.isEmpty( selectedGender );
        binding.imageNext.setEnable( isEnable );
    }

    private void requestDetails() {
        SessionManager.shared.jsonObject.addProperty( "gender", selectedGender );
        SessionManager.shared.jsonObject.addProperty( "dob", selectedDate );
        SessionManager.shared.jsonObject.addProperty( "country", selectedCountry );
        startActivity( new Intent( this, TermsConditionActivity.class ) );
    }
}

// endregion
// --------------------------------------


// endregion
// --------------------------------------

