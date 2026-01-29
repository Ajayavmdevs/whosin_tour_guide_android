package com.whosin.business.service.models;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;

import java.util.ArrayList;
import java.util.List;

public class EventModel implements DiffIdentifier, ModelProtocol {

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
    @SerializedName("type")
    @Expose
    private String type="";
    @SerializedName("image")
    @Expose
    private String image="";
    @SerializedName("venue")
    @Expose
    private JsonElement venueElement;
    @SerializedName("venueType")
    @Expose
    private String venueType="";
    @SerializedName("reservation_time")
    @Expose
    private String reservationTime="";
    @SerializedName("event_time")
    @Expose
    private String eventTime="";
    @SerializedName("custom_venue")
    @Expose
    private Object customVenue;
    @SerializedName("event_status")
    @Expose
    private String event_status ="";
    @SerializedName("orgData")
    @Expose
    private EventOrgDateModel orgData;
    @SerializedName("admins")
    @Expose
    private List<String> admins;
    @SerializedName("invitedGuests")
    @Expose
    private List<EventInviteGuestModel> invitedGuests;

    @SerializedName("inGuests")
    @Expose
    private List<EventInviteGuestModel> inGuests;
    @SerializedName("invitedGuestsCount")
    @Expose
    private int invitedGuestsCount;

    @SerializedName("extraGuestCount")
    @Expose
    private int extraGuestCount = 0;

    @SerializedName("inGuestsCount")
    @Expose
    private int inGuestsCount = 0;

    @SerializedName("currentUserReview")
    @Expose
    private CurrentUserRatingModel currentUserRating;

    @SerializedName("packages")
    @Expose
    private List<PackageModel> packages;

  /*  public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }*/


    @SerializedName("myInvitationStatus")
    @Expose
    private String myInviteStatus;

    public String getEvent_status() {
        return event_status;
    }

    public void setEvent_status(String event_status) {
        this.event_status = event_status;
    }

    public String getCustomVenueId() {
        if (venueElement != null && venueElement.isJsonPrimitive()) {
            return venueElement.getAsString();
        }
        return null;
    }

    public VenueObjectModel getCustomVenueObject() {
        if (venueElement != null && venueElement.isJsonObject()) {
            // Use Gson to deserialize the object
            return new Gson().fromJson( venueElement, VenueObjectModel.class );
        }
        return null;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

/*    public VenueObjectModel getVenue() {
        return venue;
    }

    public void setVenue(VenueObjectModel venue) {
        this.venue = venue;
    }*/


    public String getVenueType() {
        return venueType;
    }

    public void setVenueType(String venueType) {
        this.venueType = venueType;
    }


    public String getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(String reservationTime) {
        this.reservationTime = reservationTime;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public Object getCustomVenue() {
        return customVenue;
    }

    public void setCustomVenue(Object customVenue) {
        this.customVenue = customVenue;
    }

    public EventOrgDateModel getOrgData() {
        return orgData;
    }

    public void setOrgData(EventOrgDateModel orgData) {
        this.orgData = orgData;
    }

    public List<String> getAdmins() {
        return admins != null ? admins : new ArrayList<>();
    }

    public void setAdmins(List<String> admins) {
        this.admins = admins;
    }

    public List<EventInviteGuestModel> getInvitedGuests() {
        return invitedGuests;
    }

    public void setInvitedGuests(List<EventInviteGuestModel> invitedGuests) {
        this.invitedGuests = invitedGuests;

    }

    public List<EventInviteGuestModel> getInGuests() {
        return inGuests;
    }

    public void setInGuests(List<EventInviteGuestModel> inGuests) {
        this.inGuests = inGuests;
    }

    public int getInvitedGuestsCount() {
        return invitedGuestsCount;
    }

    public void setInvitedGuestsCount(int invitedGuestsCount) {
        this.invitedGuestsCount = invitedGuestsCount;
    }

    public CurrentUserRatingModel getCurrentUserRating() {
        return currentUserRating == null ? new CurrentUserRatingModel() : currentUserRating;
    }

    public void setCurrentUserRating(CurrentUserRatingModel currentUserRating) {
        this.currentUserRating = currentUserRating;
    }

    public List<PackageModel> getPackages() {
        return packages != null ? packages : new ArrayList<>();
    }

    public void setPackages(List<PackageModel> packages) {
        this.packages = packages;
    }


    public String getMyInviteStatus() {
        return myInviteStatus;
    }

    public void setMyInviteStatus(String myInviteStatus) {
        this.myInviteStatus = myInviteStatus;
    }

    public int getExtraGuestCount() {
        return extraGuestCount;
    }

    public void setExtraGuestCount(int extraGuestCount) {
        this.extraGuestCount = extraGuestCount;
    }

    public int getInGuestsCount() {
        return inGuestsCount;
    }

    public void setInGuestsCount(int inGuestsCount) {
        this.inGuestsCount = inGuestsCount;
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
