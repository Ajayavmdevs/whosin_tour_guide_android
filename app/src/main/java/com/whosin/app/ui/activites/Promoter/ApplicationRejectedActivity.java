package com.whosin.app.ui.activites.Promoter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;

import com.google.gson.Gson;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.ActivityApplicationRejectedBinding;
import com.whosin.app.service.models.NotificationModel;
import com.whosin.app.ui.activites.comman.BaseActivity;

public class ApplicationRejectedActivity extends BaseActivity {

    private ActivityApplicationRejectedBinding binding;

    private NotificationModel model;
    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @SuppressLint("DefaultLocale")
    @Override
    protected void initUi() {

        binding.tvSubTitle.setText(getValue("your_application_rejected"));
        binding.title.setText(getValue("apply_again"));

        String modelString = getIntent().getStringExtra("model");

        if (!Utils.isNullOrEmpty( modelString )) {
            model = new Gson().fromJson( modelString, NotificationModel.class );
        }

        if (model != null){
            int remainingDay = Utils.calculateRemainingDays(model.getUpdatedAt());
            if (remainingDay == 0) {
                binding.tvDaysRemaining.setVisibility(View.GONE);
            } else {
                binding.tvDaysRemaining.setText(setValue("days_remaining",String.valueOf(remainingDay)));
            }
        }



    }

    @Override
    protected void setListeners() {

        binding.btnClose.setOnClickListener(v -> finish());

        binding.remainningDaysLayout.setOnClickListener(v -> {
            if (model != null) {
                int remainingDay = Utils.calculateRemainingDays(model.getUpdatedAt());
                if (remainingDay == 0) {
                    if (model.getType().equals("ring-request-rejected")) {
                        startActivity(new Intent(activity, PromoterActivity.class).putExtra("isPromoter", false));
                    } else {
                        startActivity(new Intent(activity, PromoterActivity.class).putExtra("isPromoter", true));
                    }
                }
            }
        });
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityApplicationRejectedBinding.inflate(getLayoutInflater());
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


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    // --------------------------------------
    // endregion



}