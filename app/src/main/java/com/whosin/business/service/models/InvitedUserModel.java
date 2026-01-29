package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;

import io.realm.RealmList;
import io.realm.RealmObject;

public class InvitedUserModel  extends RealmObject implements DiffIdentifier, ModelProtocol{
    @SerializedName("_id")
    @Expose
    private String id = "";
    @SerializedName("eventId")
    @Expose
    private String eventId = "";
    @SerializedName("userId")
    @Expose
    private String userId = "";
    @SerializedName("circleId")
    @Expose
    private String circleId = "";
    @SerializedName("inviteStatus")
    @Expose
    private String inviteStatus = "";
    @SerializedName("promoterStatus")
    @Expose
    private String promoterStatus = "";
    @SerializedName("first_name")
    @Expose
    private String firstName = "";
    @SerializedName("last_name")
    @Expose
    private String lastName = "";
    @SerializedName("email")
    @Expose
    private String email = "";
    @SerializedName("image")
    @Expose
    private String image = "";
    @SerializedName("isVip")
    @Expose
    private boolean isVip ;
    @SerializedName("follow")
    @Expose
    private String follow = "";
    @SerializedName("title")
    @Expose
    private String title = "";
    @SerializedName("avatar")
    @Expose
    private String avatar = "";
    @SerializedName("isDeleted")
    @Expose
    private boolean isDeleted;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("__v")
    @Expose
    private int v;
    @SerializedName("plusOneInvite")
    @Expose
    private RealmList<InvitedUserModel> plusOneInvite;
    @SerializedName("logs")
    @Expose
    private RealmList<LogsModel> logs;
    @SerializedName("isCancelAfterConfirm")
    @Expose
    private boolean isCancelAfterConfirm = false;

    private UserDetailModel userDetailModel = null;

    public String getCircleId() {
        return circleId;
    }
    public void setCircleId(String circleId) {
        this.circleId = circleId;
    }
    public boolean isDeleted() {
        return isDeleted;
    }
    public String getFullName() {
        return getFirstName().trim() + " " + getLastName().trim();
    }
    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
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

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }

    public String getId() {
        return Utils.notNullString(id);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getInviteStatus() {
        return inviteStatus;
    }

    public void setInviteStatus(String inviteStatus) {
        this.inviteStatus = inviteStatus;
    }

    public String getPromoterStatus() {
        return promoterStatus;
    }

    public void setPromoterStatus(String promoterStatus) {
        this.promoterStatus = promoterStatus;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isIsVip() {
        return isVip;
    }

    public void setIsVip(boolean isVip) {
        this.isVip = isVip;
    }

    public String getFollow() {
        return follow;
    }

    public void setFollow(String follow) {
        this.follow = follow;
    }
    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }

    public boolean isVip() {
        return isVip;
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }

    public String getTitle() {
        return Utils.notNullString(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAvatar() {
        return Utils.notNullString(avatar);
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserDetailModel getUserDetailModel() {
        return userDetailModel;
    }

    public void setUserDetailModel(UserDetailModel userDetailModel) {
        this.userDetailModel = userDetailModel;
    }

    public RealmList<InvitedUserModel> getPlusOneInvite() {
        return plusOneInvite;
    }

    public void setPlusOneInvite(RealmList<InvitedUserModel> plusOneInvite) {
        this.plusOneInvite = plusOneInvite;
    }

    public RealmList<LogsModel> getLogs() {
        return logs;
    }

    public void setLogs(RealmList<LogsModel> logs) {
        this.logs = logs;
    }


    public boolean isCancelAfterConfirm() {
        return isCancelAfterConfirm;
    }

    public void setCancelAfterConfirm(boolean cancelAfterConfirm) {
        isCancelAfterConfirm = cancelAfterConfirm;
    }
}
