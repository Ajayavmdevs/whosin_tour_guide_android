package com.whosin.business.service.models.myCartModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.ModelProtocol;

public class MyCartCancellationModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("tourId")
    @Expose
    private int tourId = 0;

    @SerializedName("optionId")
    @Expose
    private Object optionId = 0;

    @SerializedName("fromDate")
    @Expose
    private String fromDate = "";

    @SerializedName("toDate")
    @Expose
    private String toDate = "";

    @SerializedName("percentage")
    @Expose
    private String percentage = "";

    @SerializedName("_id")
    @Expose
    private String _id = "";


    public int getTourId() {
        return tourId;
    }

    public void setTourId(int tourId) {
        this.tourId = tourId;
    }

    public Object getOptionId() {
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

    public String getPercentage() {
        return Utils.notNullString(percentage);
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String get_id() {
        return Utils.notNullString(_id);
    }

    public void set_id(String _id) {
        this._id = _id;
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
