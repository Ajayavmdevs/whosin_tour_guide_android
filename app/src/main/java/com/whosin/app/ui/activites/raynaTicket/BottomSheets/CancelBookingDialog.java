package com.whosin.app.ui.activites.raynaTicket.BottomSheets;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.whosin.app.R;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.FragmentCancelBookingDialogBinding;

import java.util.Objects;

public class CancelBookingDialog extends DialogFragment {

    private FragmentCancelBookingDialogBinding binding;

    public CommanCallback<String> callback ;

    public CommanCallback<String> submitCallback ;

    public String refundAmount;

    public boolean isOctaBooking = false;

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
        return R.layout.fragment_cancel_booking_dialog;
    }

    private void initUi(View view) {
        binding = FragmentCancelBookingDialogBinding.bind(view);

        binding.tvTitle.setText(Utils.getLangValue("cancel_booking"));
        binding.tvBtnTitle.setText(Utils.getLangValue("submit"));
        binding.tvReason.setText(Utils.getLangValue("reason"));

        Glide.with(requireActivity()).load(R.drawable.icon_close).into(binding.ivClose);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if (TextUtils.isEmpty(refundAmount)) refundAmount = "0.0";
        String descriptionText = "";
        if (Objects.equals(refundAmount, "0.0")) {
            descriptionText = Utils.getLangValue("noRefundText");
            binding.tvDescription.setText(descriptionText);
        } else {
            SpannableString styledPrice = Utils.getStyledText(requireActivity(), refundAmount);
            SpannableStringBuilder fullText = new SpannableStringBuilder().append(Utils.getLangValue("refund_message")).append(" ")
                    .append(styledPrice)
                     .append(Utils.getLangValue("customer_support_message"));
             binding.tvDescription.setText(fullText);
        }


        if (isOctaBooking){
            binding.tvDescription.setText(refundAmount);
        }

    }

    private void setListeners() {

        binding.ivClose.setOnClickListener(view -> {
            dismiss();
        });


        binding.submitBtn.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);

            String reason = binding.editMessage.getText().toString(); // Replace with actual reason, if you have one

            if (submitCallback != null) {
                submitCallback.onReceive(reason); // Pass the reason to the callback
            }

            dismiss();
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