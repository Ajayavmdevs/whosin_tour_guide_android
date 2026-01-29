package com.whosin.business.ui.activites.setting;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import com.whosin.business.R;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.interfaces.CommanCallback;
import com.whosin.business.databinding.MarkupDialogBinding;

import java.util.Objects;

public class AddMarkUpDialog extends DialogFragment {
    private MarkupDialogBinding binding;

    public CommanCallback<String> callback;

    public String markup = "";


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate( getLayoutRes(), container, false );
        initUi( view );
        setListeners();
        return view;
    }

    public void initUi(View view) {

        binding = com.whosin.business.databinding.MarkupDialogBinding.bind( view );

        binding.tvDialogTitle.setText(Utils.getLangValue("Markup"));
        binding.tvCancel.setText(Utils.getLangValue("cancel"));
        binding.tvDone.setText(Utils.getLangValue("submit"));
        binding.tvName.setHint(Utils.getLangValue("Enter your markup"));

        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );

        if (!TextUtils.isEmpty(markup)){
            binding.tvName.setText(markup);
        }
        binding.tvName.requestFocus();

    }

    private void setListeners() {

        binding.tvCancel.setOnClickListener( view -> dismiss() );

        binding.tvDone.setOnClickListener( view -> {
            if (TextUtils.isEmpty(binding.tvName.getText().toString())) return;
            callback.onReceive(binding.tvName.getText().toString());
            dismiss();
        } );

    }

    private int getLayoutRes() {
        return R.layout.markup_dialog;
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
