package com.whosin.app.ui.fragment.PromoterCreateEvent;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.whosin.app.R;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.FragmentRequirementsAddDialogBinding;

import java.util.Objects;


public class RequirementsAddDialog extends DialogFragment {

    private FragmentRequirementsAddDialogBinding binding;

    public boolean isEdit = false;

    public String editSting = "";

    public CommanCallback<String> callback;

    public String requirementTitle = "";

    public String hintText = "";

    public boolean isFromCmFrequency = false;


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
        binding = FragmentRequirementsAddDialogBinding.bind( view );
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );

        binding.tvName.setHint(Utils.getLangValue("message_here"));
        binding.tvDone.setHint(Utils.getLangValue("create"));

        Utils.showSoftKeyboard(requireActivity(),view);

        binding.tvTitle.setText(requirementTitle);

        if (!TextUtils.isEmpty(hintText)) binding.tvName.setHint(hintText);

        if (isEdit){
           if (!Utils.isNullOrEmpty(editSting)){
               binding.tvName.setText(editSting);
               binding.tvName.setSelection(editSting.length());
               binding.tvDone.setText(Utils.getLangValue("edit"));
           }
        }

        if (isFromCmFrequency) {
            binding.tvName.setInputType(InputType.TYPE_CLASS_NUMBER);
            if (!Utils.isNullOrEmpty(editSting)){
                binding.tvName.setText(editSting);
                binding.tvName.setSelection(editSting.length());
            }
        }


    }

    private void setListeners() {

        binding.tvDone.setOnClickListener( view -> {
            if (callback != null) {
                if (!Utils.isNullOrEmpty(binding.tvName.getText().toString().trim())) {
                    callback.onReceive(binding.tvName.getText().toString().trim());
                    dismiss();
                } else {
                    Graphics.showAlertDialogWithOkButton(requireActivity(), requireContext().getString(R.string.app_name), Utils.getLangValue("enter_your_text"));
                }

            }
        } );

    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            ViewGroup.LayoutParams layoutParam = bottomSheet.getLayoutParams();
            layoutParam.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParam.width = ViewGroup.LayoutParams.MATCH_PARENT;
            bottomSheet.setLayoutParams(layoutParam);
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        return dialog;
    }


    private int getLayoutRes() {
        return R.layout.fragment_requirements_add_dialog;
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

