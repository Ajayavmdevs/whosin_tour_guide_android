package com.whosin.app.ui.activites.venue;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.whosin.app.R;
import com.whosin.app.comman.Graphics;
import com.whosin.app.databinding.ActivitySelectMemberPackageBinding;
import com.whosin.app.ui.activites.comman.BaseActivity;

public class SelectMemberPackageActivity extends BaseActivity {

    private ActivitySelectMemberPackageBinding binding;

    // --------------------------------------
    // region Life Cycle
    // --------------------------------------


    @Override
    protected void initUi() {


    }

    @Override
    protected void setListeners() {
        binding.ivClose.setOnClickListener( view -> onBackPressed() );
        Glide.with( activity ).load( R.drawable.icon_close ).into( binding.ivClose );
        binding.annualCheckBox.setOnClickListener( view -> selectCheckBox() );

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivitySelectMemberPackageBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }

    // --------------------------------------
    // region Private
    // --------------------------------------

    private void selectCheckBox() {

        if (binding.annualCheckBox.isChecked()) {
            binding.annualCheckBox.getResources().getDrawable( R.drawable.check_box_select );
        } else {
            binding.annualCheckBox.getResources().getDrawable( R.drawable.icon_check_box );

        }

    }


    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // --------------------------------------
    // region Adapter
    // --------------------------------------


}