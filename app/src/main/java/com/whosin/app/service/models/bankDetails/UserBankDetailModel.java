package com.whosin.app.service.models.bankDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.service.models.ModelProtocol;

public class UserBankDetailModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("bankDetails")
    @Expose
    private BankDetailsModel bankDetails;

    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("userId")
    @Expose
    private String userId;

    @SerializedName("holderName")
    @Expose
    private String holderName;

    @SerializedName("holderType")
    @Expose
    private String holderType;

    @SerializedName("country")
    @Expose
    private String country;

    @SerializedName("currency")
    @Expose
    private String currency;

    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;

    @SerializedName("__v")
    @Expose
    private int version;

    public BankDetailsModel getBankDetails() {
        return bankDetails;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getHolderName() {
        return holderName;
    }

    public String getHolderType() {
        return holderType;
    }

    public String getCountry() {
        return country;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public int getIdentifier() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean isValidModel() {
        return bankDetails != null && holderName != null;
    }
}

