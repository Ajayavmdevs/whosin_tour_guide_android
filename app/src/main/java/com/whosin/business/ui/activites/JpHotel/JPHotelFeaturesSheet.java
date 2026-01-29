package com.whosin.business.ui.activites.JpHotel;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.whosin.business.R;
import com.whosin.business.databinding.JpHotelFeaturesBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JPHotelFeaturesSheet extends DialogFragment {

    private JpHotelFeaturesBinding binding;

    public Activity activity;

    public List<String> features = new ArrayList<>();


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
        setListener();
        return view;
    }

    private void initUi(View v) {

        binding = JpHotelFeaturesBinding.bind(v);

        if (activity == null) {
            activity = requireActivity();
        }

        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Glide.with(requireActivity()).load(R.drawable.icon_close).into(binding.ivClose);

        if (features.isEmpty()) dismiss();

        binding.tvHotelFeatures.setVisibility(View.VISIBLE);
        binding.tvHotelFeatures.removeAllViews();
        binding.tvHotelFeatures.setColumnCount(2);
        for (String feature : features) {
            TextView textView = new TextView(activity);
            textView.setText("• " + feature);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.rayna_more_info_title));
            textView.setTextColor(ContextCompat.getColor(activity, R.color.white));
            textView.setTypeface(ResourcesCompat.getFont(activity, R.font.sf_medium));
            textView.setPadding(0, 10, 10, 10);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            textView.setLayoutParams(params);

            binding.tvHotelFeatures.addView(textView);

//        binding.tvSelectParticipant.setText("Select Participants");


        }
    }

    private void setListener() {

        binding.ivClose.setOnClickListener(v -> dismiss());

    }

    public int getLayoutRes() {
        return R.layout.jp_hotel_features;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            assert bottomSheet != null;
            ViewGroup.LayoutParams layoutParam = bottomSheet.getLayoutParams();
            layoutParam.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParam.width = ViewGroup.LayoutParams.MATCH_PARENT;
            bottomSheet.setLayoutParams(layoutParam);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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


