package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

public class PromoterListModel implements DiffIdentifier, ModelProtocol {
    @SerializedName("_id")
    @Expose
    private String id = "";
    @SerializedName("title")
    @Expose
    private String title = "";
    @SerializedName("description")
    @Expose
    private String description = "";
    @SerializedName("image")
    @Expose
    private String image = "";
    @SerializedName("type")
    @Expose
    private String type = "";
    @SerializedName("typeId")
    @Expose
    private String typeId = "";
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("eventId")
    @Expose
    private String eventId = "";
    @SerializedName("userId")
    @Expose
    private String userId = "";
    @SerializedName("readStatus")
    @Expose
    private boolean readStatus ;
    @SerializedName("inviteStatus")
    @Expose
    private String inviteStatus = "";
    @SerializedName("promoterStatus")
    @Expose
    private String promoterStatus = "";


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public boolean isReadStatus() {
        return readStatus;
    }

    public void setReadStatus(boolean readStatus) {
        this.readStatus = readStatus;
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

    public String getInviteStatus() {
        return inviteStatus;
    }

    public void setInviteStatus(String inviteStatus) {
        this.inviteStatus = inviteStatus;
    }

    public String getPromoterStatus() {
        return promoterStatus;
    }

    public void setPromoterStatus(String promoterStatus) {
        this.promoterStatus = promoterStatus;
    }
}
