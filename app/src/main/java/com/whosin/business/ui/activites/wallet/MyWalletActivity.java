package com.whosin.business.ui.activites.wallet;

import androidx.viewpager.widget.ViewPager;

import android.view.View;

import com.whosin.business.R;
import com.whosin.business.databinding.ActivityMyWalletBinding;
import com.whosin.business.service.manager.AppSettingManager;
import com.whosin.business.ui.adapter.ViewPagerAdapter;
import com.whosin.business.ui.fragment.comman.BaseFragment;
import com.whosin.business.ui.fragment.wallet.HistoryFragment;
import com.whosin.business.ui.fragment.wallet.MyItemsFragment;

public class MyWalletActivity extends BaseFragment {

    private ActivityMyWalletBinding binding;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {
        binding = ActivityMyWalletBinding.bind(view);

        binding.tvTitle.setText(getValue("my_wallet"));

        binding.btnDeleteAll.setVisibility(View.GONE);

        setViewPager(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        AppSettingManager.shared.walletHistoryCallBack = data -> {
          if (data) binding.viewPager.setCurrentItem(1,true);
        };

    }

    @Override
    public void setListeners() {



    }

    public void populateData(boolean getDataFromServer) {}

    @Override
    public int getLayoutRes() {
        return R.layout.activity_my_wallet;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager(), 0);
        adapter.addFrag(new MyItemsFragment(), getValue("my_items"));
//        adapter.addFrag(new GiftsFragment(), "Gifts");
        adapter.addFrag(new HistoryFragment(), getValue("history"));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        binding.tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2) {
                    HistoryFragment historyFragment = (HistoryFragment) adapter.instantiateItem(viewPager, position);
                    if (historyFragment != null) {
                        historyFragment.giftHistoryList();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Adapter


    // --------------------------------------
    // endregion
}