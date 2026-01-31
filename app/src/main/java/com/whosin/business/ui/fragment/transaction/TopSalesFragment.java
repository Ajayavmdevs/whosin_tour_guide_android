package com.whosin.business.ui.fragment.transaction;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonObject;
import com.whosin.business.R;
import com.whosin.business.comman.Utils;
import com.whosin.business.databinding.FragmentTopSalesBinding;
import com.whosin.business.service.DataService;
import com.whosin.business.service.models.ContainerModel;
import com.whosin.business.service.models.statistics.StatisticsModel;
import com.whosin.business.service.models.statistics.TransactionListModel;
import com.whosin.business.service.rest.RestCallback;
import com.whosin.business.ui.fragment.comman.BaseFragment;

import java.util.List;

public class TopSalesFragment extends BaseFragment {

    private FragmentTopSalesBinding binding;
    private TopSalesAdapter<TransactionListModel> adapter;
    private String fromDate;
    private String toDate;

    @Override
    public void initUi(View view) {
        binding = FragmentTopSalesBinding.bind(view);
        adapter = new TopSalesAdapter<>();
        binding.itemRecycler.setLayoutManager(new LinearLayoutManager(context));
        binding.itemRecycler.setAdapter(adapter);
    }

    public void updateDateFilter(String fromDate, String toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        populateData(true);
    }

    @Override
    public void setListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener(() -> populateData(true));
    }

    @Override
    public void populateData(boolean getDataFromServer) {
        if (!getDataFromServer) return;
        
        binding.swipeRefreshLayout.setRefreshing(true);
        JsonObject jsonObject = new JsonObject();
        
        String fDate = fromDate;
        String tDate = toDate;
        
        String currentDate = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.ENGLISH).format(new java.util.Date());
        
        if (Utils.isNullOrEmpty(fDate)) {
            fDate = currentDate;
        }
        if (Utils.isNullOrEmpty(tDate)) {
            tDate = currentDate;
        }
        
        jsonObject.addProperty("fromDate", fDate);
        jsonObject.addProperty("toDate", tDate);
        
        DataService.shared(context).requestGetStatistics(jsonObject, new RestCallback<ContainerModel<StatisticsModel>>(null) {
            @Override
            public void result(ContainerModel<StatisticsModel> model, String error) {
                binding.swipeRefreshLayout.setRefreshing(false);
                if (!Utils.isNullOrEmpty(error)) {
                    // Handle error if needed, maybe show toast
                    return;
                }
                
                if (model != null && model.getData() != null) {
                    StatisticsModel stats = model.getData();
                    List<TransactionListModel> list = stats.getList();
                    
                    if (list != null && !list.isEmpty()) {
                        adapter.updateData(list);
                        binding.itemRecycler.setVisibility(View.VISIBLE);
                        binding.emptyPlaceHolderView.setVisibility(View.GONE);
                    } else {
                        binding.itemRecycler.setVisibility(View.GONE);
                        binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.itemRecycler.setVisibility(View.GONE);
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_top_sales;
    }
}

