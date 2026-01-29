package com.whosin.business.service.models.rayna;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.service.models.ModelProtocol;

public class RaynaOprationDaysModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("tourId")
    @Expose
    private int tourId;

    @SerializedName("tourOptionId")
    @Expose
    private int tourOptionId;

    @SerializedName("monday")
    @Expose
    private int monday = 0;

    @SerializedName("tuesday")
    @Expose
    private int tuesday = 0;

    @SerializedName("wednesday")
    @Expose
    private int wednesday = 0;

    @SerializedName("thursday")
    @Expose
    private int thursday = 0;

    @SerializedName("friday")
    @Expose
    private int friday = 0;

    @SerializedName("saturday")
    @Expose
    private int saturday = 0;

    @SerializedName("sunday")
    @Expose
    private int sunday = 0;


    public int getTourId() {
        return tourId;
    }

    public void setTourId(int tourId) {
        this.tourId = tourId;
    }

    public int getTourOptionId() {
        return tourOptionId;
    }

    public void setTourOptionId(int tourOptionId) {
        this.tourOptionId = tourOptionId;
    }

    public int getMonday() {
        return monday;
    }

    public void setMonday(int monday) {
        this.monday = monday;
    }

    public int getTuesday() {
        return tuesday;
    }

    public void setTuesday(int tuesday) {
        this.tuesday = tuesday;
    }

    public int getWednesday() {
        return wednesday;
    }

    public void setWednesday(int wednesday) {
        this.wednesday = wednesday;
    }

    public int getThursday() {
        return thursday;
    }

    public void setThursday(int thursday) {
        this.thursday = thursday;
    }

    public int getFriday() {
        return friday;
    }

    public void setFriday(int friday) {
        this.friday = friday;
    }

    public int getSaturday() {
        return saturday;
    }

    public void setSaturday(int saturday) {
        this.saturday = saturday;
    }

    public int getSunday() {
        return sunday;
    }

    public void setSunday(int sunday) {
        this.sunday = sunday;
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
