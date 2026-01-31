package com.whosin.business.service.models.statistics;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.service.models.rayna.RaynaTicketDetailModel;

public class TransactionListModel implements DiffIdentifier {

    @SerializedName("ticketId")
    @Expose
    private String ticketId;

    @SerializedName("quantity")
    @Expose
    private int quantity;

    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    @SerializedName("totalSale")
    @Expose
    private int totalSale;

    @SerializedName("totalCost")
    @Expose
    private int totalCost;

    @SerializedName("totalProfit")
    @Expose
    private int totalProfit;

    @SerializedName("ticketInfo")
    @Expose
    private RaynaTicketDetailModel ticketInfo;

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getTotalSale() {
        return totalSale;
    }

    public void setTotalSale(int totalSale) {
        this.totalSale = totalSale;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(int totalCost) {
        this.totalCost = totalCost;
    }

    public int getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(int totalProfit) {
        this.totalProfit = totalProfit;
    }

    public RaynaTicketDetailModel getTicketInfo() {
        return ticketInfo;
    }

    public void setTicketInfo(RaynaTicketDetailModel ticketInfo) {
        this.ticketInfo = ticketInfo;
    }

    @Override
    public int getIdentifier() {
        return ticketId != null ? ticketId.hashCode() : 0;
    }
}
