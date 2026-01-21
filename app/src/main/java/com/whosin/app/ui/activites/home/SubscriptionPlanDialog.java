package com.whosin.app.ui.activites.home;

import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.Graphics;
import com.whosin.app.databinding.SubscriptionPlanDialogBinding;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.models.SubscriptionModel;
import com.whosin.app.ui.activites.venue.TouristPlanActivity;
import com.whosin.app.ui.activites.venue.ui.PaymentActivity;

public class SubscriptionPlanDialog extends DialogFragment {


    private SubscriptionPlanDialogBinding mBinding;

    private SubscriptionModel subscriptionModel;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        initUi(view);
        setListener();
        return view;

    }

    private void initUi(View v) {

        mBinding = SubscriptionPlanDialogBinding.bind(v);
        AppSettingManager.shared.requestSubscriptionCustomPlan(requireActivity());
        subscriptionModel = AppSettingManager.shared.homePostersubscriptionModel;
        if (subscriptionModel != null) {
            if (subscriptionModel.getTitle() != null) {

                Glide.with(requireActivity()).load(R.drawable.icon_close).into(mBinding.ivClose);
                getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{Color.parseColor(subscriptionModel.getStartColor()), Color.parseColor(subscriptionModel.getEndColor())});
                mBinding.getRoot().setBackground(gradientDrawable);

                mBinding.tvTitle.setText(subscriptionModel.getTitle());
                mBinding.tvSubTitle.setText(subscriptionModel.getSubTitle());
                mBinding.tvDescription.setText(subscriptionModel.getDescription());
                mBinding.btnBuyNow.setText( subscriptionModel.getButtonText());
                Graphics.loadImage(subscriptionModel.getImage(), mBinding.ivGIF);



            }
        }else {
            dismiss();
        }
//        subscriptionModel = AppSettingManager.shared.getSubscriptionData();


    }

    private void setListener() {
        mBinding.ivClose.setOnClickListener(view -> dismiss());


//        mBinding.constraint.setBackgroundColor( Integer.parseInt( String.valueOf( Color.parseColor( AppSettingManager.shared.getSubscriptionData().getStartColor() ) ) ));
        mBinding.tvSee.setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), TouristPlanActivity.class).putExtra("packageModel", new Gson().toJson(AppSettingManager.shared.getSubscriptionData())));
        });

        mBinding.btnBuyNow.setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), PaymentActivity.class)
                    .putExtra("packageModel", new Gson().
                            toJson(subscriptionModel.getPackageId())));

        });
    }

    private int getLayoutRes() {
        return R.layout.subscription_plan_dialog;
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