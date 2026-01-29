package com.whosin.business.ui.activites.auth;


import android.content.Intent;
import android.view.View;

import com.whosin.business.databinding.ActivityAuthContinueAddingBinding;
import com.whosin.business.ui.activites.home.MainHomeActivity;
import com.whosin.business.ui.activites.comman.BaseActivity;
import com.whosin.business.ui.activites.Profile.ProfilePhotoActivity;

public class AuthContinueAddingActivity extends BaseActivity {
    private ActivityAuthContinueAddingBinding binding;

    // --------------------------------------
    // region Life Cycle
    // --------------------------------------

    @Override
    protected void initUi() {

    }

    @Override
    protected void setListeners() {
        binding.txtSkip.setOnClickListener( view -> {
            startActivity( new Intent( AuthContinueAddingActivity.this, MainHomeActivity.class ).addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP ).addFlags( Intent.FLAG_ACTIVITY_NEW_TASK ) );
        } );
        binding.imageNext.setOnClickListener( v -> {
            startActivity( new Intent( AuthContinueAddingActivity.this, ProfilePhotoActivity.class ).addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP ).addFlags( Intent.FLAG_ACTIVITY_NEW_TASK ) );

        } );

    }
    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityAuthContinueAddingBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }

    // endregion
    // --------------------------------------

}