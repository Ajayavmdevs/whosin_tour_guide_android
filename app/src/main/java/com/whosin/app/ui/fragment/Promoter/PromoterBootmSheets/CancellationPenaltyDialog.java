package com.whosin.app.ui.fragment.Promoter.PromoterBootmSheets;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.FragmentCancellationPenaltyDialogBinding;
import com.whosin.app.databinding.FragmentCreateBucketDialogBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.EventInOutPenaltyModel;
import com.whosin.app.service.models.PaymentCredentialModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.home.activity.OrderConfirmationDialog;
import com.whosin.app.ui.fragment.wallet.PurchaseSuccessFragment;

import java.util.Objects;

public class CancellationPenaltyDialog extends DialogFragment {

    private FragmentCancellationPenaltyDialogBinding binding;

    public EventInOutPenaltyModel model;

    public CommanCallback<Boolean> callback ;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        initUi(view);
        setListeners();

        return view;
    }

    private int getLayoutRes() {
        return R.layout.fragment_cancellation_penalty_dialog;
    }

    private void initUi(View view) {
        binding = FragmentCancellationPenaltyDialogBinding.bind(view);

        binding.tvTitle.setText(Utils.getLangValue("cancellation_charges"));
        binding.tvDescription.setText(Utils.getLangValue("as_you_have_canceled_alert"));
        binding.faqBtn.setText(Utils.getLangValue("faq"));
        binding.txtNoBtnTitle.setText(Utils.getLangValue("pay"));

        Glide.with(requireActivity()).load(R.drawable.icon_close).into(binding.ivClose);
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        binding.tvDescription.setText(model.getMessage());

    }

    private void setListeners() {

        binding.ivClose.setOnClickListener(view -> {
            dismiss();
        });


        binding.btnPay.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            if (callback != null) {
                callback.onReceive(true);
                dismiss();
            }
        });

        binding.faqBtn.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            if (callback != null) {
                callback.onReceive(false);
                dismiss();
            }
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