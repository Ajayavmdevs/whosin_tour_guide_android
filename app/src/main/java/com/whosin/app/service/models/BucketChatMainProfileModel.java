package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

import java.util.List;

public class BucketChatMainProfileModel implements DiffIdentifier,ModelProtocol {

    @SerializedName("buckets")
    @Expose
    private List<BucketChatProfileModel> bucketsModels;

    @SerializedName("users")
    @Expose
    private List<ContactListModel> users;


    public List<BucketChatProfileModel> getBucketsModels() {
        return bucketsModels;
    }

    public void setBucketsModels(List<BucketChatProfileModel> bucketsModels) {
        this.bucketsModels = bucketsModels;
    }

    public List<ContactListModel> getUsers() {
        return users;
    }

    public void setUsers(List<ContactListModel> users) {
        this.users = users;
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
