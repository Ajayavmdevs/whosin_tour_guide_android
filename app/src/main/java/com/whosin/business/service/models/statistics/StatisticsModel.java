package com.whosin.business.service.models.statistics;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class StatisticsModel {

    @SerializedName("totalSale")
    @Expose
    private double totalSale;

    @SerializedName("totalProfit")
    @Expose
    private double totalProfit;

    @SerializedName("vat")
    @Expose
    private double vat;

    @SerializedName("list")
    @Expose
    private List<TransactionListModel> list;

    public double getTotalSale() {
        return totalSale;
    }

    public void setTotalSale(int totalSale) {
        this.totalSale = totalSale;
    }

    public double getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(double totalProfit) {
        this.totalProfit = totalProfit;
    }

    public double getVat() {
        return vat;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }

    public List<TransactionListModel> getList() {
        return list;
    }

    public void setList(List<TransactionListModel> list) {
        this.list = list;
    }
}