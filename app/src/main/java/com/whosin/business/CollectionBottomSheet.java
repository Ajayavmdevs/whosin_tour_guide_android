package com.whosin.business;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.whosin.business.comman.interfaces.CommanCallback;
import com.whosin.business.databinding.FragmentCollectionBottomsheetBinding;

public class CollectionBottomSheet extends DialogFragment {

    private FragmentCollectionBottomsheetBinding binding;

    private CommanCallback<String> listener;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    public CollectionBottomSheet() {

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

    private void initUi(View view) {
        binding = FragmentCollectionBottomsheetBinding.bind(view);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private void setListeners() {
        Glide.with(requireActivity()).load(R.drawable.icon_close_btn).into(binding.ivClose);
        binding.ivClose.setOnClickListener(view -> getDialog().onBackPressed());

        binding.agreenBtn.setOnClickListener(view -> {
            if (listener != null) {
                listener.onReceive("success");
                dismiss();
            }
        });


        binding.notNowBtn.setOnClickListener(view -> {
            dismiss();
        });
    }

    private int getLayoutRes() {
        return R.layout.fragment_collection_bottomsheet;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
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

    public void setListener(CommanCallback<String> listener) {
        this.listener = listener;
    }

// endregion
// --------------------------------------
}