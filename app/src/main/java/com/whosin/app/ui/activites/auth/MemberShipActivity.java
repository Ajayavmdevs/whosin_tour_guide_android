package com.whosin.app.ui.activites.auth;


import android.view.View;

import com.whosin.app.R;
import com.whosin.app.databinding.ActivityMemberShipBinding;
import com.whosin.app.ui.activites.comman.BaseActivity;

public class MemberShipActivity extends BaseActivity {

    private ActivityMemberShipBinding binding;
    private String isSelect;
    // --------------------------------------
    // region Life Cycle
    // --------------------------------------


    @Override
    protected void initUi() {

    }

    @Override
    protected void setListeners() {
        binding.navbar.getBackBtn().setOnClickListener(view -> {
            onBackPressed();
        });
        binding.txtPlatinum.setOnClickListener(view -> selectGender("EK Platinum"));
        binding.txtEmirates.setOnClickListener(view -> selectGender("Emirates NBD"));
        binding.txtFaceCard.setOnClickListener(view -> selectGender("FACECARD"));
        binding.btnSkip.setOnClickListener(view -> selectGender("Skip"));

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityMemberShipBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    private void selectGender(String type) {

        binding.txtPlatinum.setBackground(getResources().getDrawable(R.drawable.img_input_bg));
        binding.txtEmirates.setBackground(getResources().getDrawable(R.drawable.img_input_bg));
        binding.txtFaceCard.setBackground(getResources().getDrawable(R.drawable.img_input_bg));
        binding.btnSkip.setBackground(getResources().getDrawable(R.drawable.img_input_bg));

        isSelect = type;

        switch (type) {
            case "EK Platinum":
                binding.txtPlatinum.setBackground(getResources().getDrawable(R.drawable.brand_pink_bg));
                binding.txtPlatinum.setTextColor(getResources().getColor(R.color.white));

                break;
            case "Emirates NBD":
                binding.txtEmirates.setBackground(getResources().getDrawable(R.drawable.brand_pink_bg));
                binding.txtEmirates.setTextColor(getResources().getColor(R.color.white));

                break;
            case "FACECARD":
                binding.txtFaceCard.setBackground(getResources().getDrawable(R.drawable.brand_pink_bg));
                binding.txtFaceCard.setTextColor(getResources().getColor(R.color.white));

                break;
        }
    }

    // endregion
    // --------------------------------------

}