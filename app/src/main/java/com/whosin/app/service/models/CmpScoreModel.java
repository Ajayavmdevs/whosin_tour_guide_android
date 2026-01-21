package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

public class CmpScoreModel implements DiffIdentifier,ModelProtocol {


    @SerializedName("punctuality")
    @Expose
    private double punctuality;
    @SerializedName("activity")
    @Expose
    private double activity;
    @SerializedName("value")
    @Expose
    private double value;

    public double getPunctuality() {
        return punctuality;
    }

    public void setPunctuality(double punctuality) {
        this.punctuality = punctuality;
    }

    public double getActivity() {
        return activity;
    }

    public void setActivity(double activity) {
        this.activity = activity;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
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
