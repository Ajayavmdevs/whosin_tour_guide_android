package com.whosin.app.ui.fragment.CmProfile.CmBottomSheets;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whosin.app.R;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.FragmentEventFAQDialogBinding;

import java.util.Objects;

public class EventFAQDialog extends DialogFragment {

    private FragmentEventFAQDialogBinding binding;

    public CommanCallback<Boolean> callback;

    public CommanCallback<Boolean> faqCallBack;

    public String message = "";

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
        return R.layout.fragment_event_f_a_q_dialog;
    }

    private void initUi(View view) {
        binding = FragmentEventFAQDialogBinding.bind(view);

        binding.titleTv.setText(Utils.getLangValue("no"));
        binding.btnTitle.setText(Utils.getLangValue("yes"));

        if (!TextUtils.isEmpty(message)){
            binding.tvMessage.setText(message);
        }

        assert getDialog() != null;
        Objects.requireNonNull(getDialog().getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    private void setListeners() {
        binding.noButton.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            dismiss();
        });

        binding.yesButton.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            if (callback != null) {
                callback.onReceive(true);
                dismiss();
            }
        });

        binding.FaqButton.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            if (faqCallBack != null) {
                faqCallBack.onReceive(true);
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