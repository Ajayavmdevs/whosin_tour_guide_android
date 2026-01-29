package com.whosin.business.service.models.TravelDeskModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.ModelProtocol;

public class TravelDeskAvailabilityModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("date")
    @Expose
    private String date = "";

    @SerializedName("startTime")
    @Expose
    private int startTime;

    @SerializedName("endTime")
    @Expose
    private int endTime;
    @SerializedName("timeSlotId")
    @Expose
    private int timeSlotId;
    @SerializedName("left")
    @Expose
    private int left;
    @SerializedName("id")
    @Expose
    private int id;

    public TravelDeskAvailabilityModel(){

    }

    public TravelDeskAvailabilityModel(int startTime , int endTime,int timeSlotId){
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeSlotId = timeSlotId;
    }

    public String getDate() {
        return Utils.notNullString(date);
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public int getTimeSlotId() {
        return timeSlotId;
    }

    public void setTimeSlotId(int timeSlotId) {
        this.timeSlotId = timeSlotId;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
