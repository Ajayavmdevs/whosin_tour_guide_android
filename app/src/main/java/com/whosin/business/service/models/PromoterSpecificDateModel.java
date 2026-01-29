package com.whosin.business.service.models;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;

public class PromoterSpecificDateModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("date")
    @Expose
    private String date = "";

    @SerializedName("startTime")
    @Expose
    private String startTime = "";

    @SerializedName("endTime")
    @Expose
    private String endTime = "";

    public PromoterSpecificDateModel() {
    }

    public PromoterSpecificDateModel(String date, String startTime, String endTime) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getDate() {
        if (TextUtils.isEmpty(date)) date = "";
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        if (TextUtils.isEmpty(startTime)) startTime = "";
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        if (TextUtils.isEmpty(endTime)) endTime = "";
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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
