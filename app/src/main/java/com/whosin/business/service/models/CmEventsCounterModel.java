package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;

public class CmEventsCounterModel implements DiffIdentifier,ModelProtocol{

    @SerializedName("eventsImIn")
    @Expose
    private int eventsImIn = 0;

    @SerializedName("speciallyForMe")
    @Expose
    private int speciallyForMe = 0;

    @SerializedName("imInterested")
    @Expose
    private int imInterested = 0;

    @SerializedName("myList")
    @Expose
    private int myList = 0;


    public int getEventsImIn() {
        return eventsImIn;
    }

    public void setEventsImIn(int eventsImIn) {
        this.eventsImIn = eventsImIn;
    }

    public int getSpeciallyForMe() {
        return speciallyForMe;
    }

    public void setSpeciallyForMe(int speciallyForMe) {
        this.speciallyForMe = speciallyForMe;
    }

    public int getImInterested() {
        return imInterested;
    }

    public void setImInterested(int imInterested) {
        this.imInterested = imInterested;
    }

    public int getMyList() {
        return myList;
    }

    public void setMyList(int myList) {
        this.myList = myList;
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
