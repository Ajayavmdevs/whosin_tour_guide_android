package com.whosin.business.ui.fragment.wallet;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.whosin.business.R;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.interfaces.CommanCallback;
import com.whosin.business.databinding.FragmentPurchaseSuccessBinding;

import java.io.InputStream;
import java.util.Objects;


public class PurchaseSuccessFragment extends DialogFragment {

    private FragmentPurchaseSuccessBinding binding;
    public CommanCallback<Boolean> callBack;
    public boolean closeCartActivity = false;
    public CommanCallback<Boolean> callBackForRaynaBooking;
    private AnimatedImageDrawable animatedDrawable;


    // --------------------------------------
    // region Life Cycle
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

    @SuppressLint("SetTextI18n")
    private void initUi(View v) {
        binding = FragmentPurchaseSuccessBinding.bind(v);
        Glide.with(requireActivity()).load(R.drawable.icon_close).into(binding.ivClose);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                InputStream inputStream = getResources().openRawResource(R.raw.package_sucess);
                Drawable drawable = Drawable.createFromStream(inputStream, null);
                if (drawable instanceof AnimatedImageDrawable) {
                    animatedDrawable = (AnimatedImageDrawable) drawable;
                    binding.ivSuccess.setImageDrawable(animatedDrawable);
                    animatedDrawable.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        binding.tvPurchasedBundle.setText(Utils.getLangValue("your_transaction_successfully"));
        binding.subTv.setText(Utils.getLangValue("access_your_purchase"));
        binding.tvWalletTitle.setText(Utils.getLangValue("home_tab_wallet"));

        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void setListener() {
        binding.ivClose.setOnClickListener(view -> {
            if (callBackForRaynaBooking != null) {
                callBackForRaynaBooking.onReceive(true);
            }
            if (callBack != null) {
                callBack.onReceive(false);
            }
            dismiss();
        });

        binding.walletBtn.setOnClickListener(view -> {
            if (callBackForRaynaBooking != null) {
                callBackForRaynaBooking.onReceive(true);
            }

            if (callBack != null) {
                callBack.onReceive(true);
            }
            dismiss();
        });
    }

    private int getLayoutRes() {
        return R.layout.fragment_purchase_success;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (animatedDrawable != null && animatedDrawable.isRunning()) {
                animatedDrawable.stop();
            }
        }
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