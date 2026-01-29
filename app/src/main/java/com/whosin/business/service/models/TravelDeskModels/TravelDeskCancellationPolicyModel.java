package com.whosin.business.service.models.TravelDeskModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.ModelProtocol;

public class TravelDeskCancellationPolicyModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("hours")
    @Expose
    private int hours;

    @SerializedName("percents")
    @Expose
    private int percents;

    @SerializedName("tourId")
    @Expose
    private int tourId;

    @SerializedName("optionId")
    @Expose
    private int optionId;

    @SerializedName("fromDate")
    @Expose
    private String fromDate;

    @SerializedName("toDate")
    @Expose
    private String toDate;

    @SerializedName("percentage")
    @Expose
    private int percentage;

    public int getTourId() {
        return tourId;
    }

    public void setTourId(int tourId) {
        this.tourId = tourId;
    }

    public int getOptionId() {
        return optionId;
    }

    public void setOptionId(int optionId) {
        this.optionId = optionId;
    }

    public String getFromDate() {
        return Utils.notNullString(fromDate);
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return Utils.notNullString(toDate);
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getPercents() {
        return percents;
    }

    public void setPercents(int percents) {
        this.percents = percents;
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
