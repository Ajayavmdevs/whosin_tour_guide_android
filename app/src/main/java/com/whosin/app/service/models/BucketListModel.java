package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;

import java.util.ArrayList;
import java.util.List;

public class BucketListModel implements DiffIdentifier,ModelProtocol {


    @SerializedName("buckets")
    @Expose
    private List<CreateBucketListModel> bucketsModels;

    @SerializedName("outings")
    @Expose
    private List<InviteFriendModel> outingModels;

    @SerializedName("events")
    @Expose
    private List<BucketEventListModel> eventModels;


    @SerializedName("users")
    @Expose
    private List<ContactListModel> users;

    public void setBucketsModels(List<CreateBucketListModel> bucketsModels) {
        this.bucketsModels = bucketsModels;
    }

    public List<CreateBucketListModel> getBucketsModels() {
        return bucketsModels == null ? new ArrayList<>() : bucketsModels;
    }



    public void setBucketsModels(ArrayList<CreateBucketListModel> bucketsModels) {
        this.bucketsModels = bucketsModels;
    }

    public List<ContactListModel> getUsers() {
        if (users == null){
            return new ArrayList<>();
        }
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

    public List<InviteFriendModel> getOutingModels() {
        return outingModels == null ? new ArrayList<>() : outingModels;

    }

    public void setOutingModels(List<InviteFriendModel> outingModels) {
        this.outingModels = outingModels;
    }

    public List<BucketEventListModel> getEventModels() {
        return eventModels == null ? new ArrayList<>() : eventModels;
    }

    public void setEventModels(List<BucketEventListModel> eventModels) {
        this.eventModels = eventModels;
    }

}
