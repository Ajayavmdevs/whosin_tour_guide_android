package com.whosin.app.service.models.rayna;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;

public class RaynaCheckReviewModel implements DiffIdentifier, ModelProtocol {


    @SerializedName("_id")
    @Expose
    private String id = "";

    @SerializedName("userId")
    @Expose
    private String userId = "";

    @SerializedName("customTicketId")
    @Expose
    private String customTicketId = "";

    @SerializedName("createdAt")
    @Expose
    private String createdAt = "";

    @SerializedName("reviewStatus")
    @Expose
    private String reviewStatus = "";

    @SerializedName("ticketName")
    @Expose
    private String ticketName = "";

    public String getId() {
        return Utils.notNullString(id);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return Utils.notNullString(userId);
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCustomTicketId() {
        return Utils.notNullString(customTicketId);
    }

    public void setCustomTicketId(String customTicketId) {
        this.customTicketId = customTicketId;
    }

    public String getCreatedAt() {
        return Utils.notNullString(createdAt);
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getReviewStatus() {
        return Utils.notNullString(reviewStatus);
    }

    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public String getTicketName() {
        return Utils.notNullString(ticketName);
    }

    public void setTicketName(String ticketName) {
        this.ticketName = ticketName;
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
