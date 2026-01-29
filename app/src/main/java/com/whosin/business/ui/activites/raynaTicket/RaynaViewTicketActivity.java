package com.whosin.business.ui.activites.raynaTicket;

import android.view.View;

import com.whosin.business.databinding.ActivityRaynaViewTicketBinding;
import com.whosin.business.ui.activites.comman.BaseActivity;

public class RaynaViewTicketActivity extends BaseActivity {

    private ActivityRaynaViewTicketBinding binding;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

    }

    @Override
    protected void setListeners() {

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityRaynaViewTicketBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------




    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------



    // endregion
    // --------------------------------------
}