package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;

import java.util.ArrayList;
import java.util.List;

public class YachtPackageModel implements DiffIdentifier,ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String id = "";
    @SerializedName("offerId")
    @Expose
    private String offerId = "";
    @SerializedName("title")
    @Expose
    private String title = "";
    @SerializedName("description")
    @Expose
    private String description = "";
    @SerializedName("amount")
    @Expose
    private int amount = 0;
    @SerializedName("discount")
    @Expose
    private int discount =0;
    @SerializedName("slots")
    @Expose
    private List<Object> slots;
    @SerializedName("minimumHour")
    @Expose
    private int minimumHour = 0;
    @SerializedName("maximumHour")
    @Expose
    private int maximumHour = 0;
    @SerializedName("pricePerHour")
    @Expose
    private int pricePerHour = 0;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getTitle() {
        return Utils.notNullString(title  );
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return Utils.notNullString( description );
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public List<Object> getSlots() {
        return slots == null ? new ArrayList<>() : slots;
    }

    public void setSlots(List<Object> slots) {
        this.slots = slots;
    }

    public int getMinimumHour() {
        return minimumHour;
    }

    public void setMinimumHour(int minimumHour) {
        this.minimumHour = minimumHour;
    }

    public int getMaximumHour() {
        return maximumHour;
    }

    public void setMaximumHour(int maximumHour) {
        this.maximumHour = maximumHour;
    }

    public int getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(int pricePerHour) {
        this.pricePerHour = pricePerHour;
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
