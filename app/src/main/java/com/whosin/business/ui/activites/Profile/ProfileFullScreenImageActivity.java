package com.whosin.business.ui.activites.Profile;

import android.view.View;

import com.whosin.business.comman.Graphics;
import com.whosin.business.databinding.ActivityProfileFullScreenImageBinding;
import com.whosin.business.service.manager.SessionManager;
import com.whosin.business.ui.activites.comman.BaseActivity;

public class ProfileFullScreenImageActivity extends BaseActivity {

    private ActivityProfileFullScreenImageBinding binding;

    public static  String EXTRA_IMAGE_URL = "extra_image_url";

    @Override
    protected void initUi() {

        String imageUrl = getIntent().getStringExtra( EXTRA_IMAGE_URL );
        Graphics.loadImageWithFirstLetter( imageUrl,binding.ivImage , SessionManager.shared.getUser().getFullName() );


    }

    @Override
    protected void setListeners() {

        binding.ivBack.setOnClickListener( v-> {
            onBackPressed();
        } );


    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityProfileFullScreenImageBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}