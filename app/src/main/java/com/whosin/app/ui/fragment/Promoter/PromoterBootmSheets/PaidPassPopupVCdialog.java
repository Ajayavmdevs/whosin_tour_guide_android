package com.whosin.app.ui.fragment.Promoter.PromoterBootmSheets;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whosin.app.R;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.FragmentPaidPassPopupVCDialogBinding;
import com.whosin.app.service.models.PromoterPaidPassModel;

import java.util.Objects;

public class PaidPassPopupVCdialog extends DialogFragment {

    private FragmentPaidPassPopupVCDialogBinding binding;

    public PromoterPaidPassModel promoterPaidPassModel;

    public CommanCallback<Boolean> callback;

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
        return R.layout.fragment_paid_pass_popup_v_c_dialog;
    }

    private void initUi(View view) {
        binding = FragmentPaidPassPopupVCDialogBinding.bind(view);

        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        binding.okBtnTitle.setText(Utils.setLangValue("ok"));
        binding.txtPay.setText(Utils.setLangValue("pay"));

        if (promoterPaidPassModel != null) {
            binding.tvAED.setText(promoterPaidPassModel.getTitle());
            binding.tvMessage.setText(promoterPaidPassModel.getDescription());
            binding.vaildDays.setText(String.valueOf(promoterPaidPassModel.getValidityInDays() + " Days"));
        }

    }

    private void setListeners() {

        binding.okButton.setOnClickListener(view -> {
            dismiss();
        });

        binding.payButton.setOnClickListener(view -> {
            if (callback != null) {
                callback.onReceive(true);
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