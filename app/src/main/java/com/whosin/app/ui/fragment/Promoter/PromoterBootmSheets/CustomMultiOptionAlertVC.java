package com.whosin.app.ui.fragment.Promoter.PromoterBootmSheets;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import com.whosin.app.R;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.FragmentCustomMultiOptionAlertVCBinding;

import java.util.Objects;

public class CustomMultiOptionAlertVC extends DialogFragment {

    private FragmentCustomMultiOptionAlertVCBinding binding;

    public String error = "";

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
        return R.layout.fragment_custom_multi_option_alert_v_c;
    }

    private void initUi(View view) {
        binding = FragmentCustomMultiOptionAlertVCBinding.bind(view);

        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        binding.tvTitle.setText(Utils.getLangValue("time_sensitive"));
        binding.tvBtnTitle.setText(Utils.getLangValue("get_event_pass"));
        binding.tvCancelTitle.setText(Utils.getLangValue("cancel"));

        binding.tvDescription.setText(error);



    }

    private void setListeners() {

        binding.cancelButton.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            dismiss();
        });

        binding.eventPassBtn.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            if (callback != null){
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