package com.whosin.app.ui.fragment.search;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whosin.app.R;
import com.whosin.app.databinding.FragmentYachtsBinding;
import com.whosin.app.ui.fragment.comman.BaseFragment;

public class YachtsFragment extends BaseFragment {

    private FragmentYachtsBinding binding;


    @Override
    public void initUi(View view) {
        binding = FragmentYachtsBinding.bind(view);

    }

    @Override
    public void setListeners() {

    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_yachts;
    }
}