package com.whosin.business.service.models.rayna;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.ModelProtocol;

public class RaynaBookingDateModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("startDate")
    @Expose
    private String startDate = "";

    @SerializedName("fromDate")
    @Expose
    private String fromDate = "";

    @SerializedName("toDate")
    @Expose
    private String toDate = "";

    @SerializedName("endDate")
    @Expose
    private String endDate = "";

    public RaynaBookingDateModel() {
    }

    public RaynaBookingDateModel(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getStartDate() {
        return Utils.notNullString(startDate);
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return Utils.notNullString(endDate);
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
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

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }
}
