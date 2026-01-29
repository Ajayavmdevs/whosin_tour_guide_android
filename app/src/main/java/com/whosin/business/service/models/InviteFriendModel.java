package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.Repository.ChatRepository;
import com.whosin.business.service.manager.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class InviteFriendModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("title")
    @Expose
    private String title = "";
    @SerializedName("userId")
    @Expose
    private String userId = "";
    @SerializedName("venueId")
    @Expose
    private String venueId = "";
    @SerializedName("date")
    @Expose
    private String date = "";
    @SerializedName("dateTime")
    @Expose
    private String dateTime="";
    @SerializedName("startTime")
    @Expose
    private String startTime="";
    @SerializedName("endTime")
    @Expose
    private String endTime="";
    @SerializedName("extraGuest")
    @Expose
    private int extraGuest = 0;
    @SerializedName("status")
    @Expose
    private String status = "";
    @SerializedName("invitedBy")
    @Expose
    private String invitedBy = "";
    @SerializedName("eventId")
    @Expose
    private String eventId = "";
    @SerializedName("inviteType")
    @Expose
    private String inviteType = "";
    @SerializedName("inviteStatus")
    @Expose
    private String inviteStatus = "";
    @SerializedName("_id")
    @Expose
    private String id = "";
    @SerializedName("createdAt")
    @Expose
    private String createdAt = "";
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt = "";
    @SerializedName("__v")
    @Expose
    private Integer v;
    @SerializedName("invitedUser")
    @Expose
    private List<ContactListModel> invitedUser;
    @SerializedName("user")
    @Expose
    private UserDetailModel user;
    @SerializedName("venue")
    @Expose
    private VenueObjectModel venue;

    @SerializedName("offer")
    @Expose
    private OffersModel offersModel;

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @SerializedName("isDeleted")
    @Expose
    private boolean isDeleted;

    public ChatMessageModel getLastMsg() {
        ChatMessageModel model =  ChatRepository.shared(Graphics.context).getLastMessages(id);
        if (model == null) {
            return new ChatMessageModel();
        }
        return model;
    }

    public String getDateTime() {
        return Utils.notNullString(dateTime);
    }

    public String getInvitedBy() {
        return invitedBy;
    }

    public void setInvitedBy(String invitedBy) {
        this.invitedBy = invitedBy;
    }

    public String getEventId() {
        return Utils.notNullString(eventId);
     }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getInviteType() {
        return Utils.notNullString(inviteType);
    }

    public void setInviteType(String inviteType) {
        this.inviteType = inviteType;
    }

    public String getInviteStatus() {
        return Utils.notNullString(inviteStatus);
     }

    public void setInviteStatus(String inviteStatus) {
        this.inviteStatus = inviteStatus;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public UserDetailModel getUser() {
        return user;
    }

    public void setUser(UserDetailModel user) {
        this.user = user;
    }

    public List<ContactListModel> getInvitedUser() {
        return invitedUser == null ? new ArrayList<>() : invitedUser;
     }

    public void setInvitedUser(List<ContactListModel> invitedUser) {
        this.invitedUser = invitedUser;
    }

    public VenueObjectModel getVenue() {
        return venue;
    }

    public void setVenue(VenueObjectModel venue) {
        this.venue = venue;
    }

    public String getTitle() {
        return Utils.notNullString(title);
     }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserId() {
        return Utils.notNullString(userId);
     }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVenueId() {
        return Utils.notNullString(venueId);
     }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public String getDate() {
        return Utils.notNullString(date);
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return Utils.notNullString(startTime);
     }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return Utils.notNullString(endTime);
     }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getExtraGuest() {
        return extraGuest;
    }

    public void setExtraGuest(int extraGuest) {
        this.extraGuest = extraGuest;
    }

    public String getStatus() {
        return Utils.notNullString(status);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

    public OffersModel getOffersModel() {
        return offersModel;
    }

    public void setOffersModel(OffersModel offersModel) {
        this.offersModel = offersModel;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }

    public boolean isOwnerOfOuting() {
        return SessionManager.shared.getUser().getId().equals(getUserId());
    }

    public boolean isAllowEdit() {
        if (SessionManager.shared.getUser().getId().equals(getUserId())){
            return !getStatus().equals("cancelled") && !getStatus().equals("completed");
        }
        return  false;
    }
}
