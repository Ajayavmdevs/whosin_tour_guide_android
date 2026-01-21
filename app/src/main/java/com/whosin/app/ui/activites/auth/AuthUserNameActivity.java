package com.whosin.app.ui.activites.auth;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.ActivityAuthUserNameBinding;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.ui.activites.home.MainHomeActivity;
import com.whosin.app.ui.activites.comman.BaseActivity;

import java.util.HashMap;
import java.util.Map;

public class AuthUserNameActivity extends BaseActivity {

    private ActivityAuthUserNameBinding binding;
    private String selectedGender = "";

    // --------------------------------------
    // region Life Cycle
    // --------------------------------------
    @Override
    protected void initUi() {

        applyTranslations();

        binding.navbar.setgetGreetingText(getValue("add_your_name"));

        binding.editFirstName.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService( Context.INPUT_METHOD_SERVICE );
        imm.toggleSoftInput( InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY );
        getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE );
    }

    @Override
    protected void setListeners() {
        binding.navbar.getBackBtn().setOnClickListener( view -> {
            onBackPressed();
        } );

        binding.imageNext.setOnClickListener( view -> {
            requestUserName();

        } );
        TextWatcher editTextListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkValidation();
            }
        };
        binding.editFirstName.addTextChangedListener( editTextListener );
        binding.editLastName.addTextChangedListener( editTextListener );

        binding.roundMale.setOnClickListener( view -> selectGender( "Male" ) );
        binding.linearFemale.setOnClickListener( view -> selectGender( "Female" ) );
        binding.linearPrefer.setOnClickListener( view -> selectGender( "Prefer not to Say" ) );

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityAuthUserNameBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();

        map.put(binding.title, "what_is_your_name");
        map.put(binding.tvAuthSubTitleText, "your_name_will_on_profile");
        map.put(binding.editFirstName, "first_name");
        map.put(binding.editLastName, "last_name");
        map.put(binding.txt, "please_specify_your_gender");
        map.put(binding.tvMale, "male");
        map.put(binding.tvFemale, "female");
        map.put(binding.tvPreferNotSay, "prefer_not_to_say");



        return map;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------
    private void checkValidation() {
        binding.imageNext.setEnable( !binding.editFirstName.getText().toString().isEmpty() &&
                !binding.editLastName.getText().toString().isEmpty() && !selectedGender.isEmpty() );
    }

    private void requestUserName() {

        String name = binding.editFirstName.getText().toString();
        String lastName = binding.editLastName.getText().toString();

        if (Utils.isNullOrEmpty( name )) {
            Toast.makeText( activity, getValue("please_enter_first_name"), Toast.LENGTH_SHORT ).show();
            return;
        }
        if (Utils.isNullOrEmpty( lastName )) {
            Toast.makeText( activity, getValue("please_enter_last_name"), Toast.LENGTH_SHORT ).show();
            return;
        }
        if (Utils.isNullOrEmpty( selectedGender )) {
            Toast.makeText( activity, getValue("please_select_gender"), Toast.LENGTH_SHORT ).show();
            return;
        }

        requestImageUpdate( name, lastName );


    }

    private void selectGender(String type) {

        binding.roundMale.setBackground( getResources().getDrawable( R.drawable.img_input_bg ) );
        binding.linearFemale.setBackground( getResources().getDrawable( R.drawable.img_input_bg ) );
        binding.linearPrefer.setBackground( getResources().getDrawable( R.drawable.img_input_bg ) );

        selectedGender = type;
        checkValidation();
        switch (type) {
            case "Male":
                binding.roundMale.setBackground( getResources().getDrawable( R.drawable.brand_pink_bg ) );
                Utils.hideKeyboard( activity );
                break;
            case "Female":
                binding.linearFemale.setBackground( getResources().getDrawable( R.drawable.brand_pink_bg ) );
                Utils.hideKeyboard( activity );
                break;
            case "Prefer not to Say":
                binding.linearPrefer.setBackground( getResources().getDrawable( R.drawable.brand_pink_bg ) );
                Utils.hideKeyboard( activity );
                break;
        }

    }


    private void requestImageUpdate(String name, String lastName) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty( "first_name", name );
        jsonObject.addProperty( "last_name", lastName );
        jsonObject.addProperty( "gender", selectedGender.toLowerCase() );

        showProgress();
        SessionManager.shared.updateProfile( activity, jsonObject, (success, error) -> {
            hideProgress();
            if (!Utils.isNullOrEmpty( error )) {
                Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                return;
            }
            if (success) {
                startActivity( new Intent( activity, MainHomeActivity.class ) );
                activity.finish();

            }
        } );
    }

    // endregion
    // --------------------------------------
}