package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;

public class ActivitySlotModel implements DiffIdentifier,ModelProtocol {


    @SerializedName("time")
    @Expose
    private String time = "";

    @SerializedName("totalSeats")
    @Expose
    private int totalSeats ;

    @SerializedName("isAvailable")
    private boolean isAvailable;


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }


    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
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
