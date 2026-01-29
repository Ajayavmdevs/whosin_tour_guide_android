package com.whosin.business.service.models;

import com.whosin.business.comman.DiffIdentifier;

public class TimeSlotModel implements DiffIdentifier, ModelProtocol{
    private String date;
    private String startTime;
    private String endTime;

    public TimeSlotModel(String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public TimeSlotModel(String date,String startTime, String endTime) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }
}
