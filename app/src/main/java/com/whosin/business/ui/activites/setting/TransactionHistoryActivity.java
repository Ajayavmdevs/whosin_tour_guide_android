package com.whosin.business.ui.activites.setting;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.whosin.business.R;
import com.whosin.business.comman.AppConstants;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.interfaces.TransCallBack;
import com.whosin.business.databinding.ActivityTransactionHistoryBinding;
import com.whosin.business.service.manager.RaynaTicketManager;
import com.whosin.business.service.models.rayna.TourOptionsModel;
import com.whosin.business.ui.activites.comman.BaseActivity;
import com.whosin.business.ui.adapter.ViewPagerAdapter;
import com.whosin.business.ui.fragment.transaction.DateFilterTransactionSheet;
import com.whosin.business.ui.fragment.transaction.PayoutsFragment;
import com.whosin.business.ui.fragment.transaction.TopSalesFragment;
import com.whosin.business.ui.fragment.transaction.TransactionsFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class TransactionHistoryActivity extends BaseActivity implements TransCallBack {

    private ActivityTransactionHistoryBinding binding;
    private ViewPagerAdapter adapter;
    ArrayList<String> data = new ArrayList<>(Arrays.asList(
            "All",
            "Today",
            "Yesterday",
            "This Week",
            "Last Week",
            "Last Month",
            "Last Year",
            "Custom Date"
    ));

    @Override
    protected void initUi() {
        setViewPager(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, R.layout.item_spinner_text_for_rayna, data);
        adapter.setDropDownViewResource(R.layout.item_spinner_text_for_rayna);
        binding.spinnerOptions.setAdapter(adapter);
    }

    @Override
    protected void setListeners() {
        binding.ivClose.setOnClickListener(view -> onBackPressed());
        binding.dateTimeLayout.setOnClickListener(view -> {
            openDateRangeActionSheet();
        });
        binding.spinnerOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                handleDateRangeSelection(data.get(pos));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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


    private void openDateRangeActionSheet() {
        Graphics.showActionSheet(
                this,
                getString(R.string.app_name),
                data,
                (item, position) -> handleDateRangeSelection(item)
        );
    }


    private void handleDateRangeSelection(String selected) {
        Date today = Utils.getToday();

        switch (selected) {

            case "All":
                clearDateFilter();
                break;

            case "Today":
                applyDate(today, today, "Today");
                break;

            case "Yesterday":
                Date yesterday = Utils.getYesterday();
                applyDate(yesterday, yesterday, "Yesterday");
                break;

            case "This Week":
                applyDate(Utils.getStartOfWeek(), today, "This Week");
                break;

            case "Last Week":
                applyDate(
                        Utils.getStartOfLastWeek(),
                        Utils.getEndOfLastWeek(),
                        "Last Week"
                );
                break;

            case "This Month":
                applyDate(Utils.getStartOfMonth(), today, "This Month");
                break;

            case "Last Month":
                applyDate(Utils.getStartOfLastMonth(), Utils.getEndOfLastMonth(), "Last Month");
                break;

            case "Last Year":
                applyDate(Utils.getStartOfLastYear(), Utils.getEndOfLastYear(), "Last Year");
                break;

            case "Custom Date":
                openCustomDateSheet();
                break;
        }
    }

    private void applyDate(Date startDate, Date endDate, String label) {
        String apiStartDate = Utils.formatDate(startDate, "yyyy-MM-dd");
        String apiEndDate = Utils.formatDate(endDate, "yyyy-MM-dd");

        binding.selectDate.setText(label);

        updateFragments(apiStartDate, apiEndDate);
    }


    private void openCustomDateSheet() {
        DateFilterTransactionSheet sheet = new DateFilterTransactionSheet();
        sheet.callback = data -> {
            if (data != null && !data.isEmpty()) {

                String apiStartDate = data.get(0);
                String apiEndDate = data.size() > 1 ? data.get(data.size() - 1) : apiStartDate;

                String startDate = Utils.changeDateFormat(
                        apiStartDate,
                        "yyyy-MM-dd",
                        AppConstants.DATEFORMT_DD_MM_YYYY
                );

                if (data.size() > 1) {
                    String endDate = Utils.changeDateFormat(
                            apiEndDate,
                            "yyyy-MM-dd",
                            AppConstants.DATEFORMT_DD_MM_YYYY
                    );
                    binding.selectDate.setText(startDate + " - " + endDate);
                } else {
                    binding.selectDate.setText(startDate);
                }

                updateFragments(apiStartDate, apiEndDate);
            }
        };
        sheet.show(getSupportFragmentManager(), "DateFilter");
    }

    private void updateFragments(String startDate, String endDate) {
        if (adapter == null) return;

        for (int i = 0; i < adapter.getCount(); i++) {
            Fragment fragment = adapter.getItem(i);

            if (fragment instanceof TopSalesFragment) {
                ((TopSalesFragment) fragment).updateDateFilter(startDate, endDate);
            } else if (fragment instanceof TransactionsFragment) {
                ((TransactionsFragment) fragment).updateDateFilter(startDate, endDate);
            }
        }
    }

    private void clearDateFilter() {
        binding.selectDate.setText("All");
        updateFragments("", "");
    }


    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        adapter.addFrag(new TopSalesFragment(this), getValue("Most Sales"));
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

    @Override
    public void onReceive(String totalSale, String totalProfit) {
        Utils.setStyledText(activity, binding.ivTopSalesAmount, totalSale);
        Utils.setStyledText(activity, binding.ivTotalProfitAmount, totalProfit);
    }
}
