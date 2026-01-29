package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.AppConstants;
import com.whosin.business.comman.DiffIdentifier;

import java.util.List;

public class ClaimHistoryModel implements DiffIdentifier, ModelProtocol{

    @SerializedName("_id")
    @Expose
    private String id="";
    @SerializedName("type")
    @Expose
    private String type="";
    @SerializedName("claimCode")
    @Expose
    private String claimCode="";
    @SerializedName("amount")
    @Expose
    private String amount="";
    @SerializedName("billAmount")
    @Expose
    private String billAmount="";
    @SerializedName("totalPerson")
    @Expose
    private String totalPerson="";
    @SerializedName("brunch")
    @Expose
    private List<BrunchModel> brunch;
    @SerializedName("createdAt")
    @Expose
    private String createdAt="";
    @SerializedName("claimId")
    @Expose
    private String claimId="";
    @SerializedName("venue")
    @Expose
    private VenueObjectModel venue;
    @SerializedName("specialOffer")
    @Expose
    private SpecialOfferModel specialOffer;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClaimCode() {
        return claimCode;
    }

    public void setClaimCode(String claimCode) {
        this.claimCode = claimCode;
    }

    public String getTotalPerson() {
        return totalPerson;
    }

    public String getAmount() {
        return amount;
    }

    public String getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(String billAmount) {
        this.billAmount = billAmount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setTotalPerson(String totalPerson) {
        this.totalPerson = totalPerson;
    }

    public List<BrunchModel> getBrunch() {
        return brunch;
    }

    public void setBrunch(List<BrunchModel> brunch) {
        this.brunch = brunch;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getClaimId() {
        return claimId;
    }

    public void setClaimId(String claimId) {
        this.claimId = claimId;
    }

    public VenueObjectModel getVenue() {
        return venue;
    }

    public void setVenue(VenueObjectModel venue) {
        this.venue = venue;
    }

    public SpecialOfferModel getSpecialOffer() {
        return specialOffer;
    }

    public void setSpecialOffer(SpecialOfferModel specialOffer) {
        this.specialOffer = specialOffer;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }

    public AppConstants.CLAIMTYPE getClaimType() {
        switch (getType()) {
            case "total": return AppConstants.CLAIMTYPE.CLAIM_TOTAL;
            case "brunch": return AppConstants.CLAIMTYPE.CLAIM_BRUNCH;
            default:
                return AppConstants.CLAIMTYPE.NONE;
        }
    }
}
