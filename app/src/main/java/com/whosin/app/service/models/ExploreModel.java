package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffIdentifier;

import java.util.ArrayList;
import java.util.List;

public class ExploreModel implements DiffIdentifier,ModelProtocol {


    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("event")
    @Expose
    private SearchEventModel event;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("offer")
    @Expose
    private OffersModel offer;
    @SerializedName("activity")
    @Expose
    private ActivityDetailModel activity;

    private List<UserDetailModel> users = new ArrayList<>();
    private List<VenueObjectModel> venus = new ArrayList<>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public SearchEventModel getEvent() {
        return event;
    }

    public void setEvent(SearchEventModel event) {
        this.event = event;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public OffersModel getOffer() {
        return offer;
    }

    public void setOffer(OffersModel offer) {
        this.offer = offer;
    }

    public ActivityDetailModel getActivity() {
        return activity;
    }

    public void setActivity(ActivityDetailModel activity) {
        this.activity = activity;
    }


    public AppConstants.ExploreResultType getBlockType() {
        switch (getType()) {
            case "offer": return AppConstants.ExploreResultType.OFFER;
            case "event": return AppConstants.ExploreResultType.EVENT;
            case "activity": return AppConstants.ExploreResultType.ACTIVITY;
            case "suggested_venue": return AppConstants.ExploreResultType.SUGGESTED_VENUE;
            case "suggested_user": return AppConstants.ExploreResultType.SUGGESTED_USER;
            default:
                return AppConstants.ExploreResultType.NONE;
        }
    }


    @Override
    public int getIdentifier() {
        switch (getType()) {
            case "offer": return getOffer().getId().hashCode();
            case "event": return getEvent().getId().hashCode();
            case "activity": return getActivity().getId().hashCode();
            default:
                return 0;
        }
    }

    @Override
    public boolean isValidModel() {
        return true;
    }

    public List<UserDetailModel> getUsers() {
        return users;
    }

    public void setUsers(List<UserDetailModel> users) {
        this.users = users;
    }

    public List<VenueObjectModel> getVenus() {
        return venus;
    }

    public void setVenus(List<VenueObjectModel> venus) {
        this.venus = venus;
    }
}
