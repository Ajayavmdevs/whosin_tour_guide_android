package com.whosin.business.ui.activites.auth;


import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.whosin.business.R;
import com.whosin.business.databinding.ActivityResetPasswordBinding;
import com.whosin.business.ui.activites.comman.BaseActivity;

public class ResetPasswordActivity extends BaseActivity {

    private ActivityResetPasswordBinding binding;

    // --------------------------------------
    // region Life Cycle
    // --------------------------------------


    @Override
    protected void initUi() {

    }

    @Override
    protected void setListeners() {
        binding.navbar.getBackBtn().setOnClickListener(view -> {
            onBackPressed();
        });
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

        binding.show.setOnClickListener(view -> {
            showHidePass(binding.editPassword, binding.show);
        });
        binding.showConfirm.setOnClickListener(view -> {
            showHidePass(binding.confirmPassword, binding.showConfirm);
        });
        binding.editPassword.addTextChangedListener(editTextListener);
        binding.confirmPassword.addTextChangedListener(editTextListener);

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    // region Private
    // --------------------------------------

    private void checkValidation() {

        String password = binding.editPassword.getText().toString();
        String conformPassword = binding.confirmPassword.getText().toString();
        binding.imageNext.setEnable(!password.isEmpty() && !conformPassword.isEmpty() && password.equals(conformPassword));
    }

    private void showHidePass(EditText text, ImageView id) {
        if (text.getTransformationMethod().equals(PasswordTransformationMethod.getInstance()) || text.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
            //Show Password
            text.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            id.setImageResource(R.drawable.img_password);
        } else {
            //Hide Password
            text.setTransformationMethod(PasswordTransformationMethod.getInstance());
            id.setImageResource(R.drawable.password_hide);
        }
    }
// endregion
    // --------------------------------------


    // endregion
    // --------------------------------------

}