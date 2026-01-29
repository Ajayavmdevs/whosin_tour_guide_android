package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;

import java.util.ArrayList;
import java.util.List;

public class ItemModel implements DiffIdentifier, ModelProtocol{

    @SerializedName("_id")
    @Expose
    private String id = "";
    @SerializedName("giftMessage")
    @Expose
    private List<String> giftMessage;
    @SerializedName("packageId")
    @Expose
    private String packageId="";
    @SerializedName("uniqueCode")
    @Expose
    private Object uniqueCode;
    @SerializedName("date")
    @Expose
    private String date="";
    @SerializedName("time")
    @Expose
    private String time="";
    @SerializedName("qty")
    @Expose
    private Integer qty;
    @SerializedName("remainingQty")
    @Expose
    private int remainingQty = 0;
    @SerializedName("price")
    @Expose
    private double price = 0.0;
    @SerializedName("createdAt")
    @Expose
    private String createdAt ="";
    @SerializedName("usedQty")
    @Expose
    private int usedQty = 0;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public Object getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(Object uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getRemainingQty() {
        return remainingQty;
    }

    public void setRemainingQty(int remainingQty) {
        this.remainingQty = remainingQty;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getUsedQty() {
        return usedQty;
    }

    public void setUsedQty(int usedQty) {
        this.usedQty = usedQty;
    }

    public List<String> getGiftMessage() {
        if (giftMessage == null) {
            return new ArrayList<>();
        }
        return giftMessage;
    }

    public void setGiftMessage(List<String> giftMessage) {
        this.giftMessage = giftMessage;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }
}
