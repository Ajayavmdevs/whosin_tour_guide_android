package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;

public class UpdateStatusModel implements DiffIdentifier,ModelProtocol {
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("wallet")
    @Expose
    private boolean wallet;
    @SerializedName("outing")
    @Expose
    private boolean outing;
    @SerializedName("bucket")
    @Expose
    private boolean bucket;
    @SerializedName("event")
    @Expose
    private boolean event;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isWallet() {
        return wallet;
    }

    public void setWallet(boolean wallet) {
        this.wallet = wallet;
    }

    public boolean isOuting() {
        return outing;
    }

    public void setOuting(boolean outing) {
        this.outing = outing;
    }

    public boolean isBucket() {
        return bucket;
    }

    public void setBucket(boolean bucket) {
        this.bucket = bucket;
    }

    public boolean isEvent() {
        return event;
    }

    public void setEvent(boolean event) {
        this.event = event;
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
