package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;

import java.util.Date;

public class StoryObjectModel implements DiffIdentifier,ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("mediaType")
    @Expose
    private String mediaType;
    @SerializedName("contentType")
    @Expose
    private String contentType;
    @SerializedName("mediaUrl")
    @Expose
    private String mediaUrl;
    @SerializedName("thumbnail")
    @Expose
    private Object thumbnail;
    @SerializedName("buttonText")
    @Expose
    private String buttonText;
    @SerializedName("status")
    @Expose
    private boolean status;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    @SerializedName("expiryDate")
    @Expose
    private String expiryDate;


    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("offerId")
    @Expose
    private String offerId;
    @SerializedName("ticketId")
    @Expose
    private String ticketId;

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public Object getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Object thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    @Override
    public int getIdentifier() {
        return id.hashCode();
    }

    @Override
    public boolean isValidModel() {
        return true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getDuration() { return Long.parseLong(duration); }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public Date getExpiryDate() {
        try {
            return Utils.stringToDate(expiryDate, AppConstants.DATEFORMAT_LONG_TIME);
        } catch (Exception e) {
            return new Date();
        }

    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }
}
