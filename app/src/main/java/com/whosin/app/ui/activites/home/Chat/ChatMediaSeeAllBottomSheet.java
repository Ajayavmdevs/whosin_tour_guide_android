package com.whosin.app.ui.activites.home.Chat;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.whosin.app.R;
import com.whosin.app.databinding.FragmentChatMediaSeeAllBottomSheetBinding;


public class ChatMediaSeeAllBottomSheet extends DialogFragment {

    private FragmentChatMediaSeeAllBottomSheetBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setStyle( DialogFragment.STYLE_NORMAL, R.style.OtpDialogStyle );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        initUi(view);
        setListener();
        return view;
    }

    public void initUi(View view) {
        binding = FragmentChatMediaSeeAllBottomSheetBinding.bind(view);

    }


    public void setListener() {



    }

    public int getLayoutRes() {
        return R.layout.fragment_chat_media_see_all_bottom_sheet;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getView() != null) {
            getView().post(() -> {
                View parent = (View) getView().getParent();
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(parent);
                int peekHeight = parent.getHeight();
                behavior.setPeekHeight(peekHeight);
            });
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
    }


}