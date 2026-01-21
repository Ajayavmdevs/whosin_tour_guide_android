package com.whosin.app.ui.activites;

import android.view.View;


import com.whosin.app.R;
import com.whosin.app.databinding.ActivityNoInterntBinding;
import com.whosin.app.service.rest.NetworkConnectivity;
import com.whosin.app.ui.activites.comman.BaseActivity;

public class NoInterntActivity extends BaseActivity {
    ActivityNoInterntBinding binding;

    @Override
    protected void initUi() {

    }

    @Override
    protected void setListeners() {
        binding.btnRetry.setOnClickListener(view -> {
            if (NetworkConnectivity.isConnected(activity))
                finish();
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_no_internt;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityNoInterntBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
}