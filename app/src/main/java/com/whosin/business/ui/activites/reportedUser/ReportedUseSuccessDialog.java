package com.whosin.business.ui.activites.reportedUser;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.whosin.business.R;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.interfaces.CommanCallback;
import com.whosin.business.databinding.FragmentReportedUseSuccessDialogBinding;

import java.util.Objects;

public class ReportedUseSuccessDialog extends DialogFragment {

    private FragmentReportedUseSuccessDialogBinding binding;

    public CommanCallback<Boolean> callBack;


    // --------------------------------------
    // region Life Cycle
    // --------------------------------------

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        initUi(view);
        setListener();
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void initUi(View v) {
        binding = FragmentReportedUseSuccessDialogBinding.bind(v);
        Glide.with(requireActivity()).load(R.drawable.icon_close).into(binding.ivClose);
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        binding.tvPurchasedBundle.setText(Utils.getLangValue("report_successfully_submitted"));
        binding.subTv.setText(Utils.getLangValue("you_report_helps_us_to_improve"));


    }

    private void setListener() {

        binding.ivClose.setOnClickListener(view -> {
            if (callBack != null){
                callBack.onReceive(true);
            }
            dismiss();
        });

    }

    private int getLayoutRes() {
        return R.layout.fragment_reported_use_success_dialog;
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