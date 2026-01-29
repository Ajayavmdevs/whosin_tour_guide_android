package com.whosin.business.service.models.JuniperHotelModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.ModelProtocol;
import com.whosin.business.service.models.rayna.TourOptionsModel;

public class JPHotelPolicyRuleModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("From")
    @Expose
    public String from = "";
    @SerializedName("DateFrom")
    @Expose
    public String dateFrom = "";
    @SerializedName("DateFromHour")
    @Expose
    public String dateFromHour = "";
    @SerializedName("DateTo")
    @Expose
    public String dateTo = "";
    @SerializedName("DateToHour")
    @Expose
    public String dateToHour = "";
    @SerializedName("Type")
    @Expose
    public String type = "";
    @SerializedName("FixedPrice")
    @Expose
    public String fixedPrice = "";
    @SerializedName("PercentPrice")
    @Expose
    public String percentPrice = "";
    @SerializedName("Nights")
    @Expose
    public String nights = "";
    @SerializedName("ApplicationTypeNights")
    @Expose
    public String applicationTypeNights = "";

    public JPHotelPolicyRuleModel(TourOptionsModel t) {
        this.dateFrom = t.getDateFrom();
        this.dateTo = t.getDateTo();
        this.dateFromHour = t.getDateFromHour();
        this.dateToHour = t.getDateToHour();
        this.percentPrice = t.getPercentPrice();
    }

    public JPHotelPolicyRuleModel() {

    }



    public String getFrom() {
        return Utils.notNullString(from);
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getDateFrom() {
        return Utils.notNullString(dateFrom);
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateFromHour() {
        return Utils.notNullString(dateFromHour);
    }

    public void setDateFromHour(String dateFromHour) {
        this.dateFromHour = dateFromHour;
    }

    public String getType() {
        return Utils.notNullString(type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFixedPrice() {
        return Utils.notNullString(fixedPrice);
    }

    public void setFixedPrice(String fixedPrice) {
        this.fixedPrice = fixedPrice;
    }

    public String getPercentPrice() {
        return Utils.notNullString(percentPrice);
    }

    public void setPercentPrice(String percentPrice) {
        this.percentPrice = percentPrice;
    }

    public String getNights() {
        return Utils.notNullString(nights);
    }

    public void setNights(String nights) {
        this.nights = nights;
    }

    public String getApplicationTypeNights() {
        return Utils.notNullString(applicationTypeNights);
    }

    public void setApplicationTypeNights(String applicationTypeNights) {
        this.applicationTypeNights = applicationTypeNights;
    }

    public String getDateTo() {
        return Utils.notNullString(dateTo);
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public String getDateToHour() {
        return Utils.notNullString(dateToHour);
    }

    public void setDateToHour(String dateToHour) {
        this.dateToHour = dateToHour;
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
