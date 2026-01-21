package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;

import java.util.List;

public class PromoterCirclesModel implements DiffIdentifier,ModelProtocol{

    @SerializedName("_id")
    @Expose
    private String id = "";
    @SerializedName("userId")
    @Expose
    private String userId = "";
    @SerializedName("title")
    @Expose
    private String title = "";
    @SerializedName("avatar")
    @Expose
    private String avatar = "";
    @SerializedName("description")
    @Expose
    private String description = "";
    @SerializedName("createdAt")
    @Expose
    private String createdAt = "";

    @SerializedName("members")
    @Expose
    private List<UserDetailModel> members;

    @SerializedName("totalMembers")
    @Expose
    private int totalMembers = 0;


    public String getId() {
        return Utils.notNullString(id);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return Utils.notNullString(userId);
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAvatar() {
        return Utils.notNullString(avatar);
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDescription() {
        return Utils.notNullString(description);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return Utils.notNullString(createdAt);
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(int totalMembers) {
        this.totalMembers = totalMembers;
    }

    public String getTitle() {
        return Utils.notNullString(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<UserDetailModel> getMembers() {
        return members;
    }

    public void setMembers(List<UserDetailModel> members) {
        this.members = members;
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
