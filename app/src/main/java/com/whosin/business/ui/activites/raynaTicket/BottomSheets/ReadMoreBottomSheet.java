package com.whosin.business.ui.activites.raynaTicket.BottomSheets;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.DialogFragment;

import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.whosin.business.R;
import com.whosin.business.databinding.FragmentReadMoreBottomSheetBinding;

public class ReadMoreBottomSheet extends DialogFragment {

    private FragmentReadMoreBottomSheetBinding binding;

    public String title = "";

    public String formattedDescription = "";


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        initUi(view);
        setListener();
        return view;


    }

    private void initUi(View v) {
        binding = FragmentReadMoreBottomSheetBinding.bind(v);

        Glide.with(requireActivity()).load(R.drawable.icon_close).into(binding.ivClose);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if (!TextUtils.isEmpty(title)) binding.tvTitle.setText(title);

        binding.tvDescription.setMovementMethod(LinkMovementMethod.getInstance());
        binding.tvDescription.setText(Html.fromHtml(formattedDescription
                .replace("<li>", "<li>&nbsp;&nbsp;")
                .replace("</li>", "</li>")
                .replace("</ul>", "</ul><br>"), HtmlCompat.FROM_HTML_MODE_LEGACY
        ));

    }

    private void setListener() {
        binding.ivClose.setOnClickListener(view -> dismiss());
    }

    public int getLayoutRes() {
        return R.layout.fragment_read_more_bottom_sheet;
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

    @Override
    public void onResume() {
        super.onResume();
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