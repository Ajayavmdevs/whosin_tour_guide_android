package com.whosin.app.service.models;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

import java.util.ArrayList;
import java.util.List;

public class SpecialOfferModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String id = "";
    @SerializedName("venueId")
    @Expose
    private String venueId= "";
    @SerializedName("title")
    @Expose
    private String title="";
    @SerializedName("description")
    @Expose
    private String description="";
    @SerializedName("type")
    @Expose
    private String type="";
    @SerializedName("discount")
    @Expose
    private int discount = 0;
    @SerializedName("maxPersonAllowed")
    @Expose
    private String maxPersonAllowed = "";
    @SerializedName("maxBrunchAllowed")
    @Expose
    private String maxBrunchAllowed = "";
    @SerializedName("pricePerPerson")
    @Expose
    private String pricePerPerson = "";
    @SerializedName("claimCode")
    @Expose
    private String claimCode="";
    @SerializedName("branches")
    @Expose
    private List<BrunchModel> brunch ;
    @SerializedName("offers")
    @Expose
    private BrunchListModel offers;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
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

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getClaimCode() {
        return claimCode;
    }

    public void setClaimCode(String claimCode) {
        this.claimCode = claimCode;
    }

    public List<BrunchModel> getBrunch() {
        return brunch;
    }

    public void setBrunch(List<BrunchModel> brunch) {
        this.brunch = brunch;
    }

    public String getMaxPersonAllowed() {
        return maxPersonAllowed;
    }

    public void setMaxPersonAllowed(String maxPersonAllowed) {
        this.maxPersonAllowed = maxPersonAllowed;
    }

    public BrunchListModel getOffers() {
        return offers;
    }

    public void setOffers(BrunchListModel offers) {
        this.offers = offers;
    }

    public String getMaxBrunchAllowed() {
        return maxBrunchAllowed;
    }

    public void setMaxBrunchAllowed(String maxBrunchAllowed) {
        this.maxBrunchAllowed = maxBrunchAllowed;
    }

    public String getPricePerPerson() {
        return TextUtils.isEmpty(pricePerPerson) ? "0" : pricePerPerson;
    }

    public void setPricePerPerson(String pricePerPerson) {
        this.pricePerPerson = pricePerPerson;
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
