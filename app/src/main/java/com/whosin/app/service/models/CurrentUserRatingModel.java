package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

public class CurrentUserRatingModel implements DiffIdentifier,ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String id = "";
    @SerializedName("venueId")
    @Expose
    private String venueId = "";
    @SerializedName("eventOrgId")
    @Expose
    private String eventOrgId ="";
    @SerializedName("activityId")
    @Expose
    private String activityId ="";
    @SerializedName("userId")
    @Expose
    private String userId = "";
    @SerializedName("stars")
    @Expose
    private float stars = 0;
    @SerializedName("review")
    @Expose
    private String review = "";
    @SerializedName("createdAt")
    @Expose
    private String createdAt = "";

    @SerializedName("user")
    @Expose
    private UserDetailModel user;

    @SerializedName("reply")
    @Expose
    private String reply = "";

    @SerializedName("image")
    @Expose
    private String image = "";
    @SerializedName("title")
    @Expose
    private String title = "";
    @SerializedName("type")
    @Expose
    private String type = "";
    @SerializedName("itemId")
    @Expose
    private String itemId = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public String getEventOrgId() {
        return eventOrgId;
    }

    public void setEventOrgId(String eventOrgId) {
        this.eventOrgId = eventOrgId;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public float getStars() {
        return stars;
    }

    public void setStars(float stars) {
        this.stars = stars;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public UserDetailModel getUser() {
        return user;
    }
    public void setUser(UserDetailModel user) {
        this.user = user;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}
