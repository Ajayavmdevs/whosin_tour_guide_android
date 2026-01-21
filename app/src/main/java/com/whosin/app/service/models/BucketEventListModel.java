package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.Repository.ChatRepository;

import java.util.ArrayList;
import java.util.List;

public class BucketEventListModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String id = "";
    @SerializedName("title")
    @Expose
    private String title = "";
    @SerializedName("description")
    @Expose
    private String description = "";
    @SerializedName("user_type")
    @Expose
    private String userType="";
    @SerializedName("org_id")
    @Expose
    private String orgId="";
    @SerializedName("user_id")
    @Expose
    private Object userId="";
    @SerializedName("admins")
    @Expose
    private List<ContactListModel> admins;
    @SerializedName("type")
    @Expose
    private String type = "";
    @SerializedName("image")
    @Expose
    private String image = "";
    @SerializedName("venue_type")
    @Expose
    private String venueType = "";
    @SerializedName("venue")
    @Expose
    private VenueObjectModel venue;
    @SerializedName("custom_venue")
    @Expose
    private Object customVenue;
    @SerializedName("reservation_time")
    @Expose
    private String reservationTime = "";
    @SerializedName("event_time")
    @Expose
    private String eventTime ="";
    @SerializedName("event_status")
    @Expose
    private String event_status ="";
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("createdAt")
    @Expose
    private String createdAt = "";
    @SerializedName("myInvitationStatus")
    @Expose
    private String myInvitationStatus = "";
    @SerializedName("invitedUsers")
    @Expose
    private List<InviteFriendModel> invitedUsers;
    @SerializedName("packages")
    @Expose
    private List<PackageModel> packages;
    @SerializedName("org")
    @Expose
    private EventOrgDateModel org;

    @SerializedName("user")
    @Expose
    private ContactListModel user;

    private boolean isComplementry = false;
    private boolean isPromoter = false;

    public ChatMessageModel getLastMsg() {

        ChatMessageModel model =  ChatRepository.shared(Graphics.context).getLastMessages(id);
        if (model == null) {
            return new ChatMessageModel();
        }
        return model;
    }

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

    public String getMyInvitationStatus() {
        return myInvitationStatus;
    }

    public void setMyInvitationStatus(String myInvitationStatus) {
        this.myInvitationStatus = myInvitationStatus;
    }

    public String getEvent_status() {
        return event_status;
    }

    public void setEvent_status(String event_status) {
        this.event_status = event_status;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserType() {
        return Utils.notNullString(userType);
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

    public List<ContactListModel> getAdmins() {
        return admins;
    }

    public void setAdmins(List<ContactListModel> admins) {
        this.admins = admins;
    }

    public String getType() {
        return Utils.notNullString(type);

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
        return eventTime;
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
        return Utils.notNullString(createdAt);
     }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<InviteFriendModel> getInvitedUsers() {
        return invitedUsers == null ? new ArrayList<>() : invitedUsers;
     }

    public void setInvitedUsers(List<InviteFriendModel> invitedUsers) {
        this.invitedUsers = invitedUsers;
    }

    public List<PackageModel> getPackages() {
        return packages == null ? new ArrayList<>() : packages;
     }

    public void setPackages(List<PackageModel> packages) {
        this.packages = packages;
    }

    public EventOrgDateModel getOrg() {
        return org;
    }

    public void setOrg(EventOrgDateModel org) {
        this.org = org;
    }

    public ContactListModel getUser() {
        return user;
    }

    public void setUser(ContactListModel user) {
        this.user = user;
    }

    public boolean isComplementry() {
        return isComplementry;
    }

    public void setComplementry(boolean complementry) {
        isComplementry = complementry;
    }

    public boolean isPromoter() {
        return isPromoter;
    }

    public void setPromoter(boolean promoter) {
        isPromoter = promoter;
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
