package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;

import java.util.List;

public class WalletEventModel implements DiffIdentifier,ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String id="";
    @SerializedName("title")
    @Expose
    private String title="";
    @SerializedName("description")
    @Expose
    private String description="";
    @SerializedName("user_type")
    @Expose
    private String userType="";
    @SerializedName("org_id")
    @Expose
    private String orgId="";
    @SerializedName("user_id")
    @Expose
    private Object userId;
    @SerializedName("admins")
    @Expose
    private List<String> admins;
    @SerializedName("type")
    @Expose
    private String type="";
    @SerializedName("image")
    @Expose
    private String image="";
    @SerializedName("venue_type")
    @Expose
    private String venueType="";
    @SerializedName("venue")
    @Expose
    private VenueObjectModel venue;
    @SerializedName("custom_venue")
    @Expose
    private Object customVenue;
    @SerializedName("reservation_time")
    @Expose
    private String reservationTime="";
    @SerializedName("event_time")
    @Expose
    private String eventTime="";
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("createdAt")
    @Expose
    private String createdAt="";
    @SerializedName("packages")
    @Expose
    private List<PackageModel> packages;
    @SerializedName("events_organizer")
    @Expose
    private EventOrgDateModel eventsOrganizer;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return Utils.notNullString(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return Utils.notNullString(description);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public Object getUserId() {
        return userId;
    }

    public void setUserId(Object userId) {
        this.userId = userId;
    }

    public List<String> getAdmins() {
        return admins;
    }

    public void setAdmins(List<String> admins) {
        this.admins = admins;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return Utils.notNullString(image);
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVenueType() {
        return venueType;
    }

    public void setVenueType(String venueType) {
        this.venueType = venueType;
    }

    public VenueObjectModel getVenue() {
        return venue;
    }

    public void setVenue(VenueObjectModel venue) {
        this.venue = venue;
    }

    public Object getCustomVenue() {
        return customVenue;
    }

    public void setCustomVenue(Object customVenue) {
        this.customVenue = customVenue;
    }

    public String getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(String reservationTime) {
        this.reservationTime = reservationTime;
    }

    public String getEventTime() {
        return Utils.notNullString(eventTime);
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<PackageModel> getPackages() {
        return packages;
    }

    public void setPackages(List<PackageModel> packages) {
        this.packages = packages;
    }

    public EventOrgDateModel getEventsOrganizer() {
        return eventsOrganizer;
    }

    public void setEventsOrganizer(EventOrgDateModel eventsOrganizer) {
        this.eventsOrganizer = eventsOrganizer;
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
