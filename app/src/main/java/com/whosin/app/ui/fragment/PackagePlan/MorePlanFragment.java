package com.whosin.app.ui.fragment.PackagePlan;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whosin.app.R;
import com.whosin.app.databinding.FragmentMorePlanBinding;
import com.whosin.app.ui.fragment.comman.BaseFragment;


public class MorePlanFragment extends BaseFragment {


    // --------------------------------------
    // region Life Cycle
    // --------------------------------------

    private FragmentMorePlanBinding binding;


    @Override
    public void initUi(View view) {
        binding = FragmentMorePlanBinding.bind( view );

    }

    @Override
    public void setListeners() {

    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_more_plan;
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



    // endregion
    // --------------------------------------
}