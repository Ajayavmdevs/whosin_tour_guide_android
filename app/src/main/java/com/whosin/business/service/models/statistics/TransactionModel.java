package com.whosin.business.service.models.statistics;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TransactionModel {

    @SerializedName("list")
    @Expose
    private List<TransactionListModel> list;

    @SerializedName("page")
    @Expose
    private int page;

    @SerializedName("limit")
    @Expose
    private int limit;

    @SerializedName("total")
    @Expose
    private int total;

    @SerializedName("totalPages")
    @Expose
    private int totalPages;

    public List<TransactionListModel> getList() {
        return list;
    }

    public void setList(List<TransactionListModel> list) {
        this.list = list;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
