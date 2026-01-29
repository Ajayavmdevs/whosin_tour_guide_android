package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;

public class SubscriptionVoucherListModel implements ModelProtocol, DiffIdentifier {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("orderId")
    @Expose
    private String orderId;

    @SerializedName("venueId")
    @Expose
    private String venueId;
    @SerializedName("voucherId")
    @Expose
    private String voucherId;
    @SerializedName("price")
    @Expose
    private int price;
    @SerializedName("qty")
    @Expose
    private int qty;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("venue")
    @Expose
    private VenueObjectModel venue;
    @SerializedName("vouchar")
    @Expose
    private VoucherModel vouchar;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getVenueId() {return venueId;}

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public String getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(String voucherId) {
        this.voucherId = voucherId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public VenueObjectModel getVenue() {
        return venue;
    }

    public void setVenue(VenueObjectModel venue) {
        this.venue = venue;
    }

    public VoucherModel getVouchar() {
        return vouchar;
    }

    public void setVouchar(VoucherModel vouchar) {
        this.vouchar = vouchar;
    }

    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }
}
