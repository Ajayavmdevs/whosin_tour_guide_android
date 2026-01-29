package com.whosin.business.ui.fragment.Chat;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.whosin.business.R;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.interfaces.CommanCallback;
import com.whosin.business.databinding.FragmentShareDocumentBottomSheetBinding;

public class ReportAndBlockBottomSheet extends DialogFragment {

    private FragmentShareDocumentBottomSheetBinding binding;

    public CommanCallback<Boolean> callback;

    public CommanCallback<Boolean> reportSheetCallBack = null;

    public CommanCallback<Boolean> reportAndBlockCallBack = null;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        initUi(view);
        setListener();
        return view;
    }

    private void setListener() {

        binding.reportLayout.setOnClickListener(v -> {
            if (reportSheetCallBack != null) {
                reportSheetCallBack.onReceive(true);
            }
            dismiss();
        });

        binding.reportAndBlockLayout.setOnClickListener(v -> {
            if (reportAndBlockCallBack != null) {
                reportAndBlockCallBack.onReceive(true);
            }
            dismiss();
        });

        binding.blockUserLayout.setOnClickListener(v -> {
            if (callback != null){
                callback.onReceive(true);
                dismiss();
            }
        });




    }


    private void initUi(View view) {

        binding = FragmentShareDocumentBottomSheetBinding.bind(view);

        binding.tvReportTitle.setText(Utils.getLangValue("report_to_Whosin"));
        binding.tvReportSubTitle.setText(Utils.getLangValue("block_and_report_message"));

        binding.tvReportAndBlock.setText(Utils.getLangValue("report_and_block"));
        binding.tvBlockTitle.setText(Utils.getLangValue("block"));
        binding.tvReportTitle.setText(Utils.getLangValue("report"));

    }

    public int getLayoutRes() {
        return R.layout.fragment_share_document_bottom_sheet;
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


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    // endregion
    // --------------------------------------
}