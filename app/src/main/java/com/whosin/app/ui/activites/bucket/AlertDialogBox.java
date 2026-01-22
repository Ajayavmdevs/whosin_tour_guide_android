package com.whosin.app.ui.activites.bucket;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whosin.app.R;
import com.whosin.app.databinding.AlertDialogBoxBinding;


public class AlertDialogBox extends DialogFragment {

    private AlertDialogBoxBinding binding;
    private String message= "";

    public AlertDialogBox(String message) {
        this.message = message;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(getLayoutRes(), container, false);
        initUi(view);
        setListeners();
        return view;
    }


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    public void initUi(View view) {
        binding = AlertDialogBoxBinding.bind(view);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        binding.txtMessage.setText(message);
    }

    public void setListeners() {

        binding.tvOk.setOnClickListener(v -> {
            dismiss();
        });


    }

    public int getLayoutRes() {
        return R.layout.alert_dialog_box;
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