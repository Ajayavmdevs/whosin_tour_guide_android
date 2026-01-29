package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;

import java.util.List;

public class ClaimSpecialOfferModel implements DiffIdentifier, ModelProtocol {
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("venueId")
    @Expose
    private String venueId;
    @SerializedName("specialOfferId")
    @Expose
    private String specialOfferId;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("claimCode")
    @Expose
    private String claimCode;
    @SerializedName("claimId")
    @Expose
    private String claimId;
    @SerializedName("amount")
    @Expose
    private Integer amount;
    @SerializedName("billAmount")
    @Expose
    private Integer billAmount;
    @SerializedName("totalPerson")
    @Expose
    private Integer totalPerson;
    @SerializedName("branches")
    @Expose
    private List<BrunchModel> brunch ;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("__v")
    @Expose
    private Integer v;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public String getSpecialOfferId() {
        return specialOfferId;
    }

    public void setSpecialOfferId(String specialOfferId) {
        this.specialOfferId = specialOfferId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(Integer billAmount) {
        this.billAmount = billAmount;
    }

    public String getClaimCode() {
        return claimCode;
    }

    public void setClaimCode(String claimCode) {
        this.claimCode = claimCode;
    }

    public String getClaimId() {
        return claimId;
    }

    public void setClaimId(String claimId) {
        this.claimId = claimId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getTotalPerson() {
        return totalPerson;
    }

    public void setTotalPerson(Integer totalPerson) {
        this.totalPerson = totalPerson;
    }

    public List<BrunchModel> getBrunch() {
        return brunch;
    }

    public void setBrunch(List<BrunchModel> brunch) {
        this.brunch = brunch;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
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
