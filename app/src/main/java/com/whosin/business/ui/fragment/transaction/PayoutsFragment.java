package com.whosin.business.ui.fragment.transaction;

import android.view.View;

import com.whosin.business.R;
import com.whosin.business.databinding.FragmentPayoutBinding;
import com.whosin.business.ui.fragment.comman.BaseFragment;

public class PayoutsFragment extends BaseFragment {

    private FragmentPayoutBinding binding;

    @Override
    public void initUi(View view) {
        binding = FragmentPayoutBinding.bind(view);
        binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
    }

    @Override
    public void setListeners() {

    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_payout;
    }
}
