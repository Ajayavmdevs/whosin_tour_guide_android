package com.whosin.app.ui.fragment.PackagePlan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.FragmentPlanDetailBinding;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.models.MemberShipModel;
import com.whosin.app.service.models.MemberShipPackageModel;
import com.whosin.app.service.models.SubscriptionModel;
import com.whosin.app.ui.activites.venue.TouristPlanActivity;

import java.util.Objects;

public class PlanDetailFragment extends DialogFragment {

    private FragmentPlanDetailBinding binding;
    public MemberShipModel memberShipModel;

    public CommanCallback<Boolean> callBack;

    // --------------------------------------
    // region Life Cycle
    // --------------------------------------

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate( getLayoutRes(), container, false );
        initUi( view );
        setListener();
        return view;

    }

    @SuppressLint("SetTextI18n")
    private void initUi(View v) {
        binding = FragmentPlanDetailBinding.bind( v );
        Glide.with( requireActivity() ).load( R.drawable.icon_close ).into( binding.ivClose );
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );

        binding.tvOnBoard.setText(Utils.getLangValue("we_are_glad_to_have_you_onboard"));
        binding.subTv.setText(Utils.getLangValue("access_buy_one_get"));
        binding.tvViewTitle.setText(Utils.getLangValue("view"));

        if (memberShipModel != null) {
            binding.tvPurchasedBundleSub.setText("purchased "+memberShipModel.getTitle());
        }

    }

    private void setListener() {
        binding.ivClose.setOnClickListener( view -> {
            if (callBack != null) {
                callBack.onReceive(false);
            }
            dismiss();
        } );

        binding.viewBtn.setOnClickListener( view -> {
                startActivity( new Intent(requireActivity(), TouristPlanActivity.class).putExtra( "memberShipModel", new Gson().toJson(memberShipModel)));
        } );
    }

    private int getLayoutRes() {
        return R.layout.fragment_plan_detail;
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(getDialog()).setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                if (callBack != null) {
                    callBack.onReceive(false);
                }
                dismiss();
                return true;
            }
            return false;
        });
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------



    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // --------------------------------------
    // region Adapter
    // --------------------------------------



    // endregion
    // --------------------------------------
}