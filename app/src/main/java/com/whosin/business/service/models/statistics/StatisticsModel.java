package com.whosin.business.service.models.statistics;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class StatisticsModel {

    @SerializedName("totalSale")
    @Expose
    private int totalSale;

    @SerializedName("totalProfit")
    @Expose
    private double totalProfit;

    @SerializedName("vat")
    @Expose
    private double vat;

    @SerializedName("list")
    @Expose
    private List<StatisticsTicketModel> list;

    public int getTotalSale() {
        return totalSale;
    }

    public double getTotalProfit() {
        return totalProfit;
    }

    public double getVat() {
        return vat;
    }

    public List<StatisticsTicketModel> getList() {
        return list;
    }
}


class StatisticsTicketModel {

    @SerializedName("quantity")
    @Expose
    private int quantity;

    @SerializedName("ticketId")
    @Expose
    private String ticketId;

    @SerializedName("title")
    @Expose
    private String title;

    public int getQuantity() {
        return quantity;
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getTitle() {
        return title;
    }
}