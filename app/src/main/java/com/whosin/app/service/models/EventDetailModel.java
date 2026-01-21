package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

import java.util.ArrayList;

public class EventDetailModel implements DiffIdentifier,ModelProtocol {

    @SerializedName("event")
    @Expose
    private EventModel eventModel;

    @SerializedName("users")
    @Expose
    private ArrayList<ContactListModel> users;


    public EventModel getEventModel() {
        return eventModel;
    }

    public void setEventModel(EventModel eventModel) {
        this.eventModel = eventModel;
    }

    public ArrayList<ContactListModel> getUsers() {
        return users != null ? users : new ArrayList<>();
    }

    public void setUsers(ArrayList<ContactListModel> users) {
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
