package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

public class InviationsModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String _id;

    @SerializedName("userId")
    @Expose
    private String userId;

    @SerializedName("extraGuest")
    @Expose
    private int extraGuest;

    @SerializedName("invitedBy")
    @Expose
    private String invitedBy;

    @SerializedName("inviteType")
    @Expose
    private String inviteType;

    @SerializedName("inviteStatus")
    @Expose
    private String inviteStatus;

    @SerializedName("createdAt")
    @Expose
    private String createdAt;


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getExtraGuest() {
        return extraGuest;
    }

    public void setExtraGuest(int extraGuest) {
        this.extraGuest = extraGuest;
    }

    public String getInvitedBy() {
        return invitedBy;
    }

    public void setInvitedBy(String invitedBy) {
        this.invitedBy = invitedBy;
    }

    public String getInviteType() {
        return inviteType;
    }

    public void setInviteType(String inviteType) {
        this.inviteType = inviteType;
    }

    public String getInviteStatus() {
        return inviteStatus;
    }

    public void setInviteStatus(String inviteStatus) {
        this.inviteStatus = inviteStatus;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return false;
    }
}
