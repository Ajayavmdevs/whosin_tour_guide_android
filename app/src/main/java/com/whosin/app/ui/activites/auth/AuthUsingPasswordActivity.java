package com.whosin.app.ui.activites.auth;


import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.ActivityAuthUsingPasswordBinding;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.ui.activites.home.MainHomeActivity;
import com.whosin.app.ui.activites.comman.BaseActivity;

public class AuthUsingPasswordActivity extends BaseActivity {

    private ActivityAuthUsingPasswordBinding binding;

    public String userId = "";

    // --------------------------------------
    // region Life Cycle
    // --------------------------------------

    @Override
    protected void initUi() {
        binding.editTextPassword.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        userId =  getIntent().getStringExtra( "userId" );
    }

    @Override
    protected void setListeners() {

        binding.resetPassword.setOnClickListener(view -> {
            startActivity(new Intent(AuthUsingPasswordActivity.this, ResetPasswordActivity.class));
        });

        binding.btnContinue.setOnClickListener(view -> {
            verifyPassword();
        });
    }


    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityAuthUsingPasswordBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    // endregion
    // --------------------------------------
    // region Data/Services
    // --------------------------------------

    private void verifyPassword() {

        SessionManager.shared.verifyPassword(userId, binding.editTextPassword.getText().toString(), activity, (success, error) -> {
            if (!Utils.isNullOrEmpty(error)) {
                Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                return;
            }
            if (success) {
                if (SessionManager.shared.getUser().getFirstName().isEmpty() || SessionManager.shared.getUser().getLastName().isEmpty()) {
                    startActivity(new Intent(activity, AuthUserNameActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                } else {
                    startActivity(new Intent(activity, MainHomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                }
            }
        });
    }

    // endregion
    // --------------------------------------

}