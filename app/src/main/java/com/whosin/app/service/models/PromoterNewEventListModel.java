package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

import java.util.List;

public class PromoterNewEventListModel implements DiffIdentifier, ModelProtocol{

    @SerializedName("events")
    @Expose
    private List<PromoterNewEventDetailModel> events ;

    @SerializedName("users")
    @Expose
    private List<InvitedUserModel> users ;

    @SerializedName("venues")
    @Expose
    private List<VenueObjectModel> venues ;

    @SerializedName("circles")
    @Expose
    private List<InvitedUserModel> circles ;

    public List<PromoterNewEventDetailModel> getEvents() {
        return events;
    }

    public void setEvents(List<PromoterNewEventDetailModel> events) {
        this.events = events;
    }

    public List<InvitedUserModel> getUsers() {
        return users;
    }

    public void setUsers(List<InvitedUserModel> users) {
        this.users = users;
    }

    public List<VenueObjectModel> getVenues() {
        return venues;
    }

    public void setVenues(List<VenueObjectModel> venues) {
        this.venues = venues;
    }

    public List<InvitedUserModel> getCircles() {
        return circles;
    }

    public void setCircles(List<InvitedUserModel> circles) {
        this.circles = circles;
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
