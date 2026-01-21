package com.whosin.app.ui.activites.auth;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.whosin.app.comman.Utils;

import com.whosin.app.databinding.ActivityAuthUserEmailBinding;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.ui.activites.comman.BaseActivity;

public class
AuthUserEmailActivity extends BaseActivity {

    private ActivityAuthUserEmailBinding binding;

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

        String name = getIntent().getStringExtra( "first_name" );

        binding.nameControl.setMainTitle( "Hello"+" "+ name +",\n What’s your email address?" );

        binding.imageNext.setOnClickListener( view -> {

           /* OtpVerificationBottomSheet bottomSheet = new OtpVerificationBottomSheet();
            bottomSheet.show(getSupportFragmentManager(), "1");*/
            requestEmail();

        } );
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityAuthUserEmailBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void requestEmail(){
        String email = binding.editEmail.getText().toString();
        if (Utils.isNullOrEmpty( email )) {
            Toast.makeText( activity, "Please enter email", Toast.LENGTH_SHORT ).show();
            return;
        }
        SessionManager.shared.jsonObject.addProperty( "email",email );

        if(Utils.isValidEmail( email )){
            startActivity(new Intent(this, AuthUserCountryActivity.class));
        }else {
            Toast.makeText( activity, "Please enter valid email", Toast.LENGTH_SHORT ).show();

        }
    }
    // endregion
    // --------------------------------------

}