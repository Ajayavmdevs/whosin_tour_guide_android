package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.AppConstants;
import com.whosin.business.comman.DiffIdentifier;

public class MyUserFeedModel implements DiffIdentifier,ModelProtocol {

    @SerializedName("type")
    @Expose
    private String type = "";
    @SerializedName("offer")
    @Expose
    private OffersModel offer;
    @SerializedName("venue")
    @Expose
    private VenueObjectModel venue;
    @SerializedName("createdAt")
    @Expose
    private String createdAt = "";

    @SerializedName("activity")
    @Expose
    private ActivityDetailModel activityDetailModel;

    @SerializedName("event")
    @Expose
    private SearchEventModel event;
    @SerializedName("user")
    @Expose
    private UserDetailModel user;

    public UserDetailModel getUser() {
        return user;
    }

    public void setUser(UserDetailModel user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public AppConstants.UserFeedType getBlockType() {
        switch (getType()) {
            case "friend_updates": return AppConstants.UserFeedType.FRIENDS_UPDATE;
            case "venue_updates": return AppConstants.UserFeedType.VENUE_UPDATE;
            case "event_checkin": return AppConstants.UserFeedType.EVENY_UPDATE;
            case "activity_recommendation": return AppConstants.UserFeedType.ACTIVITY_RECOMMENDATION;
            case "venue_recommendation": return AppConstants.UserFeedType.VENUE_RECOMMENDATION;
            case "offer_recommendation": return AppConstants.UserFeedType.OFFER_RECOMMENDATION;
            default:
                return AppConstants.UserFeedType.NONE;
        }
    }


    public OffersModel getOffer() {
        return offer;
    }

    public void setOffer(OffersModel offer) {
        this.offer = offer;
    }

    public VenueObjectModel getVenue() {
        return venue;
    }

    public SearchEventModel getEvent() {
        return event;
    }

    public void setEvent(SearchEventModel event) {
        this.event = event;
    }

    public void setVenue(VenueObjectModel venue) {
        this.venue = venue;
    }

    public ActivityDetailModel getActivityDetailModel() {
        return activityDetailModel;
    }

    public void setActivityDetailModel(ActivityDetailModel activityDetailModel) {
        this.activityDetailModel = activityDetailModel;
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
        return true;
    }
}
