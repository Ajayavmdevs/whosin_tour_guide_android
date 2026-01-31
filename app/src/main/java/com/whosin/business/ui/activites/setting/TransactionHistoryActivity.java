package com.whosin.business.ui.activites.setting;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.whosin.business.comman.AppConstants;
import com.whosin.business.comman.Utils;
import com.whosin.business.databinding.ActivityTransactionHistoryBinding;
import com.whosin.business.ui.activites.comman.BaseActivity;
import com.whosin.business.ui.adapter.ViewPagerAdapter;
import com.whosin.business.ui.fragment.transaction.DateFilterTransactionSheet;
import com.whosin.business.ui.fragment.transaction.PayoutsFragment;
import com.whosin.business.ui.fragment.transaction.TopSalesFragment;
import com.whosin.business.ui.fragment.transaction.TransactionsFragment;

public class TransactionHistoryActivity extends BaseActivity {

    private ActivityTransactionHistoryBinding binding;
    private ViewPagerAdapter adapter;

    @Override
    protected void initUi() {
        setViewPager(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        binding.ivClearView.setVisibility(View.GONE);
    }

    @Override
    protected void setListeners() {
        binding.ivClose.setOnClickListener(view -> onBackPressed());
        binding.ivDateView.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            DateFilterTransactionSheet selectDateTimeDialog = new DateFilterTransactionSheet();
            selectDateTimeDialog.callback = data -> {
                if (data != null && !data.isEmpty()) {
                    String startDate = Utils.changeDateFormat(data.get(0), "yyyy-MM-dd", AppConstants.DATEFORMT_DD_MM_YYYY);
                    String apiStartDate = data.get(0);
                    String apiEndDate = apiStartDate;
                    
                    if (data.size() > 1) {
                        String endDate = Utils.changeDateFormat(data.get(data.size() - 1), "yyyy-MM-dd", AppConstants.DATEFORMT_DD_MM_YYYY);
                        binding.selectDate.setText(startDate + " - " + endDate);
                        apiEndDate = data.get(data.size() - 1);
                    } else {
                        binding.selectDate.setText(startDate);
                    }
                    binding.ivClearView.setVisibility(View.VISIBLE);
                    
                    if (adapter != null && adapter.getCount() > 0) {
                        for (int i = 0; i < adapter.getCount(); i++) {
                            Fragment fragment = adapter.getItem(i);
                            if (fragment instanceof TopSalesFragment) {
                                ((TopSalesFragment) fragment).updateDateFilter(apiStartDate, apiEndDate);
                            } else if (fragment instanceof TransactionsFragment) {
                                ((TransactionsFragment) fragment).updateDateFilter(apiStartDate, apiEndDate);
                            }
                        }
                    }
                }
            };
            selectDateTimeDialog.show(getSupportFragmentManager(), "");
        });

        binding.ivClearView.setOnClickListener(view -> {
            binding.selectDate.setText("");
            binding.ivClearView.setVisibility(View.GONE);
            
            if (adapter != null && adapter.getCount() > 0) {
                for (int i = 0; i < adapter.getCount(); i++) {
                    Fragment fragment = adapter.getItem(i);
                    if (fragment instanceof TopSalesFragment) {
                        ((TopSalesFragment) fragment).updateDateFilter(null, null);
                    } else if (fragment instanceof TransactionsFragment) {
                        ((TransactionsFragment) fragment).updateDateFilter(null, null);
                    }
                }
            }
        });
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
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        adapter.addFrag(new TopSalesFragment(), getValue("Most Sales"));
        adapter.addFrag(new TransactionsFragment(), "Transactions");
        adapter.addFrag(new PayoutsFragment(), getValue("Payouts"));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        binding.tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

}
