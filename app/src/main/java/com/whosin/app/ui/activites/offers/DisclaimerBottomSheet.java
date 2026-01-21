package com.whosin.app.ui.activites.offers;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.whosin.app.R;
import com.whosin.app.databinding.DisclaimerBottomSheetBinding;
import com.whosin.app.databinding.SelectOfferBottomSheetBinding;
import com.whosin.app.service.models.OffersModel;


public class DisclaimerBottomSheet extends DialogFragment {

    private DisclaimerBottomSheetBinding binding;
    public String disclaimerTitle = "";
    public String disclaimerDescription = "";

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate( getLayoutRes(), container, false );
        initUi( view );
        setListener();
        return view;
    }

    private void initUi(View v) {
        binding = DisclaimerBottomSheetBinding.bind( v );

        binding.tvBucketTitle.setText(disclaimerTitle);
        binding.tvDisclaimerDescription.setText(disclaimerDescription);

    }

    private void setListener() {
        binding.ivClose.setOnClickListener( view -> {
            dismiss();
        } );
    }

    private int getLayoutRes() {
        return R.layout.disclaimer_bottom_sheet;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog( requireActivity(), R.style.BottomSheetDialogThemeNoFloating );
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    // endregion
    // --------------------------------------



}