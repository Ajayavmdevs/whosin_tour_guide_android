package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

public class PromoterPaidPassModel implements DiffIdentifier, ModelProtocol{

    @SerializedName("_id")
    @Expose
    private String _id = "";

    @SerializedName("title")
    @Expose
    private String title = "";

    @SerializedName("description")
    @Expose
    private String description = "";

    @SerializedName("type")
    @Expose
    private String type = "";

    @SerializedName("amount")
    @Expose
    private int amount = 0;

    @SerializedName("validityInDays")
    @Expose
    private int validityInDays = 0;


    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getValidityInDays() {
        return validityInDays;
    }

    public void setValidityInDays(int validityInDays) {
        this.validityInDays = validityInDays;
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
