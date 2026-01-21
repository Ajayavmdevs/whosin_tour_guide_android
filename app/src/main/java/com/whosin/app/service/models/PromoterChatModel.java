package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

import java.util.List;

public class PromoterChatModel implements DiffIdentifier,ModelProtocol {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("users")
    @Expose
    private List<InvitedUserModel> users;
    @SerializedName("owner")
    @Expose
    private UserDetailModel owner;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("venueName")
    @Expose
    private String venueName;
    @SerializedName("venueImage")
    @Expose
    private String venueImage;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("startTime")
    @Expose
    private String startTime;
    @SerializedName("endTime")
    @Expose
    private String endTime;
    @SerializedName("lastMessage")
    @Expose
    private ChatMessageModel lastMessage;
    @SerializedName("totalMessages")
    @Expose
    private int totalMessages;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<InvitedUserModel> getUsers() {
        return users;
    }

    public void setUsers(List<InvitedUserModel> users) {
        this.users = users;
    }

    public UserDetailModel getOwner() {
        return owner == null ? new UserDetailModel() : owner;
    }

    public void setOwner(UserDetailModel owner) {
        this.owner = owner;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getVenueImage() {
        return venueImage;
    }

    public void setVenueImage(String venueImage) {
        this.venueImage = venueImage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

//    public ChatMessageModel getLastMessage() {
//        return lastMessage == null ? new ChatMessageModel() : lastMessage;
//    }
    public ChatMessageModel getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(ChatMessageModel lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getTotalMessages() {
        return totalMessages;
    }

    public void setTotalMessages(int totalMessages) {
        this.totalMessages = totalMessages;
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
