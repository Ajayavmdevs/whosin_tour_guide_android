package com.whosin.app.ui.activites.auth;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.ActivityCreatePasswordBinding;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.ui.activites.home.MainHomeActivity;
import com.whosin.app.ui.activites.comman.BaseActivity;

public class CreatePasswordActivity extends BaseActivity {
    private ActivityCreatePasswordBinding binding;

    @Override
    protected void initUi() {

        binding.editPassword.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService( Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    protected void setListeners() {

        binding.navbar.getBackBtn().setOnClickListener( v -> onBackPressed() );
        binding.imageNext.setOnClickListener( view -> {
            requestPassword();
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

        binding.show.setOnClickListener( view -> {
            showHidePass( binding.editPassword, binding.show );
        } );
        binding.showConfirm.setOnClickListener( view -> {
            showHidePass( binding.edConformPsw, binding.showConfirm );
        } );

        binding.editPassword.addTextChangedListener( editTextListener );
        binding.edConformPsw.addTextChangedListener( editTextListener );
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityCreatePasswordBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }

    private void requestPassword() {

        String password = binding.editPassword.getText().toString();
        String confirmPassword = binding.edConformPsw.getText().toString();

        if (Utils.isNullOrEmpty( password )) {
            Toast.makeText( activity, "Please enter password", Toast.LENGTH_SHORT ).show();
            return;
        }
        if (Utils.isNullOrEmpty( confirmPassword )) {
            Toast.makeText( activity, "Please enter confirm password", Toast.LENGTH_SHORT ).show();
            return;
        }
        if (!binding.edConformPsw.getText().toString().equals( binding.editPassword.getText().toString() )) {
            Toast.makeText( activity, "Please enter valid password", Toast.LENGTH_SHORT ).show();
            return;
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("password", confirmPassword);
        SessionManager.shared.updateProfile( activity, jsonObject,(success, error) -> {
            if (!Utils.isNullOrEmpty(error)) {
                Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                return;
            }
            if (success) {
                if (SessionManager.shared.getUser().getFirstName().isEmpty() && SessionManager.shared.getUser().getLastName().isEmpty()) {
                    startActivity( new Intent( activity, AuthUserNameActivity.class));
                } else {
                    startActivity( new Intent( activity, MainHomeActivity.class ) );
                }
            }
        } );
    }

    private void checkValidation() {

        String password = binding.editPassword.getText().toString();
        String conformPassword = binding.edConformPsw.getText().toString();
        binding.imageNext.setEnable( !password.isEmpty() && !conformPassword.isEmpty() && password.equals( conformPassword ) );

    }

    public void showHidePass(EditText text, ImageView id) {
        if (text.getTransformationMethod().equals( PasswordTransformationMethod.getInstance() ) || text.getTransformationMethod().equals( PasswordTransformationMethod.getInstance() )) {
            //Show Password
            text.setTransformationMethod( HideReturnsTransformationMethod.getInstance() );
            id.setImageResource( R.drawable.password_hide );
        } else {
            //Hide Password
            text.setTransformationMethod( PasswordTransformationMethod.getInstance() );
            id.setImageResource( R.drawable.img_password );
        }
    }


}