package com.whosin.business.ui.activites.setting;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

import com.whosin.business.databinding.ActivityMyWalletBinding;
import com.whosin.business.databinding.ActivityTransactionHistoryBinding;
import com.whosin.business.service.manager.AppSettingManager;
import com.whosin.business.ui.activites.comman.BaseActivity;
import com.whosin.business.ui.adapter.ViewPagerAdapter;
import com.whosin.business.ui.fragment.wallet.HistoryFragment;
import com.whosin.business.ui.fragment.wallet.MyItemsFragment;

public class TransactionHistoryActivity extends BaseActivity {

    private ActivityTransactionHistoryBinding binding;

    @Override
    protected void initUi() {
        setViewPager(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
    }

    @Override
    protected void setListeners() {
        binding.ivClose.setOnClickListener(view -> onBackPressed());
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityTransactionHistoryBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        adapter.addFrag(new MyItemsFragment(), getValue("Most Sales"));
        adapter.addFrag(new MyItemsFragment(), "Transactions");
        adapter.addFrag(new HistoryFragment(), getValue("Payouts"));
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

}
