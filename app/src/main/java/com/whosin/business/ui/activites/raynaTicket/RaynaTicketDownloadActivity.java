package com.whosin.business.ui.activites.raynaTicket;

import android.view.View;

import com.whosin.business.databinding.ActivityRaynaTicketDownloadBinding;
import com.whosin.business.service.models.rayna.RaynaTicketBookingModel;
import com.whosin.business.ui.activites.comman.BaseActivity;

public class RaynaTicketDownloadActivity extends BaseActivity {

    private ActivityRaynaTicketDownloadBinding binding;

    private RaynaTicketBookingModel raynaTicketBookingModel;

    private String formattedRefundAmount = "";


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
       binding = ActivityRaynaTicketDownloadBinding.inflate(getLayoutInflater());
       return binding.getRoot();
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------



    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

}