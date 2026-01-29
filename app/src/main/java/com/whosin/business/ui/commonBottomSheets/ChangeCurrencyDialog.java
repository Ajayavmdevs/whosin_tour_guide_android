package com.whosin.business.ui.commonBottomSheets;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import com.whosin.business.R;
import com.whosin.business.databinding.CurrencyUpdateDialogBinding;
import com.whosin.business.service.manager.TranslationManager;

import java.util.Objects;

public class ChangeCurrencyDialog extends DialogFragment {

    private CurrencyUpdateDialogBinding binding;

    public boolean isUpdateLang = false;

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
        binding = CurrencyUpdateDialogBinding.bind(v);
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        binding.tvPleaseWait.setText(TranslationManager.shared.get("pleaseWait"));
        binding.updatingCurrencyTitle.setText(TranslationManager.shared.get(isUpdateLang ? "updating_language" : "updating_currency"));
    }

    private void setListener() {

    }

    private int getLayoutRes() {
        return R.layout.currency_update_dialog;
    }

    // endregion
    // --------------------------------------

}
