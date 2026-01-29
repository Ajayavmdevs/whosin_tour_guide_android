package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.AppConstants;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.service.models.rayna.RaynaTicketDetailModel;

import java.util.ArrayList;
import java.util.List;

public class CommanSearchModel implements DiffIdentifier, ModelProtocol {


    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("venues")
    @Expose
    private List<VenueObjectModel> venues;

    @SerializedName("offers")
    @Expose
    private List<OffersModel> offers;

    @SerializedName("users")
    @Expose
    private List<ContactListModel> users;

    @SerializedName("events")
    @Expose
    private List<SearchEventModel> events;

    @SerializedName("activity")
    @Expose
    private List<ActivityDetailModel> activity;

    @SerializedName("ticket")
    @Expose
    private List<RaynaTicketDetailModel> ticket;


    private String selectedTab = "";


    public AppConstants.SearchResultType getBlockType() {
        switch (getType()) {
            case "venue":
                return AppConstants.SearchResultType.VENUE;
            case "offer":
                return AppConstants.SearchResultType.OFFER;
            case "user":
                return AppConstants.SearchResultType.USER;
            case "event":
                return AppConstants.SearchResultType.EVENT;
            case "activity":
                return AppConstants.SearchResultType.ACTIVITY;
            case "ticket":
                return AppConstants.SearchResultType.TICKET;
            default:
                return AppConstants.SearchResultType.NONE;
        }
    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }

    public String getSelectedTab() {
        return selectedTab;
    }

    public void setSelectedTab(String selectedTab) {
        this.selectedTab = selectedTab;
    }

    public List<VenueObjectModel> getVenues() {
        if (venues == null) {
            return new ArrayList<>();
        }
        return venues;
    }

    public void setVenues(List<VenueObjectModel> venues) {
        this.venues = venues;
    }

    public List<OffersModel> getOffers() {
        if (offers == null) {
            return new ArrayList<>();
        }
        return offers;
    }

    public void setOffers(List<OffersModel> offers) {
        this.offers = offers;
    }

    public List<ContactListModel> getUsers() {
        if (users == null) {
            return new ArrayList<>();
        }
        return users;
    }

    public void setUsers(List<ContactListModel> users) {
        this.users = users;
    }

    public List<SearchEventModel> getEvents() {
        if (events == null) {
            return new ArrayList<>();
        }
        return events;
    }

    public void setEvents(List<SearchEventModel> events) {
        this.events = events;
    }

    public List<ActivityDetailModel> getActivity() {
        if (activity == null) {
            return new ArrayList<>();
        }
        return activity;
    }

    public List<RaynaTicketDetailModel> getTicket() {
        if (ticket == null) {
            return new ArrayList<>();
        }
        return ticket;
    }

    public void setTicket(List<RaynaTicketDetailModel> ticket) {
        this.ticket = ticket;
    }

    public void setActivity(List<ActivityDetailModel> activity) {
        this.activity = activity;
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
