package com.whosin.business.ui.fragment.transaction;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.whosin.business.R;
import com.whosin.business.comman.Utils;
import com.whosin.business.databinding.FragmentTransactionBinding;
import com.whosin.business.service.DataService;
import com.whosin.business.service.models.ContainerModel;
import com.whosin.business.service.models.statistics.TransactionListModel;
import com.whosin.business.service.models.statistics.TransactionModel;
import com.whosin.business.service.rest.RestCallback;
import com.whosin.business.ui.fragment.comman.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class TransactionsFragment extends BaseFragment {

    private FragmentTransactionBinding binding;
    private TransactionsAdapter<TransactionListModel> adapter;
    private List<TransactionListModel> transactionList = new ArrayList<>();
    
    private int page = 1;
    private int limit = 10;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    
    private String fromDate;
    private String toDate;

    @Override
    public void initUi(View view) {
        binding = FragmentTransactionBinding.bind(view);
        
        adapter = new TransactionsAdapter<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        binding.itemRecycler.setLayoutManager(layoutManager);
        binding.itemRecycler.setAdapter(adapter);
        
        // Initial load
        populateData(true);
    }

    public void updateDateFilter(String fromDate, String toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        // Reset pagination
        page = 1;
        isLastPage = false;
        transactionList.clear();
        populateData(true);
    }

    @Override
    public void setListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            page = 1;
            isLastPage = false;
            transactionList.clear();
            populateData(true);
        });
        
        binding.itemRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && !isLastPage) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                && firstVisibleItemPosition >= 0) {
                            page++;
                            populateData(true);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void populateData(boolean getDataFromServer) {
        if (!getDataFromServer) return;
        if (isLoading) return;
        
        isLoading = true;
        if (page == 1) {
            binding.swipeRefreshLayout.setRefreshing(true);
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("page", page);
        jsonObject.addProperty("limit", limit);
        
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
        
        DataService.shared(context).requestGetTransactions(jsonObject, new RestCallback<ContainerModel<TransactionModel>>(null) {
            @Override
            public void result(ContainerModel<TransactionModel> model, String error) {
                isLoading = false;
                binding.swipeRefreshLayout.setRefreshing(false);
                
                if (!Utils.isNullOrEmpty(error)) {
                    // Handle error if needed
                    return;
                }
                
                if (model != null && model.getData() != null) {
                    TransactionModel transactionModel = model.getData();
                    List<TransactionListModel> list = transactionModel.getList();
                    
                    if (list != null && !list.isEmpty()) {
                        if (page == 1) {
                            transactionList.clear();
                        }
                        transactionList.addAll(list);
                        
                        // Check if last page
                        if (list.size() < limit) {
                            isLastPage = true;
                        }
                        
                        if (page == 1) {
                            adapter.updateData(transactionList);
                        } else {
                            adapter.updateDataForPaggination(transactionList);
                        }
                        
                        binding.itemRecycler.setVisibility(View.VISIBLE);
                        binding.emptyPlaceHolderView.setVisibility(View.GONE);
                    } else {
                        if (page == 1) {
                            binding.itemRecycler.setVisibility(View.GONE);
                            binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                        }
                        isLastPage = true;
                    }
                } else {
                    if (page == 1) {
                        binding.itemRecycler.setVisibility(View.GONE);
                        binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_transaction;
    }
}
