package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

public class EventChatListModel extends RealmObject implements DiffIdentifier, ModelProtocol {

    @SerializedName("data")
    @Expose
    private RealmList<EventChatModel> data;

    @SerializedName("users")
    @Expose
    private RealmList<UserDetailModel> users;


    public RealmList<EventChatModel> getData() {
        if (data == null) {
            return new RealmList<>();
        }
        return data;
    }

    public void setData(RealmList<EventChatModel> data) {
        this.data = data;
    }

    public RealmList<UserDetailModel> getUsers() {
        if (users == null) {
            return new RealmList<>();
        }
        return users;
    }

    public void setUsers(RealmList<UserDetailModel> users) {
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
