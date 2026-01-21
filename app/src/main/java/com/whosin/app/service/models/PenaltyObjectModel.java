package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

public class PenaltyObjectModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("eventId")
    @Expose
    private String eventId;
    @SerializedName("promoterId")
    @Expose
    private String promoterId;
    @SerializedName("inviteId")
    @Expose
    private String inviteId;
    @SerializedName("venueId")
    @Expose
    private Object venueId;
    @SerializedName("dateTime")
    @Expose
    private String dateTime;
    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("targetedVenue")
    @Expose
    private String targetedVenue;
    @SerializedName("frequencyIncrease")
    @Expose
    private Integer frequencyIncrease;
    @SerializedName("banDuration")
    @Expose
    private Integer banDuration;
    @SerializedName("cost")
    @Expose
    private Integer cost;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getPromoterId() {
        return promoterId;
    }

    public void setPromoterId(String promoterId) {
        this.promoterId = promoterId;
    }

    public String getInviteId() {
        return inviteId;
    }

    public void setInviteId(String inviteId) {
        this.inviteId = inviteId;
    }

    public Object getVenueId() {
        return venueId;
    }

    public void setVenueId(Object venueId) {
        this.venueId = venueId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTargetedVenue() {
        return targetedVenue;
    }

    public void setTargetedVenue(String targetedVenue) {
        this.targetedVenue = targetedVenue;
    }

    public Integer getFrequencyIncrease() {
        return frequencyIncrease;
    }

    public void setFrequencyIncrease(Integer frequencyIncrease) {
        this.frequencyIncrease = frequencyIncrease;
    }

    public Integer getBanDuration() {
        return banDuration;
    }

    public void setBanDuration(Integer banDuration) {
        this.banDuration = banDuration;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
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
