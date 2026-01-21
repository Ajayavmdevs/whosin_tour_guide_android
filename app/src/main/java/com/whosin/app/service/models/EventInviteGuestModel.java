package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

public class EventInviteGuestModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("extraGuest")
    @Expose
    private int extraGuest;
    @SerializedName("inviteStatus")
    @Expose
    private String inviteStatus;
    @SerializedName("invitedBy")
    @Expose
    private InvitedByModel invitedBy;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("userId")
    @Expose
    private String userId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getExtraGuest() {
        return extraGuest;
    }

    public void setExtraGuest(int extraGuest) {
        this.extraGuest = extraGuest;
    }

    public String getInviteStatus() {
        return inviteStatus;
    }

    public void setInviteStatus(String inviteStatus) {
        this.inviteStatus = inviteStatus;
    }

    public InvitedByModel getInvitedBy() {
        return invitedBy;
    }

    public void setInvitedBy(InvitedByModel invitedBy) {
        this.invitedBy = invitedBy;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
