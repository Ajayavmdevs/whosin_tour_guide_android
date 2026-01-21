package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

import java.util.ArrayList;
import java.util.List;

public class PenaltyListModel implements DiffIdentifier, ModelProtocol{

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("dateTime")
    @Expose
    private String dateTime;
    @SerializedName("list")
    @Expose
    private List<PenaltyObjectModel> list;
    @SerializedName("complimentoryUser")
    @Expose
    private UserDetailModel complimentoryUser;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public List<PenaltyObjectModel> getList() {
        if (list == null) {
            return new ArrayList<>();
        }
        return list;
    }

    public void setList(List<PenaltyObjectModel> list) {
        this.list = list;
    }

    public UserDetailModel getComplimentoryUser() {
        return complimentoryUser;
    }

    public void setComplimentoryUser(UserDetailModel complimentoryUser) {
        this.complimentoryUser = complimentoryUser;
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
