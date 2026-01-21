package com.whosin.app.ui.activites.auth;


import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.ActivityTermsConditionBinding;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.ui.activites.comman.BaseActivity;

public class TermsConditionActivity extends BaseActivity {

    private ActivityTermsConditionBinding binding;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

    }

    @Override
    protected void setListeners() {
        binding.navbar.getBackBtn().setOnClickListener( view -> {
            onBackPressed();
        } );
        binding.btnContinue.setOnClickListener( view -> reqUpdateProfile() );
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityTermsConditionBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    public void reqUpdateProfile() {

        SessionManager.shared.updateProfile( activity, SessionManager.shared.jsonObject, (success, error) -> {
            if (!Utils.isNullOrEmpty( error )) {
                Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                return;
            }
            if (success) {
                SessionManager.shared.jsonObject = new JsonObject();
                startActivity( new Intent( activity, AuthContinueAddingActivity.class ) );
            } else {
                Toast.makeText( activity, "wrong" + error, Toast.LENGTH_SHORT ).show();
            }
        } );
    }

}