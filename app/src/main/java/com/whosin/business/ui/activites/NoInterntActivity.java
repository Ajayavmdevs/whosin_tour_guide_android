package com.whosin.business.ui.activites;

import android.view.View;


import com.whosin.business.R;
import com.whosin.business.databinding.ActivityNoInterntBinding;
import com.whosin.business.service.rest.NetworkConnectivity;
import com.whosin.business.ui.activites.comman.BaseActivity;

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